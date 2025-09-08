package com.cit.mycomposeapplication.repository

import android.app.Application
import android.util.Log
import com.cit.mycomposeapplication.data.remote.ApiService
import com.cit.mycomposeapplication.database.AyahUiState
import com.cit.mycomposeapplication.utils.TranslationHelper.getEnglishTranslationId
import com.cit.mycomposeapplication.utils.TranslationHelper.getLocaleTranslationId
import com.cit.mycomposeapplication.utils.TranslationHelper.getTranslationIdsForApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AyahRepository(application: Application) {

    // Fetch Ayah from API and store in RoomDB
    suspend fun getAyah(retries: Int = 3): AyahUiState? {
        return withContext(Dispatchers.IO) {
            val translationId = getTranslationIdsForApi()
            val response = ApiService.fetchAyah(translationId)

            Log.d("AyahRepository", "getAyah: (21) ${translationId}");
            Log.d("AyahRepository", "getAyah: (22) ${response}");

            response?.let { ayahResponse ->
                Log.d("AyahRepository", "getAyah: (25) ");
                val verse = ayahResponse.verse

                // Retry up to 3 times if verse is too long
                if (verse.textArabic.length > 200 && retries > 0) {
                    return@withContext getAyah(retries - 1) // Retry with reduced count
                }

                // Convert API Response to Room Entity
                val ayahEntity = AyahUiState(
                    verseKey = verse.verseKey,
                    textArabic = verse.textArabic,
                    translationEnglish = verse.translations.find { it.resourceId == 20 }?.text ?: "",
                    translatedByEnglish = getEnglishTranslationId() ?: "",
                    translationNative = verse.translations.find { it.resourceId != 20 }?.text ?: "",
                    translatedByNative = getLocaleTranslationId() ?: ""
                )

                return@withContext ayahEntity
            } ?: run {
                Log.d("AyahRepository", "getAyah: (44) ");

            }

            return@withContext null
        }
    }
}
