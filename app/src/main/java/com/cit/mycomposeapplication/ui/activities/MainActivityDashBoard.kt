package com.cit.mycomposeapplication.ui.activities

import  com.cit.mycomposeapplication.R
import android.Manifest
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.cit.mycomposeapplication.composable.MainHeader
import com.cit.mycomposeapplication.models.ButtonType
import com.cit.mycomposeapplication.models.PrayerUiState
import com.cit.mycomposeapplication.ui.activities.ui.theme.MyComposeApplicationTheme
import com.cit.mycomposeapplication.utils.AppSettings
import com.cit.mycomposeapplication.viewmodel.LocationViewModel

class MainActivityDashBoard : ComponentActivity() {

    private val locationViewModel: LocationViewModel by viewModels()

    private val REQUEST_LOCATION_PERM = 115

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Step 1: Check/request location permission
        if (locationViewModel.isPermissionGranted()) {
            locationViewModel.requestLocationUpdates()
            locationViewModel.fetchUserLocation()
            observeUserLocation()
        } else {
            requestLocationpermission()
        }


        setContent {
            MyComposeApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        MainHeader(
                            locationViewModel = locationViewModel,
                            onButtonClick = ::handleButtonClick
                        )
                    }
                }
            }
        }
    }

    private fun handleButtonClick(buttonType: ButtonType) {
        when (buttonType) {
            ButtonType.ALQURAN -> gotoActivity(buttonType)
            ButtonType.QIBLA -> gotoActivity(buttonType)
            ButtonType.TASBEEH -> gotoActivity(buttonType)
            ButtonType.AZKAR -> gotoActivity(buttonType)
            ButtonType.PRAYERS -> gotoActivity(buttonType)
            ButtonType.CALENDAR -> gotoActivity(buttonType)
            ButtonType.HAJJ_UMRAH -> gotoActivity(buttonType)
            ButtonType.MASJID -> gotoActivity(buttonType)
            ButtonType.NAMES_NABI -> gotoActivity(buttonType)
        }
    }

    fun gotoActivity(buttonType: ButtonType) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }


    private fun observeUserLocation() {
        locationViewModel.userLocation.observe(this) { location ->
            if (location != null) {
                // Step 3: Initialize ViewModel with location
                locationViewModel.init(
                    latFor = location.latitude,
                    lngFor = location.longitude,
                    context = this,
                    settings = AppSettings(this) // example
                )
            }
            else{

            }
        }
    }


    fun requestLocationpermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                REQUEST_LOCATION_PERM
            )
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_LOCATION_PERM -> {
                if (ContextCompat.checkSelfPermission(
                        this@MainActivityDashBoard,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(
                        this@MainActivityDashBoard,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this@MainActivityDashBoard,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    ) {
                        ActivityCompat.requestPermissions(
                            this@MainActivityDashBoard, arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ), REQUEST_LOCATION_PERM
                        )
                    } else {
                        Log.e(" MainActivityDashBoard", " onRequestPermissionsResult: 112 ")
                    }
                } else {
                    Log.e(" MainActivityDashBoard", " onRequestPermissionsResult: 1607 ")
                }
            }

            9998 -> {
                if (ContextCompat.checkSelfPermission(
                        this@MainActivityDashBoard,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(
                        this@MainActivityDashBoard,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this@MainActivityDashBoard,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    ) {
                        ActivityCompat.requestPermissions(
                            this@MainActivityDashBoard, arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ), REQUEST_LOCATION_PERM
                        )
                    } else {
                        Log.e(" MainActivityDashBoard", " onRequestPermissionsResult: 142 ")
//                        if (!isGpsPermissionDialogShown) {
//                            isGpsPermissionDialogShown = true
//                            showDialogSettings()
//                        } else {
//
//                        }
                    }
                } else {
                    Log.e(" MainActivityDashBoard", " onRequestPermissionsResult: 1636 ")
                }
            }

            2020 -> {
//                run {
//                    if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                        //valid to download or not
//                        requestLocationpermission()
//                    } else {
//                        requestLocationpermission()
//                    }
//                }
//                run { Log.e(" MainActivityDashBoard", " onRequestPermissionsResult: 1643 ") }
            }

            else -> {
                Log.e(" MainActivityDashBoard", " onRequestPermissionsResult: 1643 ")
            }
        }
    }
}




@Composable
fun Greeting3(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview3() {
    val fakeUiState = PrayerUiState(
        currentPrayerName = "Fajr",
        currentPrayerTime = "05:12 AM",
        nextPrayerName = "Dhuhr",
        nextPrayerTime = "12:30 PM",
        remainingTime = "07:18:00",
        isBlack = true,
        backgroundResBrush = Brush.verticalGradient(colors = listOf(Color(0xFFE2C7CC), Color(0x00000000)))
    )

    val fakeLocation = "Karachi, Pakistan"

    MyComposeApplicationTheme {
        MainHeader(
            locationViewModel = object : LocationViewModel(Application()) {
                override val uiState = MutableLiveData(fakeUiState)
                override val locationLiveData = MutableLiveData(fakeLocation)
            },
            onButtonClick = {}
        )
    }
}
