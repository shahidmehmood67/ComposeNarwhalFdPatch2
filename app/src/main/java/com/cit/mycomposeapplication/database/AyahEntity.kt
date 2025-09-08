package com.cit.mycomposeapplication.database


//data class AyahEntity(
//    val id: Int = 0,
//    val verseKey: String,
//    val textArabic: String,
//    val translationEnglish: String,
//    val translatedByEnglish: String,
//    val translationNative: String,
//    val translatedByNative: String
//)

data class AyahUiState(
    val verseKey: String = "",
    val textArabic: String = "",
    val translationEnglish: String = "",
    val translatedByEnglish: String = "",
    val translationNative: String = "",
    val translatedByNative: String = ""
)

