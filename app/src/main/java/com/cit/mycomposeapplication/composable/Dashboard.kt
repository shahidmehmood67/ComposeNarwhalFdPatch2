package com.cit.mycomposeapplication.composable

import android.util.Log
import androidx.annotation.DrawableRes
import com.cit.mycomposeapplication.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cit.mycomposeapplication.models.ButtonData
import com.cit.mycomposeapplication.models.ButtonType
import com.cit.mycomposeapplication.models.PrayerUiState
import com.cit.mycomposeapplication.viewmodel.LocationViewModel

@Composable
fun MainHeader(
    locationViewModel: LocationViewModel = viewModel(),
    onButtonClick: (ButtonType) -> Unit
) {
    val uiState by locationViewModel.uiState.observeAsState(PrayerUiState())
    val locationText by locationViewModel.locationLiveData.observeAsState("")
    Log.d("Dashboard", "MainHeader: (303) locationText $locationText");

    val firstRowButtons  = listOf(
        ButtonData(ButtonType.ALQURAN, R.drawable.iconquran, "Al-Quran"),
        ButtonData(ButtonType.QIBLA, R.drawable.iconqibla, "Qibla Finder"),
        ButtonData(ButtonType.TASBEEH, R.drawable.icontasbeh, "Digital Tasbeeh")
    )

    val secondRowButtons  = listOf(
        ButtonData(ButtonType.AZKAR, R.drawable.icondua, "Azkar"),
        ButtonData(ButtonType.PRAYERS, R.drawable.iconprayertime, "Prayer Times"),
        ButtonData(ButtonType.CALENDAR, R.drawable.iconcalendar, "Hujri Calendar")
    )

    val thirdRowButtons = listOf(
        ButtonData(ButtonType.HAJJ_UMRAH, R.drawable.hajjumrah_round, "Hajj and Umrah Section"),
        ButtonData(ButtonType.MASJID, R.drawable.iconmasjid, "Masjid Finder"),
        ButtonData(ButtonType.NAMES_NABI, R.drawable._99names, "99 Names")
    )


    // Scrollable container
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .background(
                brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Transparent) // fallback
                    )
            )
    ) {
        // Header Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = uiState.backgroundResBrush
                    ?: Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Transparent)))
                .height(200.dp)
        ) {
            // Your header content goes here (location, next prayer, countdown, etc.)
            // 🔹 Toolbar / top vector
            uiState.vectorRes?.let { vecRes ->
                Image(
                    painter = painterResource(id = vecRes),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxHeight(0.5f)   // 50% of Box height
                        .fillMaxWidth(0.55f)    // 50% of Box width
                        .align(Alignment.BottomEnd),  // right corner
                    contentScale = ContentScale.Fit
                )
            }

            /*// 🔹 Background image (WEBP or PNG/JPG)
            uiState.toolbarRes?.let { res ->
                Image(
                    painter = painterResource(id = res),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }*/




            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 🕌 Location
                Text(
                    text = locationText ?: "null",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (uiState.isBlack) Color.Black else Color.White
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 🙏 Next prayer
                Text(
                    text = stringResource(id = R.string.next_prayer_is),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (uiState.isBlack) Color.Black else Color.White
                    )
                )

                Text(
                    text = uiState.nextPrayerName,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (uiState.isBlack) Color.Black else Color.White
                    )
                )

                Text(
                    text = uiState.nextPrayerTime,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = if (uiState.isBlack) Color.Black else Color.White
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                // ⏳ Remaining time
                Text(
                    text = stringResource(id = R.string.remaining_time),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (uiState.isBlack) Color.Black else Color.White
                    )
                )

                Text(
                    text = uiState.remainingTime,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (uiState.isBlack) Color.Black else Color.White
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // ⚪ Dots
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_dot_active_2),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.ic_dot),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color.Unspecified
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // First Row of buttons
        ButtonRow(buttons = firstRowButtons, onClick = onButtonClick)

        Spacer(modifier = Modifier.height(12.dp))

        // Second Row of buttons
        ButtonRow(buttons = secondRowButtons, onClick = onButtonClick)

        Spacer(modifier = Modifier.height(12.dp))

        // Third Row of buttons
        ButtonRow(buttons = thirdRowButtons, onClick = onButtonClick)
    }
}


@Composable
fun ButtonRow(
    buttons: List<ButtonData>,
    modifier: Modifier = Modifier,
    onClick: (ButtonType) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        buttons.forEach { btn ->
            Box(
                modifier = Modifier
                    .weight(1f) // equally divide width
                    .padding(horizontal = 4.dp)
            ) {
                MainButton(
                    title = btn.title,
                    imageRes = btn.imageRes,
                    onClick = { onClick(btn.type) }
                )
            }
        }
    }
}

@Composable
fun MainButton(
    title: String,
    @DrawableRes imageRes: Int,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Show actual image drawable
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = title,
            modifier = Modifier.size(70.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = title,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            fontSize = 12.sp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}







