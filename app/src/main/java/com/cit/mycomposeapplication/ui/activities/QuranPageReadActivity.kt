package com.cit.mycomposeapplication.ui.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.cit.mycomposeapplication.composable.QuranReaderApp
import com.cit.mycomposeapplication.models.AudioCommand
import com.cit.mycomposeapplication.repository.AndroidQuranRepository
import com.cit.mycomposeapplication.ui.theme.MyComposeApplicationTheme
import com.cit.mycomposeapplication.utils.ConstantsKT.SCRIPT_INDOPAK
import com.cit.mycomposeapplication.viewmodel.QuranViewModel
import java.io.File

class QuranPageReadActivity : ComponentActivity() {

    private lateinit var viewModel: QuranViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repo = AndroidQuranRepository(applicationContext)
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return QuranViewModel(repo, applicationContext) as T
            }
        })[QuranViewModel::class.java]



        if (viewModel.isDatabaseAlreadyCopied().not()) {
            Log.e("QuranPageReadActivity", "if Database not found. Copying now...")
            viewModel.copyDatabase { success ->
                viewModel.copyDatabasePages { success ->
                    viewModel.copyDatabasePagesIndopak { success ->
                        // configure VM: asset path + total pages (example)
                        val assetPath = viewModel.getAbsoluteAssetPathArg(SCRIPT_INDOPAK)

                        if (assetPath == null) {
                            Log.d("QuranPageReadActivity", "if checkDownload: Path is null")
                        }else{
                            val assetDir = File(assetPath)
                            if (!assetDir.exists() || assetDir.listFiles().isNullOrEmpty()) {
                                Log.d("QuranPageReadActivity", "if checkDownload: Directory does not exist or is empty")
                            }

                        }

                        viewModel.setAssetPath( assetPath ?: "")
                        viewModel.setTotalPages(5) // or your getPage()
                    }
                }
            }
        }
        else {
            // configure VM: asset path + total pages (example)
            val assetPath = viewModel.getAbsoluteAssetPathArg(SCRIPT_INDOPAK)

            if (assetPath == null) {
                Log.d("QuranPageReadActivity", "checkDownload: Path is null")
            }else{
                val assetDir = File(assetPath)
                if (!assetDir.exists() || assetDir.listFiles().isNullOrEmpty()) {
                    Log.d("QuranPageReadActivity", "checkDownload: Directory does not exist or is empty")
                }
            }



            viewModel.setAssetPath( assetPath ?: "")
            viewModel.setTotalPages(5) // or your getPage()
        }

        // collect audio commands from VM
        lifecycleScope.launchWhenStarted {
            viewModel.audioCommands.collect { cmd ->
                // handle audioCommand: route to existing functions (audioservicefunction, show toolbar/footer, etc.)
                when (cmd) {
                    is AudioCommand.PlaySingle -> {
                        // call your activity method to start audio
                        audioservicefunction(cmd.ayaId, cmd.pageId, cmd.readerId, cmd.ayaId, cmd.suraId, null)
                    }
                    is AudioCommand.PlayFrom -> {
                        // call audioservicefunction with start pos
                    }
                }
            }
        }

        setContent {
            val totalPages by viewModel.totalPages.collectAsState()
            QuranReaderApp(
                viewModel = viewModel,
                pageCount = totalPages,
                nightMode = false
            ) { audioObj ->
                // if you want to handle quick lambda audio actions (optional)
            }
        }
    }

    // Example placeholder for existing audio function you had in activity
    fun audioservicefunction(startAyaPos:Int, pageId:Int, readerID:Int, ayaID:Int, suraID:Int, downloadLink:String?) {
        // call your existing audio / service logic
    }
}

@Composable
fun Greeting2(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    MyComposeApplicationTheme {
        Greeting2("Android")
    }
}
