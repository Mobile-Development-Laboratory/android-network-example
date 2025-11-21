package ru.mephi.nucleardroid.network.network.retrofit

import android.util.Log
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

class RetryInterceptor(
    private val maxRetries: Int = 3
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response: Response? = null
        var tryCount = 0
        var waitTime = 1000L

        while (tryCount < maxRetries) {
            try {
                response = chain.proceed(request)
                if (response.isSuccessful) return response
            } catch (_: Exception) { }

            tryCount++
            Thread.sleep(waitTime)
            waitTime *= 2
        }

        return response ?: throw RuntimeException("Request failed after $maxRetries retries")
    }
}

object RetrofitClient {
    private const val BASE_URL = "https://amiiboapi.com/api/"
    private const val TAG = "RetrofitClient"

    private val loggingInterceptor = HttpLoggingInterceptor { message ->
        Log.e(TAG, message)
    }.apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .addInterceptor(RetryInterceptor(3))
        .addInterceptor(loggingInterceptor)
        .build()

    val instance = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(Json.asConverterFactory("application/json; charset=UTF8".toMediaType()))
        .client(okHttpClient)
        .build()
}