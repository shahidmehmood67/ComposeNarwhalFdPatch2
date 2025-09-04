package com.cit.mycomposeapplication.utils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.RectF
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import com.cit.mycomposeapplication.models.AyaRect
import kotlin.math.pow
import kotlin.math.sqrt

object DisplayInfoUtil {

    fun getSmallestWidth(context: Context): Int {
        return  context.resources.configuration.smallestScreenWidthDp
    }

    fun getSmallestWidthQualifier(context: Context): String {
        val swDp = getSmallestWidth(context)
        return "sw${swDp}dp"
    }


    fun getDensityBucket(context: Context): String {
        val densityDpi = context.resources.displayMetrics.densityDpi
        return when {
            densityDpi < 140 -> "ldpi"
            densityDpi < 180 -> "mdpi"
            densityDpi < 260 -> "hdpi"
            densityDpi < 340 -> "xhdpi"
            densityDpi < 440 -> "xxhdpi"
            else -> "xxxhdpi"
        } + " ($densityDpi dpi)"
    }

    fun getDensity(context: Context): String {
        val dm = context.resources.displayMetrics
        return "Scale factor: ${dm.density}, DPI: ${dm.densityDpi}"
    }

    fun getScreenResolution(activity: Activity): String {
        val metrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.display?.getRealMetrics(metrics)
        } else {
            @Suppress("DEPRECATION")
            (activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getRealMetrics(metrics)
        }
        return "${metrics.widthPixels} x ${metrics.heightPixels} px"
    }

    fun getScreenSizeInInches(context: Context): String {
        val metrics = context.resources.displayMetrics
        val widthInches = metrics.widthPixels / metrics.xdpi
        val heightInches = metrics.heightPixels / metrics.ydpi
        val diagonal = sqrt(widthInches.pow(2) + heightInches.pow(2))
        return "%.2f inches".format(diagonal)
    }

    fun getWidthHeightInDp(context: Context): String {
        val metrics = context.resources.displayMetrics
        val widthDp = metrics.widthPixels / metrics.density
        val heightDp = metrics.heightPixels / metrics.density
        return "Width: ${widthDp.toInt()} dp, Height: ${heightDp.toInt()} dp"
    }

    fun getScreenSizeCategory(context: Context): String {
        return when (context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) {
            Configuration.SCREENLAYOUT_SIZE_SMALL -> "small"
            Configuration.SCREENLAYOUT_SIZE_NORMAL -> "normal"
            Configuration.SCREENLAYOUT_SIZE_LARGE -> "large"
            Configuration.SCREENLAYOUT_SIZE_XLARGE -> "xlarge"
            else -> "undefined"
        }
    }

    fun getOrientation(context: Context): String {
        return when (context.resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> "portrait"
            Configuration.ORIENTATION_LANDSCAPE -> "landscape"
            else -> "undefined"
        }
    }

    fun getRefreshRate(activity: Activity): String {
        return "Refresh Rate: ${activity.display?.refreshRate ?: "Unknown"} Hz"
    }

    fun getAspectRatio(context: Context): String {
        val metrics = context.resources.displayMetrics
        val width = metrics.widthPixels.toFloat()
        val height = metrics.heightPixels.toFloat()
        val ratio = width / height
        return "Aspect Ratio: %.2f".format(ratio)
    }

    /**
     * Collects all display info in a multi-line formatted string.
     * Pass an Activity if you want resolution + refresh rate; otherwise
     * those will show "N/A".
     */
    fun getAllDisplayInfo(context: Context, activity: Activity? = null): String {
        val densityBucket = getDensityBucket(context)
        val swQualifier = getSmallestWidthQualifier(context)
        val density       = getDensity(context)
        val resolution    = activity?.let { getScreenResolution(it) } ?: "N/A"
        val sizeInches    = getScreenSizeInInches(context)
        val dpSize        = getWidthHeightInDp(context)
        val sizeCategory  = getScreenSizeCategory(context)
        val orientation   = getOrientation(context)
        val refreshRate   = activity?.let { getRefreshRate(it) } ?: "Refresh Rate: N/A"
        val aspectRatio   = getAspectRatio(context)

        return buildString {
            appendLine("Display Information")
            appendLine("-------------------")
            appendLine("Density Bucket : $densityBucket")
            appendLine("Smallest Width Qualifier : $swQualifier")
            appendLine(density) // already formatted: "Scale factor: x, DPI: y"
            appendLine("Resolution     : $resolution")
            appendLine("Size (Diagonal): $sizeInches")
            appendLine("Size (dp)      : $dpSize")
            appendLine("Size Category  : $sizeCategory")
            appendLine("Orientation    : $orientation")
            appendLine(refreshRate) // already formatted in util
            appendLine(aspectRatio) // already formatted in util
        }.trimEnd()  // remove last newline
    }



    fun Context.calculateYOffsetByDiagonal(): Int {
        // ---- Current device ----
        val metrics = this.resources.displayMetrics
        val widthPx = metrics.widthPixels
        val heightPx = metrics.heightPixels
        val xdpi = metrics.xdpi
        val ydpi = metrics.ydpi

        // diagonal in inches (more accurate using dpi)
        val diagInches = sqrt((widthPx / xdpi).pow(2) + (heightPx / ydpi).pow(2))

        val swDp = this.resources.configuration.smallestScreenWidthDp

        return when {
            diagInches >= 6.15 -> 0          // base ~6.2" realme c53
            diagInches >= 6.0  -> -5         // ~6.0" oppo
            diagInches >= 5.9  -> -15        // ~5.9"
            diagInches >= 5.7  -> -36        // ~5.7–5.8" s8
            diagInches >= 5.5 && swDp >= 411  -> -28        // ~5.5" c7
            diagInches >= 5.5 -> -28        // ~5.5" c7
            else -> -40                      // fallback for smaller
        }
    }


    // Overloaded function for AyaRect
    fun List<AyaRect>.adjustVertical(offsetTop: Float = 0f, offsetBottom: Float = 0f): List<AyaRect> {
        return this.map { rect ->
            AyaRect(
                rect.left,
                rect.top + offsetTop,
                rect.right,
                rect.bottom + offsetBottom
            )
        }
    }

}
