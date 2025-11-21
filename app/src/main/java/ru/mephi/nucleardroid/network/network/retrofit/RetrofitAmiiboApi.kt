package ru.mephi.nucleardroid.network.network.retrofit

import retrofit2.http.GET
import retrofit2.http.Query
import ru.mephi.nucleardroid.network.model.AmiiboResponse

interface RetrofitAmiiboApi {
    @GET("amiibo/")
    suspend fun getAllAmiibos(): AmiiboResponse

    @GET("amiibo/")
    suspend fun getAmiiboByCharacter(@Query("character") character: String): AmiiboResponse
}