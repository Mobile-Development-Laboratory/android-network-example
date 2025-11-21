package ru.mephi.nucleardroid.network.network.http

import kotlinx.serialization.json.Json
import ru.mephi.nucleardroid.network.model.AmiiboResponse
import ru.mephi.nucleardroid.network.network.AmiiboService
import java.net.HttpURLConnection
import java.net.URL

class HttpUrlConnectionService : AmiiboService {

    companion object {
        private const val BASE_URL = "https://amiiboapi.com/api"
    }

    override suspend fun getAllAmiibos(): Result<AmiiboResponse> {
        return try {
            val url = URL("$BASE_URL/amiibo/")
            val con = url.openConnection() as HttpURLConnection

            con.apply {
                requestMethod = "GET"
                connectTimeout = 10_000
                readTimeout = 10_000
            }

            if (con.responseCode == HttpURLConnection.HTTP_OK) {
                val `in` = con.inputStream
                val reponse = `in`.bufferedReader().use { it.readText() }

                val amiiboReponse = Json.decodeFromString<AmiiboResponse>(reponse)
                Result.success(amiiboReponse)
            } else {
                val `in` = con.errorStream
                val error = `in`.bufferedReader().use { it.readText()  }
                Result.failure(Exception("HTTP error: ${con.responseCode}, ${error}"))
            }
        } catch (th: Throwable) {
            Result.failure(th)
        }
    }

    override suspend fun getAmiibosByCharacter(character: String): Result<AmiiboResponse> {
        return try {
            val url = URL("$BASE_URL/amiibo/?character=$character")
            val con = url.openConnection() as HttpURLConnection

            con.apply {
                requestMethod = "GET"
                connectTimeout = 10_000
                readTimeout = 10_000
            }

            if (con.responseCode == HttpURLConnection.HTTP_OK) {
                val `in` = con.inputStream
                val reponse = `in`.bufferedReader().use { it.readText() }

                val amiiboReponse = Json.decodeFromString<AmiiboResponse>(reponse)
                Result.success(amiiboReponse)
            } else {
                val `in` = con.errorStream
                val error = `in`.bufferedReader().use { it.readText()  }
                Result.failure(Exception("HTTP error: ${con.responseCode}, ${error}"))
            }
        } catch (th: Throwable) {
            Result.failure(th)
        }
    }
}