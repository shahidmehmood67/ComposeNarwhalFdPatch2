package com.cit.mycomposeapplication.repository

import com.cit.mycomposeapplication.models.AyaRect
import android.content.Context
import com.cit.mycomposeapplication.database.DatabaseAccess
import com.cit.mycomposeapplication.models.Aya
import java.io.File
import kotlin.collections.map

interface QuranRepository {
    suspend fun getPageAyat(pageNumber: Int): List<Aya>
    suspend fun getAyaRectsMapBoth(pageNumber:Int, soraNumber:Int, ayaNumber:Int): List<AyaRect>
    suspend fun getTouchedAya(pageNumber:Int, x:Float, y:Float): TouchedAyaResult?
    fun getQuranPageImageFileFromAssets(pageNo:Int, assetPath:String): File
}

data class TouchedAyaResult(
    val ayaID:Int,
    val suraID:Int,
    val pageNumber:Int,
    val ayaRects: List<AyaRect>?
)

class AndroidQuranRepository(private val context: Context) : QuranRepository {
    private val db by lazy { DatabaseAccess(context) } // using your existing DatabaseAccess

    override suspend fun getPageAyat(pageNumber: Int): List<Aya> {
        // DatabaseAccess.getPageAyat returns java objects - adapt accordingly
        val list = db.getPageAyat(pageNumber)
        return list?.map {
            Aya(ayaID = it.ayaID, pageNumber = it.pageNumber, suraID = it.suraID, text = it.text)
        } ?: emptyList()
    }

    override suspend fun getAyaRectsMapBoth(pageNumber: Int, soraNumber: Int, ayaNumber: Int): List<AyaRect> {
        return db.getAyaRectsMapBoth(pageNumber, soraNumber, ayaNumber) ?: emptyList()
    }

    override suspend fun getTouchedAya(pageNumber: Int, x: Float, y: Float): TouchedAyaResult? {
        val aya = db.getTouchedAya(pageNumber, x, y)
        return aya?.let {
            TouchedAyaResult(
                ayaID = it.ayaID,
                suraID = it.suraID,
                pageNumber = it.pageNumber,
                ayaRects = it.ayaRects // already AyaRect
            )
        }
    }

    override fun getQuranPageImageFileFromAssets(pageNo: Int, assetPath: String): File {
        val pageFileName = when {
            pageNo > 99 -> "page$pageNo.png"
            pageNo in 10..99 -> "page0$pageNo.png"
            else -> "page00$pageNo.png"
        }
        return File(assetPath, pageFileName)
    }
}
