package com.cit.mycomposeapplication.composable

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import com.cit.mycomposeapplication.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cit.mycomposeapplication.database.AyahUiState
import com.cit.mycomposeapplication.models.ButtonData
import com.cit.mycomposeapplication.models.ButtonType
import com.cit.mycomposeapplication.models.PrayerUiState
import com.cit.mycomposeapplication.models.Reader
import com.cit.mycomposeapplication.models.ReciterUiModel
import com.cit.mycomposeapplication.ui.theme.*
import com.cit.mycomposeapplication.ui.theme.PrimaryDarkGreen
import com.cit.mycomposeapplication.utils.HtmlText
import com.cit.mycomposeapplication.utils.sdp
import com.cit.mycomposeapplication.viewmodel.AyahViewModel
import com.cit.mycomposeapplication.viewmodel.LocationViewModel
import com.cit.mycomposeapplication.viewmodel.ReciterViewModel


val Montserrat = FontFamily(
    Font(R.font.montserrat_regular, weight = FontWeight.Normal),
    Font(R.font.montserratmedium, weight = FontWeight.Medium),
    Font(R.font.montserratsemibold, weight = FontWeight.SemiBold),
    Font(R.font.montserratbold, weight = FontWeight.Bold)
)

val majeedFont = FontFamily(
    Font(R.font.majeedfont, weight = FontWeight.Normal)
)


@Composable
fun MainHeader(
    locationViewModel: LocationViewModel = viewModel(),
    ayahViewModel: AyahViewModel = viewModel(),
    reciterViewModel: ReciterViewModel = viewModel(),
    onButtonClick: (ButtonType) -> Unit,
    onReciterClick: (Reader) -> Unit,
    onSendMessage: () -> Unit,
) {
    val uiState by locationViewModel.uiState.observeAsState(PrayerUiState())
    val locationText by locationViewModel.locationLiveData.observeAsState("")
    Log.d("Dashboard", "MainHeader: (303) locationText $locationText");

    val firstRowButtons = listOf(
        ButtonData(ButtonType.ALQURAN, R.drawable.iconquran, "Al-Quran"),
        ButtonData(ButtonType.QIBLA, R.drawable.iconqibla, "Qibla Finder"),
        ButtonData(ButtonType.TASBEEH, R.drawable.icontasbeh, "Digital Tasbeeh")
    )

    val secondRowButtons = listOf(
        ButtonData(ButtonType.AZKAR, R.drawable.icondua, "Azkar"),
        ButtonData(ButtonType.PRAYERS, R.drawable.iconprayertime, "Prayer Times"),
        ButtonData(ButtonType.CALENDAR, R.drawable.iconcalendar, "Hijri Calendar")
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
                color = Color.White,
            )
    ) {
        // Header Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {

            // 🔹 Background image (WEBP or PNG/JPG)
            uiState.toolbarRes?.let { res ->
                Image(
                    painter = painterResource(id = res),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Your header content goes here (location, next prayer, countdown, etc.)
            // 🔹 Toolbar / top vector
            uiState.vectorRes?.let { vecRes ->
                Image(
                    painter = painterResource(id = vecRes),
                    contentDescription = null,
                    modifier = Modifier
                        .width(170.dp)               // fixed width like XML
                        .height(95.dp)               // fixed height like XML
                        .align(Alignment.BottomEnd),
                    contentScale = ContentScale.Fit
                )

            }

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
//                 Location
//                Text(
//                    text = locationText ?: "null",
//                    style = MaterialTheme.typography.bodyMedium.copy(
//                        fontWeight = FontWeight.Bold,
//                        color = if (uiState.isBlack) Color.Black else Color.White
//                    )
//                )

                HeaderTopRow(locationText ?: "null",uiState, onSendMessage, onSendMessage , R.raw.crownanim)


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

            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        AiPromptBar(
            onClick = onSendMessage
        )

        Spacer(modifier = Modifier.height(10.dp))

        // First Row of buttons
        ButtonRow(buttons = firstRowButtons, onClick = onButtonClick)

        Spacer(modifier = Modifier.height(12.dp))

        // Second Row of buttons
        ButtonRow(buttons = secondRowButtons, onClick = onButtonClick)

        Spacer(modifier = Modifier.height(12.dp))

        // Third Row of buttons
        ButtonRow(buttons = thirdRowButtons, onClick = onButtonClick)

        Spacer(modifier = Modifier.height(10.dp))

        AyahCardSection(ayahViewModel, onButtonClick)

        Spacer(modifier = Modifier.height(10.dp))

        TasbeehCardSection(ayahViewModel, onButtonClick)

        Spacer(modifier = Modifier.height(12.dp))

        ReciterSection(reciterViewModel, onReciterClick)



        // 🔹 Add bottom space
        Spacer(modifier = Modifier.height(24.dp)) // adjust (e.g., 16.dp, 24.dp, 48.dp)
    }
}

@Composable
fun HeaderTopRow(
    locationText: String?,
    uiState: PrayerUiState,
    onMenuClick: () -> Unit,
    onLocationClick: () -> Unit,
    lottieRes: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Menu Button
        IconButton(
            onClick = onMenuClick,
            modifier = Modifier.size(width = 60.dp, height = 60.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_drawer),
                contentDescription = "Menu",
                tint = if (uiState.isBlack) Color.Black else Color.White
            )
        }


        Spacer(modifier = Modifier.width(16.dp))

        // Location Card
        Card(
            modifier = Modifier
                .weight(1f)
                .height(35.dp)
                .clickable { onLocationClick() },
            shape = RoundedCornerShape(25.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent) // or keep gradient in inner Box
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = uiState.backgroundResBrush ?: Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFE2C7CC), // fallback top color
                                Color(0x00000000)  // fallback bottom color (transparent)
                            )
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_yellow_loc),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = if (uiState.isBlack) Color.Black else Color.White
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = locationText ?: "Unknown",
                        color = if (uiState.isBlack) Color.Black else Color.White,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

