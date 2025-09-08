package com.cit.mycomposeapplication.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Verse(
    val id: Int,
    @SerialName("verse_key") val verseKey: String,
    @SerialName("text_indopak") val textArabic: String,
    val translations: List<Translation>
)