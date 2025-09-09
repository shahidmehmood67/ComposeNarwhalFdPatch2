package com.cit.mycomposeapplication.viewmodel

import com.cit.mycomposeapplication.R
import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.text.format.DateFormat
import android.util.Log
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import com.cit.mycomposeapplication.models.Prayer
import com.cit.mycomposeapplication.models.PrayerUiState
import com.cit.mycomposeapplication.repository.LocationRepository
import com.cit.mycomposeapplication.ui.theme.BgtobarColorAsar
import com.cit.mycomposeapplication.ui.theme.BgtobarColorFajr
import com.cit.mycomposeapplication.ui.theme.BgtobarColorIsha
import com.cit.mycomposeapplication.ui.theme.BgtobarColorMagrib
import com.cit.mycomposeapplication.ui.theme.BgtobarColorZuhar
import com.cit.mycomposeapplication.utils.AppSettings
import com.cit.mycomposeapplication.utils.Constants
import com.cit.mycomposeapplication.utils.PrayTime
import com.cit.mycomposeapplication.utils.PrayerTimeAct.Companion.convertTimeStringToCalendar
import com.cit.mycomposeapplication.utils.PrayerTimeAct.Companion.convertTimeStringToCalendarFajarNext
import com.cit.mycomposeapplication.utils.PrayerTimeAct.Companion.getPrayerNameSunsetRise
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import kotlin.jvm.internal.Intrinsics