//        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.my_animation))
//
//
//        // Lottie Animation
//        LottieAnimation(
//            composition = rememberLottieComposition(LottieCompositionSpec.RawRes(lottieRes)).value,
//            iterations = LottieConstants.IterateForever,
//            modifier = Modifier.size(50.dp)
//        )


        // Menu Button
        IconButton(
            onClick = onMenuClick,
            modifier = Modifier.size(width = 40.dp, height = 55.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_remove_ads),
                contentDescription = "ads",
            )
        }
    }
}




@Composable
fun AiPromptBar(
    message: String = stringResource(R.string.chat_with_desc),
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(vertical = 6.dp)
                .clickable { onClick() }, // Main card click
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(5.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(35.dp),
                    border = BorderStroke(1.dp, PrimaryDarkGreen),
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp), // card height
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White // 🔹 White background
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically, // vertically center content
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp)
                    ) {
                        // Text
                        Text(
                            text = message,
                            fontSize = 14.sp,
                            color = Color.Black,
                            modifier = Modifier.weight(1f) // take remaining space
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Voice Icon
                        Icon(
                            painter = painterResource(id = R.drawable.ic_voice),
                            contentDescription = "Voice",
                            tint = PrimaryDarkGreen,
                            modifier = Modifier.size(25.dp)
                        )
                    }
                }


                Spacer(modifier = Modifier.width(8.dp))

                // Send button with gradient (unchanged)
                Box(
                    modifier = Modifier
                        .size(55.dp)
                        .clip(CircleShape)
                        .background(
                            color = PrimaryDarkGreen
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_send),
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(25.dp)
                    )
                }
            }
        }
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
            modifier = Modifier.size(75.sdp())
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = title,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            fontSize = 12.sp,
            fontFamily = Montserrat,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun AyahCardSection(
    viewModel: AyahViewModel = viewModel(),
    onButtonClick: (ButtonType) -> Unit
) {
    val ayah by viewModel.ayahLiveData.observeAsState()

    ayah?.let {
        AyatOfTheDayCard(
            ayahUiState = it,
            onButtonClick = onButtonClick
        )
    }
}

