package com.cit.mycomposeapplication.composable

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.cit.mycomposeapplication.MainActivity
import com.cit.mycomposeapplication.models.AudioCommand
import com.cit.mycomposeapplication.models.AyaRect
import com.cit.mycomposeapplication.models.HighlightCommand
import com.cit.mycomposeapplication.viewmodel.QuranViewModel
import kotlinx.coroutines.launch


@Composable
fun QuranReaderApp(
    viewModel: QuranViewModel,
    pageCount: Int = 5,
    nightMode: Boolean = false,
    onAudioCommand: (Any) -> Unit
) {
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { pageCount }
    )
    val scope = rememberCoroutineScope()

    Surface(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            val pageNumber = pageIndex + 1
            PageScreen(
                pageNo = pageNumber,
                viewModel = viewModel,
                nightMode = nightMode,
                onAudioCommand = onAudioCommand
            )
        }

        LaunchedEffect(pagerState.currentPage) {
            viewModel.setCurrentPage(pagerState.currentPage)
        }
    }
}




@Composable
fun PageScreen(
    pageNo: Int,
    viewModel: QuranViewModel,
    nightMode: Boolean,
    onAudioCommand: (Any) -> Unit
) {
    val context = LocalContext.current
    Log.d("readerScreens", "(83) PageScreen() called with: pageNo = $pageNo, viewModel = $viewModel, nightMode = $nightMode, onAudioCommand = $onAudioCommand");
    // trigger load
    LaunchedEffect(pageNo) {
        viewModel.loadBitmapForPage(pageNo)
    }

    val bitmaps by viewModel.pageBitmapStates.collectAsState()
    val bitmap = bitmaps[pageNo] // may be null while loading

    // rectangles to draw (image coordinate space)
    var rects by remember { mutableStateOf<List<AyaRect>>(emptyList()) }
    var showPopup by remember { mutableStateOf(false) }
    var popupOffset by remember { mutableStateOf(androidx.compose.ui.geometry.Offset.Zero) }
    var popupAnchorSize by remember { mutableStateOf(IntSize.Zero) }

    // listening for highlight commands from VM (when activity or other user triggers)
    val highlightFlow = viewModel.highlightCommands
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        highlightFlow.collect { cmd: HighlightCommand ->
            // only react if same page — mapping may be needed (here assume page matches)
            if (cmd.page == pageNo) {
                val r = viewModel.getAyaRects(cmd.page, cmd.sora, cmd.aya)
                rects = r
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Image + overlay
        if (bitmap != null) {
            HighlightImageWithOverlay(
                imageBitmap = bitmap,
                rects = rects,
                nightMode = nightMode,
                onLongPress = { imageX, imageY, screenOffset ->
                    // query DB for touched aya and fill rects
                    scope.launch {
                        val touched = viewModel.getTouchedAya(pageNo, imageX.toFloat(), imageY.toFloat())
                        touched?.let {
                            rects = it.ayaRects ?: emptyList()
                            // set popup location to the screenOffset where user long-pressed
                            popupOffset = screenOffset
                            showPopup = true
                        }
                    }
                },
                onTap = {
                    // toggle toolbar, handled by Activity via viewModel event if needed
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // placeholder while loading
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Loading...")
            }
        }

        // popup menu using Compose DropdownMenu anchored at popupOffset (approx)
        if (showPopup) {
            val localCtx = LocalContext.current
            // simple popup placed at top-left offset using Box with absolutePadding
            Box(
                Modifier
                    .offset(x = androidx.compose.ui.unit.Dp(popupOffset.x / localCtx.resources.displayMetrics.density),
                        y = androidx.compose.ui.unit.Dp(popupOffset.y / localCtx.resources.displayMetrics.density))
            ) {
                DropdownMenu(expanded = true, onDismissRequest = { showPopup = false }) {
                    DropdownMenuItem(
                        text = { Text("Play") },
                        onClick = {
                            showPopup = false
                            viewModel.emitAudioCommand(
                                AudioCommand.PlaySingle(
                                    ayaId = -1,
                                    pageId = pageNo,
                                    suraId = -1,
                                    readerId = 0,
                                    downloadLink = null
                                )
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Tafseer") },
                        onClick = {
                            showPopup = false
                            val intent = Intent(localCtx, MainActivity::class.java)
                            localCtx.startActivity(intent)
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Share") },
                        onClick = {
                            showPopup = false
                            val share = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "Aya text here")
                            }
                            localCtx.startActivity(Intent.createChooser(share, "Share using"))
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Copy") },
                        onClick = {
                            showPopup = false
                            val clipboard = localCtx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Copied Text", "Aya text here")
                            clipboard.setPrimaryClip(clip)
                        }
                    )
                }
            }
        }
    }
}

/**
 * Draw image and overlay rectangles; converts touches to image coordinates and supports long-press.
 */
@Composable
fun HighlightImageWithOverlay(
    imageBitmap: ImageBitmap,
    rects: List<AyaRect>,
    nightMode: Boolean,
    onLongPress: (imageX: Float, imageY: Float, screenOffset: androidx.compose.ui.geometry.Offset) -> Unit,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    var scaleRatio by remember { mutableStateOf(1f) }
    var xImageOffset by remember { mutableStateOf(0f) }
    var yImageOffset by remember { mutableStateOf(0f) }
    val bitmapWidth = imageBitmap.width.toFloat()
    val bitmapHeight = imageBitmap.height.toFloat()

    Box(modifier = modifier
        .onSizeChanged { containerSize = it }
        .pointerInput(Unit) {
            detectTapGestures(
                onLongPress = { offset ->
                    // compute image coords
                    val imageX = ((offset.x - xImageOffset) / scaleRatio)
                    val imageY = ((offset.y - yImageOffset) / scaleRatio)
                    onLongPress(imageX, imageY, offset)
                },
                onTap = { onTap() }
            )
        }
    ) {
        // draw image using Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cw = size.width
            val ch = size.height

            // compute scale & offsets like original calculateBitmapScale
            val sRatio = cw / ch
            val iRatio = bitmapWidth / bitmapHeight
            if (sRatio < iRatio) {
                scaleRatio = cw / bitmapWidth
            } else {
                scaleRatio = ch / bitmapHeight
            }
            val newImageWidth = bitmapWidth * scaleRatio
            val newImageHeight = bitmapHeight * scaleRatio
            xImageOffset = ((cw - newImageWidth) / 2f)
            yImageOffset = ((ch - newImageHeight) / 2f)

            // draw the bitmap
            drawImage(
                image = imageBitmap,
                dstOffset = IntOffset(xImageOffset.toInt(), yImageOffset.toInt()),
                dstSize = IntSize(newImageWidth.toInt(), newImageHeight.toInt())
            )


            // draw highlight rects (converted)
            val paintAlpha = 150 / 255f
            val highlightColor = Color(red = 146f/255f, green = 144f/255f, blue = 248f/255f, alpha = paintAlpha)
            for (r in rects) {
                val left = xImageOffset + r.left * scaleRatio
                val top = yImageOffset + r.top * scaleRatio
                val right = xImageOffset + r.right * scaleRatio
                val bottom = yImageOffset + r.bottom * scaleRatio
                drawRect(
                    color = highlightColor,
                    topLeft = androidx.compose.ui.geometry.Offset(left, top),
                    size = Size(right - left, bottom - top)
                )
            }
        }
    }
}
