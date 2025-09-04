package com.cit.mycomposeapplication.models


/**
 * Model class for Quarter
 *
 * @param soraName Sora name
 * @param soraid Sora ID
 * @param startPageNumber Start page number
 * @param HezbNumber Quarter number
 * @param partNumber Part number
 * @param firstVerseText First verse text
 */
data class Quarter(
    var soraName: String,
    var soraNameEnglish: String,
    var soraid: Int,
    var startPageNumber: Int,
    var hezbNumber: Int,
    var partNumber: Int,
    var firstVerseText: String,
    var ayaFirstNumber: Int,
    var counter: Int,
    var joza: Int
)
