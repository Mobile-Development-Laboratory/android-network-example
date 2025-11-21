package ru.mephi.nucleardroid.network.network.ktor

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json

object KtorClient {

    private const val BASE_URL = "https://amiiboapi.com/api/"
    private const val TAG = "KtorClient"

    val instance = HttpClient(Android) {
        install(ContentNegotiation) {
            json()
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 10_000L
            connectTimeoutMillis = 10_000L
            socketTimeoutMillis = 10_000L
        }

        install(HttpRequestRetry) {
            maxRetries = 3
            exponentialDelay()
        }

        install(Logging) {
            level = LogLevel.INFO
            logger = object : Logger {
                override fun log(message: String) {
                    Log.e(TAG, message)
                }
            }
        }

        defaultRequest {
            url(BASE_URL)
        }
    }
}