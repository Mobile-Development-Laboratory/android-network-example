package ru.mephi.nucleardroid.network.network.http

import ru.mephi.nucleardroid.network.model.AmiiboResponse
import ru.mephi.nucleardroid.network.network.AmiiboService

class HttpUrlConnectionService : AmiiboService {

    override suspend fun getAllAmiibos(): Result<AmiiboResponse> =
        Result.failure(NotImplementedError())

    override suspend fun getAmiibosByCharacter(character: String): Result<AmiiboResponse> =
        Result.failure(NotImplementedError())
}