open class LocationViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: LocationRepository = LocationRepository(application)

    private val _locationLiveData = MutableLiveData<String?>()
    open val locationLiveData: LiveData<String?> get() = _locationLiveData

    private val _userLocation = MutableLiveData<Location?>()
    val userLocation: LiveData<Location?> get() = _userLocation

    var gotosettings = false

    fun requestLocationUpdates() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getLocationFlow().collect { location ->
                _locationLiveData.postValue(location)
            }
        }
    }

    fun fetchUserLocation() {
        if (isLocationAccessEnabled()) {
            viewModelScope.launch {
                _userLocation.value = repository.fetchUserLocation()
            }
        }
        else {
            _userLocation.value = null
        }
    }

    fun isPermissionGranted(): Boolean {
        val context = getApplication<Application>().applicationContext
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    fun isLocationAccessEnabled(): Boolean {
        val context = getApplication<Application>().applicationContext
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        val isGpsEnabled = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true
        val isNetworkEnabled = locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true

        return (fineLocationGranted || coarseLocationGranted) && (isGpsEnabled || isNetworkEnabled)
    }




    private val _uiState = MutableLiveData(PrayerUiState())
    open val uiState: LiveData<PrayerUiState> = _uiState

    private var countdownJob: Job? = null
    private var nextPrayerCalendar: Calendar? = null

    fun init(latFor: Double, lngFor: Double, context: Context, settings: AppSettings) {
        var nowTime = " "

        // Current time
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val localTime = LocalTime.now()
            val dateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)
            nowTime = localTime.format(dateTimeFormatter).toString()
        }

        // Get prayer times
        val prayerTimes: LinkedHashMap<String, String> =
            PrayTime.getPrayerTimes(context, latFor, lngFor)

        // Build prayer list
        val list = mutableListOf<Prayer>()
        for (i in prayerTimes.keys.indices) {
            val key = Constants.KEYS[i]
            if (key != "sunset") {
                val name = Constants.NAME_ID[i]
                val time = prayerTimes[key] ?: ""
                val setting = settings.getInt("alarm_for_$key", 0)
                list.add(Prayer(key, name, time, setting))
            }
        }

        val nowPrayerName = getPrayerName(prayerTimes)
        val nowPrayerTime = prayerTimes[nowPrayerName] ?: ""

        // Resolve UI values and also get nextPrayerCalendar
        val (uiState, nextCal) = resolvePrayerUi(
            context = context,
            latFor = latFor,
            lngFor = lngFor,
            prayerTimes = prayerTimes,
            nowPrayerName = nowPrayerName,
            nowTime = nowTime
        )

        this.nextPrayerCalendar = nextCal

        // Cancel any existing timer before starting a new one
        countdownJob?.cancel()
        startCountdown(uiState, list, nowPrayerName, nowPrayerTime)
    }

    private fun getPrayerName(prayerTimes: LinkedHashMap<*, *>): String {
        Log.d("MainActivityDashBoard", "getPrayerName: (2809) ")
        val prayerNames = ArrayList(prayerTimes.keys as Collection<*>) as List<*>
        val now = Calendar.getInstance(TimeZone.getDefault())
        Intrinsics.checkNotNullExpressionValue(now, "now")
        now.timeInMillis = System.currentTimeMillis()
        var then = Calendar.getInstance(TimeZone.getDefault())
        Intrinsics.checkNotNullExpressionValue(then, "then")
        then.timeInMillis = System.currentTimeMillis()
        var nextAlarmFound = false
        var nameOfPrayerFound = ""
        var var8 = prayerNames.iterator()
        Log.d("MainActivityDashBoard", "getPrayerName: (2821) now: $now")
        Log.d("MainActivityDashBoard", "getPrayerName: (2822) then: $then")
        while (var8.hasNext()) {
            val obj = var8.next()!!
            if (obj is String) {
                val prayerName = obj
                Log.d("MainActivityDashBoard", "getPrayerName: (2827) prayerName: $prayerName")
                Log.d("PrayerNames", prayerName)
            } else {
                Log.d(
                    "MainActivityDashBoard",
                    "getPrayerName: (2830) Invalid type: " + obj.javaClass.simpleName
                )
            }
        }
        var prayer: String
        var time: String?
        while (var8.hasNext()) {
            Log.d("MainActivityDashBoard", "getPrayerName: (2827) var8.next(): " + var8.next())
            prayer = var8.next() as String
            Log.d(
                "MainActivityDashBoard",
                "getPrayerName: (2830) " + Intrinsics.areEqual(prayer, "sunrise")
            )
            Log.d(
                "MainActivityDashBoard",
                "getPrayerName: (2831) " + Intrinsics.areEqual(prayer, "sunset")
            )
            Log.d(
                "MainActivityDashBoard",
                "getPrayerName: (2832) " + (Intrinsics.areEqual(prayer, "sunrise") xor true)
            )
            Log.d(
                "MainActivityDashBoard",
                "getPrayerName: (2833) " + (Intrinsics.areEqual(prayer, "sunset") xor true)
            )
            if (Intrinsics.areEqual(prayer, "sunrise") xor true && Intrinsics.areEqual(
                    prayer,
                    "sunset"
                ) xor true
            ) {
                time = prayerTimes[prayer] as String?
                if (time != null) {
                    Log.d("MainActivityDashBoard", "getPrayerName: (2836) time:$time")
                    Intrinsics.checkNotNullExpressionValue(then, "then")
                    then = getCalendarFromPrayerTime(then, time)
                    Log.d("MainActivityDashBoard", "getPrayerName: (2840) $then")
                    if (then.after(now)) {
                        nameOfPrayerFound = if (prayer === "fajr") {
                            Log.d("MainActivityDashBoard", "getPrayerName: (2832) ")
                            "isha"
                        } else if (prayer === "sunrise") {
                            Log.d("MainActivityDashBoard", "getPrayerName: (2835) ")
                            "fajr"
                        } else if (prayer === "dhuzur") {
                            Log.d("MainActivityDashBoard", "getPrayerName: (2838) ")
                            "sunrise"
                        } else if (prayer === "asr") {
                            Log.d("MainActivityDashBoard", "getPrayerName: (2841) ")
                            "dhuzur"
                        } else if (prayer === "maghrib") {
                            Log.d("MainActivityDashBoard", "getPrayerName: (2844) ")
                            "asr"
                        } else if (prayer === "sunset") {
                            Log.d("MainActivityDashBoard", "getPrayerName: (2847) ")
                            "maghrib"
                        } else if (prayer === "isha") {
                            Log.d("MainActivityDashBoard", "getPrayerName: (2850) ")
                            "maghrib"
                        } else {
                            Log.d("MainActivityDashBoard", "getPrayerName: (2853) ")
                            "fajr"
                        }

                        // nameOfPrayerFound = prayer;
                        nextAlarmFound = true
                        break
                    }
                } else {
                    Log.d("MainActivityDashBoard", "getPrayerName: (2836) time: is null")
                }
            }
        }
        if (!nextAlarmFound) {
            var8 = prayerNames.iterator()
            Log.d("MainActivityDashBoard", "getPrayerName: (2868) ")
            while (var8.hasNext()) {
                prayer = var8.next() as String
                if (Intrinsics.areEqual(prayer, "sunrise") xor true && Intrinsics.areEqual(
                        prayer,
                        "sunset"
                    ) xor true
                ) {
                    time = prayerTimes[prayer] as String?
                    if (time != null) {
                        Log.d("MainActivityDashBoard", "getPrayerName: (2873) ")
                        Intrinsics.checkNotNullExpressionValue(then, "then")
                        then = getCalendarFromPrayerTime(then, time)
                        Log.d("MainActivityDashBoard", "getPrayerName: (2876) ")
                        if (then.before(now)) {
                            nameOfPrayerFound = prayer
                            then.add(6, 1)
                            Log.d("MainActivityDashBoard", "getPrayerName: (2883) prayer:$prayer")
                            break
                        }
                    }
                }
            }
        }
        return nameOfPrayerFound
    }

    private val appContext: Context = application.applicationContext

    private fun getCalendarFromPrayerTime(cal: Calendar, prayerTime: String): Calendar {
        var strTime = prayerTime
        if (!DateFormat.is24HourFormat(appContext)) {
            val display = SimpleDateFormat("HH:mm", Locale.getDefault())
            val parse = SimpleDateFormat("hh:mm a", Locale.getDefault())
            try {
                val date = parse.parse(strTime)
                if (date != null) {
                    val var10000 = display.format(date)
                    Intrinsics.checkNotNullExpressionValue(var10000, "display.format(date)")
                    strTime = var10000
                }
            } catch (var10: Exception) {
            }
        }
//        val `$this$toTypedArray$iv` = (strTime as CharSequence).split(arrayOf(":"), false, 0) as Collection<*>
        //  int $i$f$toTypedArray = false;
//        val var14 = `$this$toTypedArray$iv`.toTypedArray()
        val var14: Array<String> = strTime.split(":").toTypedArray()

        return if (var14 == null) {
            throw NullPointerException("null cannot be cast to non-null type kotlin.Array<T of kotlin.collections.ArraysKt__ArraysJVMKt.toTypedArray>")
        } else {
            val time = var14 as Array<String>
            var var10002: Int
            try {
                var10002 = time[0].toInt()
                Intrinsics.checkNotNullExpressionValue(var10002, "Integer.valueOf(time[0])")
                cal[11] = var10002
            } catch (var9: Exception) {
                cal[11] = 0
            }
            try {
                var10002 = time[1].toInt()
                Intrinsics.checkNotNullExpressionValue(var10002, "Integer.valueOf(time[1])")
                cal[12] = var10002
            } catch (var8: Exception) {
                cal[12] = 0
            }
            cal[13] = 0
            cal[14] = 0
            cal
        }
    }

    private fun startCountdown(
        baseUi: PrayerUiState,
        list: List<Prayer>,
        nowPrayerName: String,
        nowPrayerTime: String
    ) {
        countdownJob = viewModelScope.launch {
            while (isActive) {
                val remainingMillis =
                    nextPrayerCalendar?.timeInMillis?.minus(System.currentTimeMillis()) ?: 0L

                val remainingFormatted = if (remainingMillis > 0) {
                    formatMillis(remainingMillis)
                } else {
                    "00:00"
                }

                _uiState.postValue(
                    baseUi.copy(
                        prayers = list,
                        currentPrayerName = nowPrayerName,
                        currentPrayerTime = nowPrayerTime,
                        remainingTime = remainingFormatted
                    )
                )

                delay(1000) // tick every second
            }
        }
    }

    private fun resolvePrayerUi(
        context: Context,
        latFor: Double,
        lngFor: Double,
        prayerTimes: LinkedHashMap<String, String>,
        nowPrayerName: String,
        nowTime: String
    ): Pair<PrayerUiState, Calendar?> {

        var isBlack = true
        var statusBarColor: Color? = null
        var backgroundRes: Brush? = null
        var toolbarRes: Int? = null
        var vectorRes: Int? = null

        var nextPrayer = ""
        var nextPrayerTime2: String? = ""
        var nextPrayerTimeCalendr: Calendar? = createEmptyCalendar()

        when (getPrayerNameSunsetRise(prayerTimes, context)) {
            Constants.FAJR -> {
                nextPrayer = Constants.FAJR
                val tomorrowTimes = PrayTime.getPrayerTimesDate(context, latFor, lngFor, 1)
                nextPrayerTime2 = tomorrowTimes[nextPrayer] as String?
                nextPrayerTimeCalendr =
                    convertTimeStringToCalendarFajarNext(nextPrayerTime2!!, prayerTimes[Constants.ISHA]!!)

                statusBarColor = BgtobarColorFajr
                backgroundRes = Brush.verticalGradient(colors = listOf(
                    Color(0x00000000),
                    Color(0xFFE2C7CC)
                ))
                toolbarRes = R.drawable.maintoolbarfajar
                vectorRes = R.drawable.status_bar_mosque_svg
                isBlack = true
            }
            Constants.SUNRISE -> {
                nextPrayer = Constants.SUNRISE
                nextPrayerTime2 = prayerTimes[nextPrayer]
                nextPrayerTimeCalendr =
                    convertTimeStringToCalendar(nextPrayerTime2!!, Constants.TODAY)

                statusBarColor = BgtobarColorZuhar
                backgroundRes = Brush.verticalGradient(
                    colors = listOf(
                        Color(0x00000000),  // top color  transparent
                        Color(0xFF80CCE4) // bottom color
                    )
                )

                toolbarRes = R.drawable.maintoolbarzuhar
                vectorRes = R.drawable.status_bar_mosque_svg
                isBlack = true
            }
            Constants.DHUHR -> {
                nextPrayer = Constants.DHUHR
                nextPrayerTime2 = prayerTimes[nextPrayer]
                nextPrayerTimeCalendr =
                    convertTimeStringToCalendar(nextPrayerTime2!!, Constants.TODAY)

                statusBarColor = BgtobarColorZuhar
                backgroundRes = Brush.verticalGradient(
                    colors = listOf(
                        Color(0x00000000),    // endColor (transparent)
                        Color(0xFF80CCE4)   // startColor
                    )
                )
                toolbarRes = R.drawable.maintoolbarzuhar
                vectorRes = R.drawable.status_bar_mosque_svg
                isBlack = true
            }
            Constants.ASR -> {
                nextPrayer = Constants.ASR
                nextPrayerTime2 = prayerTimes[nextPrayer]
                nextPrayerTimeCalendr =
                    convertTimeStringToCalendar(nextPrayerTime2!!, Constants.TODAY)

                statusBarColor = BgtobarColorAsar
                backgroundRes = Brush.verticalGradient(
                    colors = listOf(
                        Color(0x00000000), // startColor (transparent)
                        Color(0xFFAFDAD8), // centerColor
                        Color(0xFFE5F0B7)  // endColor
                    )
                )
                toolbarRes = R.drawable.maintoolbarasar
                vectorRes = R.drawable.status_bar_mosque_svg
                isBlack = true
            }
            Constants.SUNSET, Constants.MAGHRIB -> {
                nextPrayer = Constants.MAGHRIB
                nextPrayerTime2 = prayerTimes[nextPrayer]
                nextPrayerTimeCalendr =
                    convertTimeStringToCalendar(nextPrayerTime2!!, Constants.TODAY)

                statusBarColor = BgtobarColorMagrib
                backgroundRes = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF503B5D),  // startColor
                        Color(0xFFF28D47) // endColor
                    )
                )
                toolbarRes = R.drawable.maintoolbarmagribnew
                vectorRes = R.drawable.status_bar_mosque_svg_light
                isBlack = false
            }
            Constants.ISHA -> {
                nextPrayer = Constants.ISHA
                nextPrayerTime2 = prayerTimes[nextPrayer]
                nextPrayerTimeCalendr =
                    convertTimeStringToCalendar(nextPrayerTime2!!, Constants.TODAY)

                statusBarColor = BgtobarColorIsha
                backgroundRes = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF374554), // startColor (left)
                        Color(0xFF475564), // centerColor (middle)
                        Color(0xFF4D5B6A)  // endColor (right)
                    )
                )
                toolbarRes = R.drawable.maintoolbarisha
                vectorRes = R.drawable.status_bar_mosque_svg_light
                isBlack = false
            }
            else -> {
                nextPrayer = Constants.FAJR
                nextPrayerTime2 = prayerTimes[nextPrayer]
                nextPrayerTimeCalendr = convertTimeStringToCalendar(nextPrayerTime2!!, Constants.NEXTDAY)
                statusBarColor = BgtobarColorFajr
                backgroundRes = Brush.verticalGradient(colors = listOf(
                    Color(0x00000000),
                    Color(0xFFE2C7CC)
                ))
                toolbarRes = R.drawable.maintoolbarfajar
                vectorRes = R.drawable.status_bar_mosque_svg
                isBlack = true
            }
        }

        val ui = PrayerUiState(
            currentPrayerName = nowPrayerName,
            currentPrayerTime = prayerTimes[nowPrayerName] ?: "",
            nextPrayerName = getPrayerNameLocalize(context, nextPrayer),
            nextPrayerTime = nextPrayerTime2 ?: "",
            remainingTime = "00:00",
            isBlack = isBlack,
            statusBarColor = statusBarColor,
            backgroundResBrush = backgroundRes,
            toolbarRes = toolbarRes,
            vectorRes = vectorRes,
            prayers = emptyList()
        )

        return Pair(ui, nextPrayerTimeCalendr)
    }

    fun getPrayerNameLocalize(context: Context , prayerTime: String?): String {
        return when (prayerTime) {
            "fajr" -> context.getString(R.string.fajar)
            "dhuzur" -> context.getString(R.string.dhuhr)
            "asr" -> context.getString(R.string.asr)
            "maghrib" -> context.getString(R.string.maghrib)
            "isha" -> context.getString(R.string.isha)
            else -> context.getString(R.string.fajar)
        }
    }

    private fun formatMillis(millis: Long): String {
        return try {
            val hours = TimeUnit.MILLISECONDS.toHours(millis)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
            val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
            String.format("%02d Hour %02d Minutes %02d Seconds ", hours, minutes, seconds)
        } catch (e: Exception) {
            "00"
        }
    }

    private fun createEmptyCalendar(): Calendar {
        return Calendar.getInstance()
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }

}



