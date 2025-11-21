package ru.mephi.nucleardroid.network.network.retrofit

import ru.mephi.nucleardroid.network.model.AmiiboResponse
import ru.mephi.nucleardroid.network.network.AmiiboService

class RetrofitService : AmiiboService {

    private val amiiboApi: RetrofitAmiiboApi = RetrofitClient.instance.create(RetrofitAmiiboApi::class.java)

    override suspend fun getAllAmiibos(): Result<AmiiboResponse> = runCatching {
        amiiboApi.getAllAmiibos()
    }

    override suspend fun getAmiibosByCharacter(character: String): Result<AmiiboResponse> = runCatching {
        amiiboApi.getAmiiboByCharacter(character)
    }
}