package com.cit.mycomposeapplication.utils

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


// Core scaling function: returns Float
@Composable
private fun Number.scaledSdp(): Float {
    val sw = LocalConfiguration.current.smallestScreenWidthDp.toFloat()
    return this.toFloat() * sw / 300f
}

// Returns Dp (for Modifier.size, padding, etc.)
@Composable
fun Int.sdp(): Dp = this.scaledSdp().dp

@Composable
fun Float.sdp(): Dp = this.scaledSdp().dp

// Returns Sp (for fontSize)
@Composable
fun Int.ssp()  = this.scaledSdp().sp

@Composable
fun Float.ssp() = this.scaledSdp().sp
