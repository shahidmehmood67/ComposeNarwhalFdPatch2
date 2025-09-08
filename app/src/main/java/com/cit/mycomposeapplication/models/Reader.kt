package com.cit.mycomposeapplication.models

/**
 * Model class for Reader
 *
 * @param readerID Reader id
 * @param readerName Reader name
 * @param readerNameEnglish Reader name in english
 * @param audioType Audio type
 * @param downloadUrl Link to download or stream audio
 */
data class Reader(
    val readerID: Int,
    val readerName: String,
    val readerNameEnglish: String,
    val audioType: Int,
    val downloadUrl: String
)
