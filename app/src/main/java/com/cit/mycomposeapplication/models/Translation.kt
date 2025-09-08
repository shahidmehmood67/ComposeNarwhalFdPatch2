package com.cit.mycomposeapplication.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Translation(
    val text: String,
    @SerialName("resource_id") val resourceId: Int
)
