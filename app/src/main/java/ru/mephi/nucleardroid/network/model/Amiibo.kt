
package ru.mephi.nucleardroid.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Amiibo(
    @SerialName("amiiboSeries")
    val amiiboSeries: String,

    @SerialName("character")
    val character: String,

    @SerialName("gameSeries")
    val gameSeries: String,

    @SerialName("head")
    val head: String,

    @SerialName("image")
    val image: String,

    @SerialName("name")
    val name: String,

    @SerialName("release")
    val release: Release,

    @SerialName("tail")
    val tail: String,

    @SerialName("type")
    val type: String,
)