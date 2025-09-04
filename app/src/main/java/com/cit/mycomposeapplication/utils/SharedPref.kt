package com.cit.mycomposeapplication.utils

import android.content.Context
import android.content.SharedPreferences

object SharedPref {

    const val appPrefKey = "pref_key_quran"
    const val adFreePref = "purchased_premium_ad_free_plan"

//    const val isLanguageFinished = "isLanguageFinished"

    const val LANGUAGE_KEY = "language_key"
    const val hijri_correction = "hijri_correction"

    const val CALC_METHOD = "calc_method"
    const val ASR_METHOD = "asr_method"

    const val SELECTED_SCRIPT = "SELECTED_SCRIPT"

    const val SP_KEY_native_splash = "SP_KEY_native_splash"
    const val SP_KEY_ramadan_status = "SP_KEY_ramadan_status"

    const val downloaded = "downloaded"

    const val LAST_PAGE = "LAST_PAGE"
    const val LAST_SURAH = "LAST_SURAH"

    const val IS_FIRST_SESSION = "isfirst_session"

    const val SESSION_NO = "session_no"

    const val YOFFSET = "YOFFSET"
    const val SWQUALIFIER = "SWQUALIFIER"
    const val LAST_PAGE_NUMBER_USMANI = "last_page_number_usmani"
    const val LAST_PAGE_NUMBER_INDOPAK = "last_page_number_indopak"

    @JvmStatic
    fun Context.putPref(name: String, data: Any) {
        val sharedPreference = getPref()
        val editor = sharedPreference.edit()

        when (data) {
            is String -> editor.putString(name, data)
            is Int -> editor.putInt(name, data)
            is Float -> editor.putFloat(name, data)
            is Boolean -> editor.putBoolean(name, data)
        }

        editor.apply()
    }

    @JvmStatic
    fun Context.getPref(): SharedPreferences {
        return getSharedPreferences(appPrefKey, Context.MODE_PRIVATE)
    }

    @JvmStatic
    fun Context.getStringPref(name: String, defaultValue: String = ""): String =
        getPref().getString(name, defaultValue)
            ?: ""

    fun Context.getIntPref(name: String, defaultValue: Int = 0): Int =
        getPref().getInt(name, defaultValue)

    fun Context.getFloatPref(name: String, defaultValue: Float = 0f): Float =
        getPref().getFloat(name, defaultValue)

    fun Context.getBooleanPref(name: String, defaultValue: Boolean = false): Boolean =
        getPref().getBoolean(name, defaultValue)


    @JvmStatic
    fun Context.getPremiumPref(defaultValue: Boolean = false): Boolean =
        getPref().getBoolean(adFreePref, defaultValue)


    fun Context.removePrefs() {
        val sharedPreference = getSharedPreferences(appPrefKey, Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.clear()
        editor.apply()
    }


}