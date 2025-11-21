package ru.mephi.nucleardroid.network.network

import ru.mephi.nucleardroid.network.model.AmiiboResponse

interface AmiiboService {
    suspend fun getAllAmiibos(): Result<AmiiboResponse>
    suspend fun getAmiibosByCharacter(character: String): Result<AmiiboResponse>
}