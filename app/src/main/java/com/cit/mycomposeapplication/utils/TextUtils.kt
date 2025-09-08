package com.cit.mycomposeapplication.utils

import android.text.Spanned
import androidx.compose.material3.LocalTextStyle
import androidx.core.text.HtmlCompat
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.material3.Text
import androidx.compose.ui.text.style.TextOverflow

// 🔹 Extension function: String → AnnotatedString (from HTML)
fun String.asAnnotatedString(): AnnotatedString {
    val spanned: Spanned = HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_COMPACT)
    return AnnotatedString(spanned.toString())
}

// 🔹 Composable extension: use directly inside Text
@Composable
fun String.HtmlText(
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip
) {
    Text(
        text = this.asAnnotatedString(),
        modifier = modifier,
        style = style,
        maxLines = maxLines,
        overflow = overflow
    )
}
