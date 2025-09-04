package com.cit.mycomposeapplication.models


data class HighlightCommand(
    val sora: Int,
    val aya: Int,
    val page: Int
)

sealed class AudioCommand {
    data class PlaySingle(val ayaId: Int, val pageId: Int, val suraId: Int, val readerId:Int, val downloadLink:String?): AudioCommand()
    data class PlayFrom(val startingAyaPosition: Int, val pageId:Int, val readerId:Int, val downloadLink:String?): AudioCommand()
}
