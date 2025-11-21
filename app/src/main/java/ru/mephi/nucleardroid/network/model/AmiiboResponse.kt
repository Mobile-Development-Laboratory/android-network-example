package ru.mephi.nucleardroid.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AmiiboResponse(
    @SerialName("amiibo")
    val amiibo: List<Amiibo>,
)