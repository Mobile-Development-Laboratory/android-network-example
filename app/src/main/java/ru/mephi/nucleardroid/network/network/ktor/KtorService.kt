package ru.mephi.nucleardroid.network.network.ktor

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import ru.mephi.nucleardroid.network.model.AmiiboResponse
import ru.mephi.nucleardroid.network.network.AmiiboService

class KtorService : AmiiboService {

    private val client = KtorClient.instance

    override suspend fun getAllAmiibos(): Result<AmiiboResponse> = runCatching {
        client.get("amiibo/").body()
    }

    override suspend fun getAmiibosByCharacter(character: String): Result<AmiiboResponse> = runCatching {
        client.get("amiibo/") {
            parameter("character", character)
        }.body()
    }
}