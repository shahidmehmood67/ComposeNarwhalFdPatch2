package com.cit.mycomposeapplication.utils

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.TextUnit

// --- Scale Dp like SDP ---
@Composable
fun Int.sdp(): Dp {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    // baseline = 360dp width (like many designs)
    Log.d("ScalesExt", "sdp: (17) ${(this * (screenWidth / 360f)).dp}");
    return (this * (screenWidth / 360f)).dp
}

// --- Scale Sp like SSP ---
@Composable
fun Int.ssp(): TextUnit {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    // baseline = 360dp width
    Log.d("ScalesExt", "ssp: (27) ${(this * (screenWidth / 360f)).sp}");
    return (this * (screenWidth / 360f)).sp
}
