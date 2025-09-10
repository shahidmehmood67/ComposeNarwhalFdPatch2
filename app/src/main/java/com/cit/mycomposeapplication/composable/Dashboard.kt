package com.cit.mycomposeapplication.composable

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.LocalActivity
import androidx.activity.enableEdgeToEdge
import androidx.annotation.ColorRes
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
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
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
import com.cit.mycomposeapplication.utils.ssp
import com.cit.mycomposeapplication.viewmodel.AyahViewModel
import com.cit.mycomposeapplication.viewmodel.LocationViewModel
import com.cit.mycomposeapplication.viewmodel.ReciterViewModel


val Montserrat = FontFamily(
    Font(R.font.montserrat_regular, weight = FontWeight.Normal),
    Font(R.font.montserratmedium, weight = FontWeight.Medium),
    Font(R.font.montserratsemibold, weight = FontWeight.SemiBold),
    Font(R.font.montserratbold, weight = FontWeight.Bold)
)

val Poppins = FontFamily(
    Font(R.font.poppinsregular, weight = FontWeight.Normal),
    Font(R.font.poppinsmedium, weight = FontWeight.Medium),
    Font(R.font.poppinssemibold, weight = FontWeight.SemiBold),
    Font(R.font.poppinsbold, weight = FontWeight.Bold)
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

    val context = LocalContext.current

    // Apply status bar color - Modern approach
    LaunchedEffect(uiState.statusBarColor) {
        uiState.statusBarColor?.let { color2 ->
            val activity = context as? ComponentActivity
            val color = color2.toArgb()
            Log.d("", "MainHeader: (112) color2 $color2");
            Log.d("", "MainHeader: (112) color $color");

            activity?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    it.window.statusBarColor = color
                    val controller = it.window.decorView.windowInsetsController
                    if (isColorLight(color)) {
                        controller?.setSystemBarsAppearance(
                            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                        )
                    } else {
                        controller?.setSystemBarsAppearance(
                            0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                        )
                    }
                } else {
                    // Fallback for API < 30
                    it.window.statusBarColor = color
                    @Suppress("DEPRECATION")
                    val flags = it.window.decorView.systemUiVisibility
                    it.window.decorView.systemUiVisibility = if (isColorLight(color)) {
                        flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    } else {
                        flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                    }
                }
            }
        }
    }


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
                .height(180.sdp())
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

                HeaderTopRow(locationText ?: "null",uiState, onSendMessage, onSendMessage , R.raw.crownanim)

                Spacer(modifier = Modifier.height(12.sdp()))


                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                ) {
                    // Next prayer
                    Text(
                        text = stringResource(id = R.string.next_prayer_is),
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.ssp(),
                        color = if (uiState.isBlack) Color.Black else Color.White
                    )

                    Text(
                        text = uiState.nextPrayerName,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 30.ssp(),
                        color = if (uiState.isBlack) Color.Black else Color.White
                    )

                    // Remaining time
                    Text(
                        text = stringResource(id = R.string.remaining_time),
                        color = if (uiState.isBlack) Color.Black else Color.White,
                        style = TextStyle(
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.ssp(),
                            lineHeight = 13.ssp(),
                            lineHeightStyle = LineHeightStyle(
                                alignment = LineHeightStyle.Alignment.Center,
                                trim = LineHeightStyle.Trim.Both // works like includeFontPadding=false
                            )
                        ),
                        modifier = Modifier.padding(start = 2.sdp())
                    )

                    Text(
                        text = uiState.remainingTime,
                        color = if (uiState.isBlack) Color.Black else Color.White,
                        style = LocalTextStyle.current.copy(
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.ssp(),
                            lineHeight = 13.ssp(),
                            lineHeightStyle = LineHeightStyle(
                                alignment = LineHeightStyle.Alignment.Center,
                                trim = LineHeightStyle.Trim.Both // removes extra top/bottom spacing (like includeFontPadding=false)
                            ),
                            shadow = Shadow(
                                color = BlackDim,
                                offset = Offset(0f, 0f),
                                blurRadius = 1f
                            )
                        ),
                        modifier = Modifier.padding(start = 2.sdp())
                    )
                }
            }
        }


        Spacer(modifier = Modifier.height(4.sdp()))

        AiPromptBar(
            onClick = onSendMessage
        )