@Composable
fun TasbeehCardSection(
    viewModel: AyahViewModel = viewModel(),
    onButtonClick: (ButtonType) -> Unit
) {
    val tasbeeh by viewModel.tasbeehOfDay.observeAsState()

    tasbeeh?.let {
        TasbeehOfTheDayCard(
            tasbeehUiState = it,
            onButtonClick = onButtonClick
        )
    }
}


@Composable
fun AyatOfTheDayCard(
    ayahUiState: AyahUiState, // from VM
    onButtonClick: (ButtonType) -> Unit
) {
    Log.d("", "AyatOfTheDayCard: (315) ");
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(top = 13.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(5.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White // 🔹 White background
            )
        ) {
            Column(
                modifier = Modifier.padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.ayatofday),
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = Color.Black
                )

                Text(
                    text = ayahUiState.textArabic,
                    fontFamily = majeedFont,
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp,
                    color = PrimaryDarkGreen,
                    modifier = Modifier
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                        .fillMaxWidth(),
                    style = TextStyle(
                        textDirection = TextDirection.Rtl
                    )
                )

                ayahUiState.translationEnglish.HtmlText(
                    modifier = Modifier
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .fillMaxWidth(),
                    style = TextStyle(
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = Color.Black
                    )
                )


                ViewMoreButton(
                    text = stringResource(id = R.string.see_translation),
                    onButtonClick = onButtonClick,
                    ButtonType.CARD_AYAH
                )
            }
        }
    }
}

@Composable
fun TasbeehOfTheDayCard(
    tasbeehUiState: String,
    onButtonClick: (ButtonType) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(top = 13.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(5.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White // 🔹 White background
            )
        ) {
            Column(
                modifier = Modifier.padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.tasbeehofday),
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = Color.Black
                )

                Row(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.tasbeehfullsmall2),
                        contentDescription = null,
                        modifier = Modifier
                            .size(70.sdp())
                            .padding(start = 5.dp)
                    )

                    Text(
                        text = tasbeehUiState,
                        fontFamily = majeedFont,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        color = PrimaryDarkGreen,
                        textAlign = TextAlign.End,              // align to end of its full width
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)                         // 👈 fills all remaining width
                            .padding(start = 10.dp, end = 10.dp) // keep spacing from image & right edge
                    )
                }

                ViewMoreButton(
                    text = stringResource(id = R.string.viewmore),
                    onButtonClick = onButtonClick,
                    ButtonType.CARD_TASBEEH
                )
            }
        }
    }
}

@Composable
fun ViewMoreButton(
    text: String,
    onButtonClick: (ButtonType) -> Unit,
    buttontype: ButtonType
) {
    Row(
        modifier = Modifier
            .padding(top = 6.dp)
            .background(
                color = WhiteGrey,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onButtonClick(buttontype) }
            .padding(horizontal = 7.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_viewmore),
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = Color.Unspecified
        )
        Text(
            text = text,
            fontFamily = Montserrat,
            fontWeight = FontWeight.Medium,
            fontSize = 9.sp,
            color = Color.Black,
            modifier = Modifier.padding(start = 3.dp)
        )
    }
}

@Composable
fun ReciterSection(
    viewModel: ReciterViewModel = viewModel(),
    onReciterClick: (Reader) -> Unit
) {
    val reciters by viewModel.reciters.observeAsState(emptyList())

    Column(
        modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Reciters:",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 10.dp, top = 5.dp)
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .padding(top = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 10.dp)
        ) {
            items(reciters) { reciter ->
                ReciterCard(reciter) { onReciterClick(it) }
            }
        }
    }
}

@Composable
fun ReciterCard(
    reciter: ReciterUiModel,
    onClick: (Reader) -> Unit
) {
    Column(
        modifier = Modifier
            .width(90.dp)
            .height(125.dp)
            .clickable { onClick(reciter.reader) }
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = reciter.imageRes),
            contentDescription = reciter.reader.readerNameEnglish,
            modifier = Modifier
                .size(75.sdp())
        )

        val density = LocalDensity.current
        val maxHeight = with(density) { (14.sp.toPx() * 2).toDp() } // 2 lines × lineHeight

        Text(
            text = reciter.reader.readerNameEnglish,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            lineHeight = 14.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .heightIn(max = maxHeight)
        )
    }
}










