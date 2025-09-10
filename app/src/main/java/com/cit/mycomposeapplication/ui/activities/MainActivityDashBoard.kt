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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cit.mycomposeapplication.composable.MainHeader
import com.cit.mycomposeapplication.models.ButtonType
import com.cit.mycomposeapplication.models.PrayerUiState
import com.cit.mycomposeapplication.models.Reader
import com.cit.mycomposeapplication.repository.AndroidQuranRepository
import com.cit.mycomposeapplication.ui.activities.ui.theme.MyComposeApplicationTheme
import com.cit.mycomposeapplication.utils.AppSettings
import com.cit.mycomposeapplication.utils.ConstantsKT.SCRIPT_INDOPAK
import com.cit.mycomposeapplication.viewmodel.AyahViewModel
import com.cit.mycomposeapplication.viewmodel.LocationViewModel
import com.cit.mycomposeapplication.viewmodel.QuranViewModel
import com.cit.mycomposeapplication.viewmodel.ReciterViewModel
import java.io.File

class MainActivityDashBoard : ComponentActivity() {

    private val locationViewModel: LocationViewModel by viewModels()

    private val viewModelAyah: AyahViewModel by viewModels()
    private val viewModelReciters: ReciterViewModel by viewModels()
    private lateinit var viewModelDB: QuranViewModel


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

        val repo = AndroidQuranRepository(applicationContext)
        viewModelDB = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return QuranViewModel(repo, applicationContext) as T
            }
        })[QuranViewModel::class.java]

        checkCopyDB()
        viewModelAyah.fetchAyah()
//        viewModelAyah.initGetTasbeehDaily()


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
                            ayahViewModel = viewModelAyah,
                            reciterViewModel = viewModelReciters,
                            onButtonClick = ::handleButtonClick,
                            onReciterClick = ::handleRecitersClick,
                            onSendMessage =  ::handleClick
                        )
                    }
                }
            }
        }
    }

    private fun handleButtonClick(buttonType: ButtonType) {
        Log.d("MainActivityDashBoard", "handleButtonClick: (82) $buttonType");
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
            ButtonType.CARD_AYAH -> gotoActivity(buttonType)
            ButtonType.CARD_TASBEEH -> gotoActivity(buttonType)
        }
    }

    private fun handleRecitersClick(reader: Reader) {
        Log.d("MainActivityDashBoard", "handleRecitersClick: (104) $reader");
        gotoActivity()
    }

    private fun handleClick() {
        Log.d("MainActivityDashBoard", "handleRecitersClick: (127)");
        gotoActivity()
    }

    fun gotoActivity(buttonType: ButtonType) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun gotoActivity() {
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

    private fun checkCopyDB(){
        if (viewModelDB.isDatabaseAlreadyCopied().not()) {
            Log.e("QuranPageReadActivity", "if Database not found. Copying now...")
            viewModelDB.copyDatabase { success ->
                viewModelDB.copyDatabasePages { success ->
                    viewModelDB.copyDatabasePagesIndopak { success ->
                        // configure VM: asset path + total pages (example)
                        val assetPath = viewModelDB.getAbsoluteAssetPathArg(SCRIPT_INDOPAK)

                        if (assetPath == null) {
                            Log.d("QuranPageReadActivity", "if checkDownload: Path is null")
                        }else{
                            val assetDir = File(assetPath)
                            if (!assetDir.exists() || assetDir.listFiles().isNullOrEmpty()) {
                                Log.d("QuranPageReadActivity", "if checkDownload: Directory does not exist or is empty")
                            }

                        }

                        viewModelReciters.loadReciters(this)
                    }
                }
            }
        }
        else {
            viewModelReciters.loadReciters(this)
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
                    // Step 1: Check/request location permission
                    if (locationViewModel.isPermissionGranted()) {
                        locationViewModel.requestLocationUpdates()
                        locationViewModel.fetchUserLocation()
                        observeUserLocation()
                    } else {
                        requestLocationpermission()
                    }
                    checkCopyDB()
                    viewModelAyah.fetchAyah()
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
            onButtonClick = {},
            onReciterClick = {},
            onSendMessage = {}
        )
    }
}
