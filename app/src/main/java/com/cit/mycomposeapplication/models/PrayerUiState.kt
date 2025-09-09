package com.cit.mycomposeapplication.models

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

data class PrayerUiState(
    val prayers: List<Prayer> = emptyList(),        // All daily prayers
    val currentPrayerName: String = "",             // e.g. "Asr"
    val currentPrayerTime: String = "",             // e.g. "04:25 PM"
    val nextPrayerName: String = "",                // e.g. "Maghrib"
    val nextPrayerTime: String = "",                // e.g. "06:45 PM"
    val remainingTime: String = "00:00",            // countdown
    val isBlack: Boolean = true,                    // for UI theme switching
    val statusBarColor: Color? = null,                // resolved color resource
    val backgroundResBrush: Brush? = null,                 // resolved drawable background
    val toolbarRes: Int? = null,                    // resolved drawable toolbar
    val vectorRes: Int? = null                      // resolved vector drawable
)


