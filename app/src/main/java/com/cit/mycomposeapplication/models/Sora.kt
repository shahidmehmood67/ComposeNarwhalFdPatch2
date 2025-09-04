package com.cit.mycomposeapplication.models

/**
 * Model class for sora info.
 *
 * @param name            sora name
 * @param name_english    sora name in english
 * @param ayahCount       all sora ayah count
 * @param startPageNumber sora start page number
 * @param jozaNumber      part number
 * @param places      Makki/Madani
 * @param tag Text tag
 */
data class Sora(
    var name: String,
    var name_english: String,
    var ayahCount: Int = 0,
    var startPageNumber: Int = 0,
    var jozaNumber: Int = 0,
    var places: Int = 0,
    var soraTag: String? = null
)

