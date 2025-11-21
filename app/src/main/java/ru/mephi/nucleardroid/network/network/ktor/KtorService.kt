package ru.mephi.nucleardroid.network.network.ktor

import ru.mephi.nucleardroid.network.model.AmiiboResponse
import ru.mephi.nucleardroid.network.network.AmiiboService

class KtorService : AmiiboService {

    override suspend fun getAllAmiibos(): Result<AmiiboResponse> =
        Result.failure(NotImplementedError())

    override suspend fun getAmiibosByCharacter(character: String): Result<AmiiboResponse> =
        Result.failure(NotImplementedError())
}