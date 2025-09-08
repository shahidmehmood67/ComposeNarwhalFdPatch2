package com.cit.mycomposeapplication.models

import androidx.annotation.DrawableRes

data class ButtonData(
    val type: ButtonType,
    @DrawableRes val imageRes: Int,
    val title: String
)

enum class ButtonType {
    ALQURAN, QIBLA, TASBEEH,
    AZKAR, PRAYERS, CALENDAR,
    HAJJ_UMRAH, MASJID, NAMES_NABI,

    CARD_AYAH, CARD_TASBEEH
}