//        Spacer(modifier = Modifier
//            .fillMaxWidth()
//            .height(3.sdp())
//            .background(White))


        // First Row of buttons
        ButtonRow(buttons = firstRowButtons,  rowBackgroundColor = Blue, onClick = onButtonClick)

        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(8.sdp())
            .background(White))

        // Second Row of buttons
        ButtonRow(buttons = secondRowButtons, rowBackgroundColor = Orange, onClick = onButtonClick)

        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(8.sdp())
            .background(White)
        )

        // Third Row of buttons
        ButtonRow(buttons = thirdRowButtons,rowBackgroundColor = Greenish_Yellow, onClick = onButtonClick)

        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(8.sdp())
            .background(White))

        AyahCardSection(ayahViewModel, onButtonClick)

        Spacer(modifier = Modifier.height(8.sdp()))

        TasbeehCardSection(ayahViewModel, onButtonClick)

        Spacer(modifier = Modifier.height(13.sdp()))

        ReciterSection(reciterViewModel, onReciterClick)


        // 🔹 Add bottom space
        Spacer(modifier = Modifier.height(24.dp)) // adjust (e.g., 16.dp, 24.dp, 48.dp)
    }
}

fun getColorHex(context: Context, @ColorRes colorRes: Int, includeAlpha: Boolean = true): String {
    val colorInt = ContextCompat.getColor(context, colorRes)
    return if (includeAlpha) {
        String.format("#%08X", colorInt) // ARGB (#AARRGGBB)
    } else {
        String.format("#%06X", 0xFFFFFF and colorInt) // RGB (#RRGGBB)
    }
}

