package ru.mephi.nucleardroid.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Release(
    @SerialName("au")
    val au: String? = null,

    @SerialName("eu")
    val eu: String? = null,

    @SerialName("jp")
    val jp: String? = null,

    @SerialName("na")
    val na: String? = null,
)