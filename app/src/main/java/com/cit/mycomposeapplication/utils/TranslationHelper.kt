package com.cit.mycomposeapplication.utils

import android.content.Context
import com.cit.mycomposeapplication.QuranApplication
import com.cit.mycomposeapplication.R
import java.util.Locale

object TranslationHelper {
    private val translations = mapOf(
        "en" to Pair("57", R.string.english_transliteration),
        "de" to Pair("27", R.string.frank_bubenheim),
        "es" to Pair("199", R.string.noor_international),
        "fa" to Pair("135", R.string.islamhouse),
        "fr" to Pair("779", R.string.rashid_maash),
        "hi" to Pair("122", R.string.maulana_azizul_haque),
        "it" to Pair("153", R.string.hamza_roberto_piccardo),
        "tr" to Pair("77", R.string.diyanet_isleri),
        "ur" to Pair("54", R.string.maulana_muhammad_junagarhi),
        "bn" to Pair("162", R.string.bayaan_foundation),
        "ha" to Pair("32", R.string.abubakar_mahmoud_gumi),
        "id" to Pair("33", R.string.islamic_ministry),
        "ms" to Pair("39", R.string.abdullah_muhammad_basmeih),
        "pt" to Pair("103", R.string.helmi_nasr),
        "so" to Pair("46", R.string.mahmud_muhammad_abduh),
    )

    private val englishTranslations = mapOf(
        "20" to R.string.saheeh_international
    )

    // Function to get the English translation ID
    fun getEnglishTranslationId(defaultId: String = "20"): String {
        return englishTranslations.keys.find { it == defaultId } ?: defaultId
    }
    // Get translation ID based on locale
    fun getLocaleTranslationId(): String {
        val localeCode = Locale.getDefault().language
        return translations[localeCode]?.first ?: ""
    }

    // Get translation ID for the current device locale
    fun getTranslationIdsForApi(): String {
        val defaultId = getEnglishTranslationId()
        val translationId = getLocaleTranslationId()
        return if (translationId.isNotEmpty()) "$defaultId,$translationId" else defaultId
    }

    // Get string resource ID using translation ID
    fun getStringResourceIdByTranslationId(translationId: String): Int? {
        return translations.values.find { it.first == translationId }?.second
    }
    // Get string resource ID using English translation ID
    fun getEnglishStringResourceIdByTranslationId(translationId: String): Int? {
        return englishTranslations[translationId]
    }

    // Get actual translated string
    fun getNativeTranslater(translationId: String): String {
        val stringResId = getStringResourceIdByTranslationId(translationId)
        return stringResId?.let { QuranApplication.appContext.getString(it) }
            ?: ""
    }

    // Get actual translated string for English translation
    fun getEnglishTranslaterName(translationId: String): String {
        val stringResId = getEnglishStringResourceIdByTranslationId(translationId)
        return stringResId?.let { QuranApplication.appContext.getString(it) } ?: ""
    }


}