// 4. Utility function to determine if color is light
fun isColorLight(color: Int): Boolean {
    val red = android.graphics.Color.red(color)
    val green = android.graphics.Color.green(color)
    val blue = android.graphics.Color.blue(color)

    // Calculate luminance using the relative luminance formula
    val luminance = (0.299 * red + 0.587 * green + 0.114 * blue) / 255
    return luminance > 0.5
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
            .padding(horizontal = 12.dp, vertical = 17.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Menu Button
        IconButton(
            onClick = onMenuClick,
            modifier = Modifier
                .size(width = 30.dp, height = 30.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_drawer),
                contentDescription = "Menu",
                tint = if (uiState.isBlack) Color.Black else Color.White
            )
        }


        Spacer(modifier = Modifier.width(22.sdp()))

        // Location Card
        Card(
            modifier = Modifier
                .weight(1f)
                .height(34.dp)
                .clickable { onLocationClick() },
            shape = RoundedCornerShape(25.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent) // or keep gradient in inner Box
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(
                        brush = uiState.backgroundResBrush ?: Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFE2C7CC), // fallback top color
                                Color(0x00000000)  // fallback bottom color (transparent)
                            )
                        ),
                        shape = RoundedCornerShape(35.dp)
                    )
                    .padding(start = 8.dp, end = 14.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_yellow_loc),
                        contentDescription = null,
                        modifier = Modifier.size(21.dp),
                        tint = if (uiState.isBlack) Color.Black else Color.White
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Log.d("", "HeaderTopRow: (314) locationText $locationText");

                    Text(
                        text = locationText ?: "Unknown",
                        color = if (uiState.isBlack) Color.Black else Color.White,
                        fontSize = 11.ssp(),
                        fontWeight = FontWeight.Normal,
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
            modifier = Modifier
                .size(width = 40.dp, height = 40.dp)
                .clickable { onLocationClick() },
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_remove_ads),
                contentDescription = "ads",
                tint = Color.Unspecified
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
                .fillMaxWidth(0.92f)
                .padding(vertical = 6.sdp())
                .clickable { onClick() }, // Main card click
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(2.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 6.sdp(), horizontal = 10.sdp())
            ) {
                Card(
                    shape = RoundedCornerShape(35.dp),
                    border = BorderStroke(1.dp, Green_1),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp), // card height
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
                            modifier = Modifier.size(22.sdp())
                        )
                    }
                }


                Spacer(modifier = Modifier.width(8.sdp()))

                // Send button with gradient (unchanged)
                Box(
                    modifier = Modifier
                        .size(48.sdp())
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
                        modifier = Modifier.size(21.sdp())
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
    rowBackgroundColor: Color = Color.Transparent, // new parameter for Row background
    onClick: (ButtonType) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(White) // apply Row background
            .padding(horizontal = 6.sdp()),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        buttons.forEach { btn ->
            Box(
                modifier = Modifier
                    .weight(1f) // equally divide width
                    .padding(horizontal = 4.dp)
                    .background(
                        color = White
                    )
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
            .background(White)
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Show actual image drawable
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = title,
            modifier = Modifier
                .size(70.sdp())
                .background(White.copy(alpha = 0.5f))
        )
//        Spacer(modifier = Modifier
//            .fillMaxWidth()
//            .height(4.dp)
//            .background(Yellow.copy(alpha = 0.5f))
//        )

        Text(
            text = title,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(
                fontSize = 10.ssp(),
                fontFamily = Montserrat,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                lineHeight = 12.ssp(),
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Center,
                    trim = LineHeightStyle.Trim.Both
                )
            ),
            modifier = Modifier
                .fillMaxWidth()
                .background(White.copy(alpha = 0.5f))
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
                    fontSize = 12.ssp(),
                    color = Color.Black
                )

                Text(
                    text = ayahUiState.textArabic,
                    fontFamily = majeedFont,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.ssp(),
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
                        fontSize = 11.ssp(),
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
                    fontSize = 12.ssp(),
                    color = Color.Black
                )

                Row(
                    modifier = Modifier
                        .padding(top = 10.sdp())
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.tasbeehfullsmall2),
                        contentDescription = null,
                        modifier = Modifier
                            .size(70.sdp())
                            .padding(start = 5.sdp())
                    )

                    Text(
                        text = tasbeehUiState,
                        fontFamily = majeedFont,
                        fontSize = 16.ssp(),
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
            .padding(top = 6.sdp())
            .background(
                color = WhiteGrey,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onButtonClick(buttontype) }
            .padding(horizontal = 7.sdp(), vertical = 6.sdp()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_viewmore),
            contentDescription = null,
            modifier = Modifier.size(18.sdp()),
            tint = Color.Unspecified
        )
        Text(
            text = text,
            fontFamily = Montserrat,
            fontWeight = FontWeight.Medium,
            fontSize = 9.ssp(),
            color = Color.Black,
            modifier = Modifier.padding(start = 3.sdp())
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
            fontSize = 12.ssp(),
            fontFamily = Montserrat,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 10.sdp(), top = 5.sdp())
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.sdp())
                .padding(top = 5.sdp()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
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
            .width(90.sdp())
            .height(80.sdp())
            .clickable { onClick(reciter.reader) }
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = reciter.imageRes),
            contentDescription = reciter.reader.readerNameEnglish,
            modifier = Modifier
                .size(50.sdp())
        )

        Text(
            text = reciter.reader.readerNameEnglish,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(
                fontSize = 10.ssp(),
                fontFamily = Montserrat,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                lineHeight = 11.ssp(),
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Center,
                    trim = LineHeightStyle.Trim.Both
                )
            ),
            modifier = Modifier
                .fillMaxWidth()
        )

    }
}










