package com.cit.mycomposeapplication.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cit.mycomposeapplication.ui.theme.MyComposeApplicationTheme
import com.cit.mycomposeapplication.utils.sdp
import kotlin.jvm.java

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyComposeApplicationTheme {
                Scaffold(
                    content = { innerPadding ->
                        Surface(
                            modifier = Modifier.padding(innerPadding),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            // Your main content
                            MainScreen()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun logs(){
    Log.d("MainAct", "MainScreenlogs: custom10 ${10.sdp()}")
    Log.d("MainAct", "MainScreenlogs: custom12 ${12.sdp()}")
    Log.d("MainAct", "MainScreenlogs: custom25 ${25.sdp()}")
    Log.d("MainAct", "MainScreenlogs: custom37 ${37.sdp()}")
    Log.d("MainAct", "MainScreenlogs: custom42 ${42.sdp()}")
    Log.d("MainAct", "MainScreenlogs: custom56 ${56.sdp()}")
    Log.d("MainAct", "MainScreenlogs: custom68 ${68.sdp()}")
    Log.d("MainAct", "MainScreenlogs: custom79 ${79.sdp()}")
    Log.d("MainAct", "MainScreenlogs: custom85 ${85.sdp()}")
    Log.d("MainAct", "MainScreenlogs: custom97 ${97.sdp()}")
    Log.d("MainAct", "MainScreenlogs: custom105 ${105.sdp()}")
    Log.d("MainAct", "MainScreenlogs: custom118 ${118.sdp()}")
    Log.d("MainAct", "MainScreenlogs: custom127 ${127.sdp()}")
    Log.d("MainAct", "MainScreenlogs: custom136 ${136.sdp()}")
    Log.d("MainAct", "MainScreenlogs: custom149 ${149.sdp()}")
    Log.d("MainAct", "MainScreenlogs: custom158 ${158.sdp()}")
    Log.d("MainAct", "MainScreenlogs: custom167 ${167.sdp()}")
    Log.d("MainAct", "MainScreenlogs: custom182 ${182.sdp()}")
    Log.d("MainAct", "MainScreenlogs: custom195 ${195.sdp()}")
    Log.d("MainAct", "MainScreenlogs: custom210 ${210.sdp()}")
    Log.d("MainAct", "MainScreenlogs: custom225 ${225.sdp()}")
    Log.d("MainAct", "MainScreenlogs: custom238 ${238.sdp()}")
    Log.d("MainAct", "MainScreenlogs: custom250 ${250.sdp()}")

}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(), // ← MOVED HERE
        contentAlignment = Alignment.Center
    ) {
        CenteredButtonScreen()
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Composable
fun CenteredButtonScreen() {
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp) // spacing like margin
        ) {
            Button(
                onClick = {
                    val intent = Intent(context, QuranPageReadActivity::class.java)
                    context.startActivity(intent)
                }
            ) {
                Text("Open Read Page")
            }

            Button(
                onClick = {
                    val intent = Intent(context, MainActivityDashBoard::class.java)
                    context.startActivity(intent)
                }
            ) {
                Text("Dashboard")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyComposeApplicationTheme {
        CenteredButtonScreen()
    }
}