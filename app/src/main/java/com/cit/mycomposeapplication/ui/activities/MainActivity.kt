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
    Log.d("MainAct", "MainScreenlogs: sdp10 ${(dimensionResource(id = com.intuit.sdp.R.dimen._10sdp).value)}, custom10 ${10.sdp()}")
    Log.d("MainAct", "MainScreenlogs: sdp12 ${(dimensionResource(id = com.intuit.sdp.R.dimen._12sdp).value)}, custom12 ${12.sdp()}")
    Log.d("MainAct", "MainScreenlogs: sdp25 ${(dimensionResource(id = com.intuit.sdp.R.dimen._25sdp).value)}, custom25 ${25.sdp()}")
    Log.d("MainAct", "MainScreenlogs: sdp37 ${(dimensionResource(id = com.intuit.sdp.R.dimen._37sdp).value)}, custom37 ${37.sdp()}")
    Log.d("MainAct", "MainScreenlogs: sdp42 ${(dimensionResource(id = com.intuit.sdp.R.dimen._42sdp).value)}, custom42 ${42.sdp()}")
    Log.d("MainAct", "MainScreenlogs: sdp56 ${(dimensionResource(id = com.intuit.sdp.R.dimen._56sdp).value)}, custom56 ${56.sdp()}")
    Log.d("MainAct", "MainScreenlogs: sdp68 ${(dimensionResource(id = com.intuit.sdp.R.dimen._68sdp).value)}, custom68 ${68.sdp()}")
    Log.d("MainAct", "MainScreenlogs: sdp79 ${(dimensionResource(id = com.intuit.sdp.R.dimen._79sdp).value)}, custom79 ${79.sdp()}")
    Log.d("MainAct", "MainScreenlogs: sdp85 ${(dimensionResource(id = com.intuit.sdp.R.dimen._85sdp).value)}, custom85 ${85.sdp()}")
    Log.d("MainAct", "MainScreenlogs: sdp97 ${(dimensionResource(id = com.intuit.sdp.R.dimen._97sdp).value)}, custom97 ${97.sdp()}")
    Log.d("MainAct", "MainScreenlogs: sdp105 ${(dimensionResource(id = com.intuit.sdp.R.dimen._105sdp).value)}, custom105 ${105.sdp()}")
    Log.d("MainAct", "MainScreenlogs: sdp118 ${(dimensionResource(id = com.intuit.sdp.R.dimen._118sdp).value)}, custom118 ${118.sdp()}")
    Log.d("MainAct", "MainScreenlogs: sdp127 ${(dimensionResource(id = com.intuit.sdp.R.dimen._127sdp).value)}, custom127 ${127.sdp()}")
    Log.d("MainAct", "MainScreenlogs: sdp136 ${(dimensionResource(id = com.intuit.sdp.R.dimen._136sdp).value)}, custom136 ${136.sdp()}")
    Log.d("MainAct", "MainScreenlogs: sdp149 ${(dimensionResource(id = com.intuit.sdp.R.dimen._149sdp).value)}, custom149 ${149.sdp()}")
    Log.d("MainAct", "MainScreenlogs: sdp158 ${(dimensionResource(id = com.intuit.sdp.R.dimen._158sdp).value)}, custom158 ${158.sdp()}")
    Log.d("MainAct", "MainScreenlogs: sdp167 ${(dimensionResource(id = com.intuit.sdp.R.dimen._167sdp).value)}, custom167 ${167.sdp()}")
    Log.d("MainAct", "MainScreenlogs: sdp182 ${(dimensionResource(id = com.intuit.sdp.R.dimen._182sdp).value)}, custom182 ${182.sdp()}")
    Log.d("MainAct", "MainScreenlogs: sdp195 ${(dimensionResource(id = com.intuit.sdp.R.dimen._195sdp).value)}, custom195 ${195.sdp()}")
    Log.d("MainAct", "MainScreenlogs: sdp210 ${(dimensionResource(id = com.intuit.sdp.R.dimen._210sdp).value)}, custom210 ${210.sdp()}")
    Log.d("MainAct", "MainScreenlogs: sdp225 ${(dimensionResource(id = com.intuit.sdp.R.dimen._225sdp).value)}, custom225 ${225.sdp()}")
    Log.d("MainAct", "MainScreenlogs: sdp238 ${(dimensionResource(id = com.intuit.sdp.R.dimen._238sdp).value)}, custom238 ${238.sdp()}")
    Log.d("MainAct", "MainScreenlogs: sdp250 ${(dimensionResource(id = com.intuit.sdp.R.dimen._250sdp).value)}, custom250 ${250.sdp()}")

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