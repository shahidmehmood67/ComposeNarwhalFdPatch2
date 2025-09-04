package com.cit.mycomposeapplication.models

/**
 * Model class for page
 * @param pageNumber Page number
 * @param jozaNumber Page part number
 * @param soraName Page sora name
 * @param hezbNumber Page hezb number
 * @param soraNameEnglish Sorah name in english
 */
data class Page(
    val pageNumber: Int,
    val pageNumberUsmani: Int,
    val pageNumberIndoPak: Int,
    val jozaNumber: Int,
    val hezbNumber: Int,
    val soraName: String,
    val soraNameEnglish: String,
    val soraId: Int,         // first surah on this page
    val ayaStart: Int       // first aya of that surah on this page
)

