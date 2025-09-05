package com.cit.mycomposeapplication.utils

import android.Manifest
import android.R
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import java.util.Objects
import java.util.TimeZone

class PrayerTimeAct :  Constants {

    private val REQUEST_LOCATION_PERM = 115
    private val REQUEST_LOCATION_PERM2 = 1150

    private lateinit var settings: AppSettings

    private var latitudeme: Double = 0.0
    private var longitudeme: Double = 0.0

    var permissionGranted: Boolean = false


    private val permissions = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )



    fun getNextDate(input: String): String {
        val calendar = Calendar.getInstance()

        if (input.equals("fajr", ignoreCase = true)) {
            // If input is "Fajar," add one day to the current date
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        val formatter = SimpleDateFormat("EEE dd MMM yyyy", Locale.getDefault())
        return formatter.format(calendar.time)
    }

    private fun capitalizeFirstLetter(original: String?): String? {
        return if (original == null || original.isEmpty()) {
            original // Return the original string if it is null or empty
        } else original.substring(0, 1).uppercase(Locale.getDefault()) + original.substring(1)

        // Capitalize the first letter and concatenate the rest of the original string
    }


    fun difftime(next: String?, now: String?, prayer: String?): String {
        var hours: Int = 0
        var min = 0
        val days: Int
        val difference: Long


        try {
            val simpleDateFormat = SimpleDateFormat("h:mm a", Locale.ENGLISH)
            val date1 = simpleDateFormat.parse(now)
            var date2 = simpleDateFormat.parse(next)

            val calendar = Calendar.getInstance()

            if (prayer.equals("Fajr", ignoreCase = true)) {
                // If input is "Fajr," add one day to date2
                calendar.time = date2
                calendar.add(Calendar.DAY_OF_MONTH, 1)
                date2 = calendar.time
            }

            Log.e("TAGMainAct", "difftime: date1: $date1 :: date2: $date2  :: prayer: $prayer");

            difference = date2.time - date1.time
            days = (difference / (1000 * 60 * 60 * 24)).toInt()
            hours = ((difference - 1000 * 60 * 60 * 24 * days) / (1000 * 60 * 60)).toInt()
            min =
                (difference - 1000 * 60 * 60 * 24 * days - 1000 * 60 * 60 * hours).toInt() / (1000 * 60)
            hours = if (hours < 0) -hours else hours
            Log.i("======= Hours", " :: $hours")
            return "" + hours + " hrs " + min + " mins"
        } catch (e: ParseException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        return "" + hours + " hrs " + min + " mins"
    }

    companion object {
        fun difftimeprayer2(context: Context, next: String?, now: String?, prayer: String?): String {
            Log.e("TAGMainAct", "difftimeprayer: next: $next :: now: $now  ");
            var hours: Int = 0
            var min = 0
            val days: Int
            val difference: Long
            try {
                val simpleDateFormat = SimpleDateFormat("h:mm a", Locale.ENGLISH)
                println("System Locale: " + Locale.getDefault())
                println("Input Time: '$now'") // Check if it's correctly formatted


                val date1 = simpleDateFormat.parse(now)
                var date2 = simpleDateFormat.parse(next)

                val calendar = Calendar.getInstance()

                if (prayer.equals("Fajr", ignoreCase = true)) {
                    // If input is "Fajr," add one day to date2
                    calendar.time = date2
                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                    date2 = calendar.time
                }

                Log.e("TAGMainAct", "difftimeprayer: date1: $date1 :: date2: $date2  ");

                difference = date2.time - date1.time
                days = (difference / (1000 * 60 * 60 * 24)).toInt()
                hours = ((difference - 1000 * 60 * 60 * 24 * days) / (1000 * 60 * 60)).toInt()
                min =
                    (difference - 1000 * 60 * 60 * 24 * days - 1000 * 60 * 60 * hours).toInt() / (1000 * 60)
                hours = if (hours < 0) -hours else hours
                Log.i("======= Hours", " :: $hours")

                return "" + hours + " Hour " + min + " Minutes"
            } catch (e: ParseException) {
                e.printStackTrace()

                Log.e("TAGMainAct", "difftimeprayer: catch: ");
            }
            return "" + hours + " hrs " + min + " mins"
        }

        fun difftimeprayer(context: Context, next: String?, now: String?, prayer: String?): String {
            Log.e("TAGMainAct", "difftimeprayer: next: $next :: now: $now")

            var hours = 0
            var min = 0

            try {
                val inputFormat: SimpleDateFormat
                val nextTimeFormatted: String
                val nowTimeFormatted: String

                if (DateFormat.is24HourFormat(context)) {
                    inputFormat = SimpleDateFormat("HH:mm", Locale.ENGLISH)
                    nextTimeFormatted = next ?: "00:00"
                    nowTimeFormatted = now ?: "00:00"
                } else {
                    inputFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
                    nextTimeFormatted = next ?: "12:00 AM"
                    nowTimeFormatted = now ?: "12:00 AM"
                }

                val date1 = inputFormat.parse(nowTimeFormatted)
                var date2 = inputFormat.parse(nextTimeFormatted)

                // Handle Fajr being next day
                if (prayer.equals("Fajr", ignoreCase = true)) {
                    val calendar = Calendar.getInstance()
                    calendar.time = date2
                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                    date2 = calendar.time
                }

                if (date1 != null && date2 != null) {
                    val difference = date2.time - date1.time
                    val days = (difference / (1000 * 60 * 60 * 24)).toInt()
                    hours = ((difference - 1000 * 60 * 60 * 24 * days) / (1000 * 60 * 60)).toInt()
                    min = (difference - 1000 * 60 * 60 * 24 * days - 1000 * 60 * 60 * hours).toInt() / (1000 * 60)
                }

                return "$hours Hour $min Minutes"

            } catch (e: ParseException) {
                Log.e("TAGMainAct", "difftimeprayer: parse error", e)
            } catch (e: Exception) {
                Log.e("TAGMainAct", "difftimeprayer: unexpected error", e)
            }

            return "-- hrs -- mins"
        }



        fun getPrayerNameSunsetRise(
            prayerTimes: LinkedHashMap<String, String>,
            context: Context
        ): String {
            val prayerNames: List<String> = ArrayList(prayerTimes.keys)
            val now = Calendar.getInstance(TimeZone.getDefault())
            now.timeInMillis = System.currentTimeMillis()

            var then = Calendar.getInstance(TimeZone.getDefault())
            then.timeInMillis = System.currentTimeMillis()

            var nextAlarmFound = false
            var nameOfPrayerFound = ""

            for (prayer in prayerNames) {
//            if (prayer != SUNRISE && prayer != SUNSET) {
                val time = prayerTimes[prayer]

                if (time != null) {
                    then = getCalendarFromPrayerTime(context, then, time)

                    if (then.after(now)) {
                        // this is the alarm to set
                        nameOfPrayerFound = prayer
                        nextAlarmFound = true
                        break
                    }
                }
//            }

            }

            if (!nextAlarmFound) {
                for (prayer in prayerNames) {
//                if (prayer != SUNRISE && prayer != SUNSET) {
                    val time = prayerTimes[prayer]

                    if (time != null) {
                        then = getCalendarFromPrayerTime(context, then, time)

                        if (then.before(now)) {
                            // this is the alarm to set
                            nameOfPrayerFound = prayer
                            then.add(Calendar.DAY_OF_YEAR, 1)
                            break
                        }
                    }
//                }
                }
            }

            return nameOfPrayerFound
        }

        fun getCalendarFromPrayerTime(
            context: Context,
            cal: Calendar,
            prayerTime: String
        ): Calendar {
            var strTime = prayerTime
            if (!DateFormat.is24HourFormat(context)) {
                val display = SimpleDateFormat("HH:mm", Locale.ENGLISH)
                val parse = SimpleDateFormat("h:mm a", Locale.ENGLISH)
                try {
                    val date = parse.parse(strTime)

                    if (date != null) strTime = display.format(date)
                } catch (e: Exception) {
                }
            }
            val time = strTime.split(":").toTypedArray()
            try {
                cal[Calendar.HOUR_OF_DAY] = Integer.valueOf(time[0])
            } catch (e: Exception) {
                cal[Calendar.HOUR_OF_DAY] = 0
            }
            try {
                cal[Calendar.MINUTE] = Integer.valueOf(time[1])
            } catch (e: Exception) {
                cal[Calendar.MINUTE] = 0
            }
            cal[Calendar.SECOND] = 0
            cal[Calendar.MILLISECOND] = 0
            return cal
        }

        fun convertTimeStringToCalendar(
            timeString: String,
            dayIndicator: Int
        ): Calendar? {
            try {
                val dateFormat12 = SimpleDateFormat("h:mm a", Locale.ENGLISH)
                val dateFormat24 = SimpleDateFormat("HH:mm", Locale.ENGLISH)
//                val simpleDateFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)


                val calendar = Calendar.getInstance()

                // Set the day based on the indicator
                calendar.add(Calendar.DAY_OF_MONTH, dayIndicator)

                // Parse the time based on the input format
                val date = if (timeString.contains("am") || timeString.contains("pm")) {
                    dateFormat12.parse(timeString)
                } else {
                    dateFormat24.parse(timeString)
                }

                // Set the time in the calendar
                date?.let {
                    calendar.set(Calendar.HOUR_OF_DAY, it.hours)
                    calendar.set(Calendar.MINUTE, it.minutes)
                    calendar.set(Calendar.SECOND, 0)
                    calendar.set(Calendar.MILLISECOND, 0)
                }

                return calendar
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }

        fun convertTimeStringToCalendarFajarNow(
            nexttimeString: String,
            nowdayIndicator: String
        ): Calendar? {
            try {
                val dateFormat12 = SimpleDateFormat("h:mm a", Locale.ENGLISH)
                val dateFormat24 = SimpleDateFormat("HH:mm", Locale.ENGLISH)

                var currentTime = Calendar.getInstance()
                val nextNamazCalendar = Calendar.getInstance()
                val nowNamazCalendar = Calendar.getInstance()

                // Parse the time based on the input format
                val date = if (nexttimeString.contains("am") || nexttimeString.contains("pm")) {
                    dateFormat12.parse(nexttimeString)
                } else {
                    dateFormat24.parse(nexttimeString)
                }
                // Parse the time based on the input format
                val dateNow =
                    if (nowdayIndicator.contains("am") || nowdayIndicator.contains("pm")) {
                        dateFormat12.parse(nowdayIndicator)
                    } else {
                        dateFormat24.parse(nowdayIndicator)
                    }


                // Set the time in the calendar
                date?.let {
                    nextNamazCalendar.set(Calendar.HOUR_OF_DAY, it.hours)
                    nextNamazCalendar.set(Calendar.MINUTE, it.minutes)
                    nextNamazCalendar.set(Calendar.SECOND, 0)
                    nextNamazCalendar.set(Calendar.MILLISECOND, 0)
                }

                // Set the time in the nextNamaz calendar
                dateNow?.let {
                    nowNamazCalendar.set(Calendar.HOUR_OF_DAY, it.hours)
                    nowNamazCalendar.set(Calendar.MINUTE, it.minutes)
                    nowNamazCalendar.set(Calendar.SECOND, 0)
                    nowNamazCalendar.set(Calendar.MILLISECOND, 0)
                }

                if (currentTime.before(nextNamazCalendar)) {
                    nowNamazCalendar.add(Calendar.DAY_OF_MONTH, -1)
                }

                return nowNamazCalendar
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }

        fun convertTimeStringToCalendarFajarNext(
            nexttimeString: String,
            nowdayIndicator: String
        ): Calendar? {
            try {
                val dateFormat12 = SimpleDateFormat("h:mm a", Locale.ENGLISH)
                val dateFormat24 = SimpleDateFormat("HH:mm", Locale.ENGLISH)

                var currentTime = Calendar.getInstance()
                val nextNamazCalendar = Calendar.getInstance()
                val nowNamazCalendar = Calendar.getInstance()

                // Parse the time based on the input format
                val date = if (nexttimeString.contains("am") || nexttimeString.contains("pm")) {
                    dateFormat12.parse(nexttimeString)
                } else {
                    dateFormat24.parse(nexttimeString)
                }
                // Parse the time based on the input format
                val dateNow =
                    if (nowdayIndicator.contains("am") || nowdayIndicator.contains("pm")) {
                        dateFormat12.parse(nowdayIndicator)
                    } else {
                        dateFormat24.parse(nowdayIndicator)
                    }

                // Set the time in the calendar
                date?.let {
                    nextNamazCalendar.set(Calendar.HOUR_OF_DAY, it.hours)
                    nextNamazCalendar.set(Calendar.MINUTE, it.minutes)
                    nextNamazCalendar.set(Calendar.SECOND, 0)
                    nextNamazCalendar.set(Calendar.MILLISECOND, 0)
                }

                // Set the time in the nextNamaz calendar
                dateNow?.let {
                    nowNamazCalendar.set(Calendar.HOUR_OF_DAY, it.hours)
                    nowNamazCalendar.set(Calendar.MINUTE, it.minutes)
                    nowNamazCalendar.set(Calendar.SECOND, 0)
                    nowNamazCalendar.set(Calendar.MILLISECOND, 0)
                }


                // If nextNamazTime is greater than now, return the calendar; otherwise, return null
                if (currentTime.after(nowNamazCalendar)) {

                    nextNamazCalendar.add(Calendar.DAY_OF_MONTH, 1)
                }
                return nextNamazCalendar
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }

    }
}
