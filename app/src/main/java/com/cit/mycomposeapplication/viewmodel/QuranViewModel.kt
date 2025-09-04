package com.cit.mycomposeapplication.viewmodel

import com.cit.mycomposeapplication.R
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cit.mycomposeapplication.models.AudioCommand
import com.cit.mycomposeapplication.models.AyaRect
import com.cit.mycomposeapplication.models.HighlightCommand
import com.cit.mycomposeapplication.repository.QuranRepository
import com.cit.mycomposeapplication.utils.Constants.SCRIPT_USMANI
import com.cit.mycomposeapplication.utils.FilenameUtils
import com.google.android.play.core.assetpacks.AssetPackManagerFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.collections.set

class QuranViewModel(private val repo: QuranRepository, private val appContext: Context) : ViewModel() {

    // UI state
    private val _currentPageIndex = MutableStateFlow(0)
    val currentPageIndex: StateFlow<Int> = _currentPageIndex.asStateFlow()

    private val _totalPages = MutableStateFlow(5) // default - override if needed
    val totalPages: StateFlow<Int> = _totalPages.asStateFlow()

    private val _assetPath = MutableStateFlow<String>("")
    val assetPath: StateFlow<String> = _assetPath.asStateFlow()

    // highlight commands — activity or any producer can emit to this SharedFlow
    private val _highlightCommands = MutableSharedFlow<HighlightCommand>(extraBufferCapacity = 8)
    val highlightCommands: SharedFlow<HighlightCommand> = _highlightCommands.asSharedFlow()

    // reset events
    private val _resetRequests = MutableSharedFlow<Unit>(extraBufferCapacity = 4)
    val resetRequests: SharedFlow<Unit> = _resetRequests.asSharedFlow()

    // audio / ui commands to be handled by Activity
    private val _audioCommands = MutableSharedFlow<AudioCommand>(extraBufferCapacity = 4)
    val audioCommands: SharedFlow<AudioCommand> = _audioCommands.asSharedFlow()

    // Cached bitmaps per page (ImageBitmap)
    private val bitmapCache = mutableMapOf<Int, androidx.compose.ui.graphics.ImageBitmap?>()
    private val _pageBitmapStates = MutableStateFlow<Map<Int, androidx.compose.ui.graphics.ImageBitmap?>>(emptyMap())
    val pageBitmapStates: StateFlow<Map<Int, androidx.compose.ui.graphics.ImageBitmap?>> = _pageBitmapStates.asStateFlow()

    private val assetPackManager = AssetPackManagerFactory.getInstance(appContext)

    val onDemandAssetPack = "on_demand_quran"
    val onDemandAssetPackIndoPak = "on_demand_quran_indopak"

    private val pathUsmani = "usmani"
    private val pathIndopak = "indopak"


    fun setAssetPath(path: String) {
        _assetPath.value = path
    }

    fun setTotalPages(total: Int) { _totalPages.value = total }

    fun setCurrentPage(index: Int) { _currentPageIndex.value = index }

    fun emitReset() { _resetRequests.tryEmit(Unit) }

    fun emitHighlight(sora:Int, aya:Int, page:Int) {
        _highlightCommands.tryEmit(HighlightCommand(sora, aya, page))
    }

    fun emitAudioCommand(cmd: AudioCommand) {
        _audioCommands.tryEmit(cmd)
    }

    fun loadBitmapForPage(pageNo: Int) {
        if (bitmapCache.containsKey(pageNo)) return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val path = _assetPath.value
                val file: File = repo.getQuranPageImageFileFromAssets(pageNo, path)
                Log.d("QuranViewModel", "loadBitmapForPage: (88) $path ::  $file");
                val options = BitmapFactory.Options().apply {
                    inPreferredConfig = Bitmap.Config.RGB_565
                    inDither = true
                    inMutable = true
                }
                val bm: Bitmap? = if (file.exists()) BitmapFactory.decodeFile(file.absolutePath, options) else null
                val imageBitmap = bm?.asImageBitmap()
                bitmapCache[pageNo] = imageBitmap
                _pageBitmapStates.value = bitmapCache.toMap()
            } catch (e: Exception) {
                // fallback empty 1x1 white bitmap
                val empty = Bitmap.createBitmap(1,1, Bitmap.Config.ARGB_8888)
                empty.eraseColor(android.graphics.Color.WHITE)
                bitmapCache[pageNo] = empty.asImageBitmap()
                _pageBitmapStates.value = bitmapCache.toMap()
            }
        }
    }

    // helper to query aya rects for a page/sora/aya
    suspend fun getAyaRects(pageNumber: Int, soraNumber: Int, ayaNumber: Int): List<AyaRect> {
        return repo.getAyaRectsMapBoth(pageNumber, soraNumber, ayaNumber)
    }

    // helper to get touched aya
    suspend fun getTouchedAya(pageNumber:Int, x:Float, y:Float) = repo.getTouchedAya(pageNumber, x, y)


    fun getAbsoluteAssetPathArg(scriptType : Int): String? {
        Log.d("QuranPagesVM  ", "getAbsoluteAssetPath  : line(198) : ");
        val assetPackPath = assetPackManager.getPackLocation(getScriptAssetPackArg(scriptType))
            ?: // asset pack is not ready
            return null
        val assetsFolderPath = assetPackPath.assetsPath()
        return FilenameUtils.concat(assetsFolderPath, getScriptAssetFolderPath(scriptType))
    }

    fun getScriptAssetFolderPath(scriptType : Int): String {
        Log.d("QuranPagesVM  ", "getScriptAssetFolderPath  : line(318) : ");
        return if (scriptType == SCRIPT_USMANI) {
            pathUsmani
        } else {
            pathIndopak
        }
    }

    fun getScriptAssetPackArg(scriptType : Int): String {
        return if (scriptType == SCRIPT_USMANI) {
            onDemandAssetPack
        } else {
            onDemandAssetPackIndoPak
        }
    }

    fun copyDatabase(onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.e("QuranViewModel", "Starting copyDatabase")

                val databaseDirectory = File(
                    appContext.filesDir.toString() + "/" + appContext.getString(R.string.app_folder_path)
                )

                if (!databaseDirectory.exists()) {
                    databaseDirectory.mkdirs()
                }

                val databaseFile = File(databaseDirectory, "quran.sqlite")
                databaseFile.parentFile?.mkdirs()

                if (!databaseFile.exists()) {
                    databaseFile.createNewFile()
                }

                val inputStream = appContext.assets.open("quran.sqlite")
                val outputStream = FileOutputStream(databaseFile)
                val buffer = ByteArray(1024)

                var read: Int
                while (inputStream.read(buffer).also { read = it } != -1) {
                    outputStream.write(buffer, 0, read)
                }

                outputStream.flush()
                outputStream.close()
                inputStream.close()

                Log.e("QuranViewModel", "copyDatabase success")
                withContext(Dispatchers.Main) { onResult(true) }

            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("QuranViewModel", "copyDatabase failed: ${e.message}")
                withContext(Dispatchers.Main) { onResult(false) }
            }
        }
    }

    fun copyDatabasePages(onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.e("QuranViewModel", "Starting copyDatabase pages")

                val databaseDirectory = File(
                    appContext.filesDir.toString() + "/" + appContext.getString(R.string.app_folder_path)
                )

                if (!databaseDirectory.exists()) {
                    databaseDirectory.mkdirs()
                }

                val databaseFile = File(databaseDirectory, "quranpages.sqlite")
                databaseFile.parentFile?.mkdirs()

                if (!databaseFile.exists()) {
                    databaseFile.createNewFile()
                }

                val inputStream = appContext.assets.open("quranpages.sqlite")
                val outputStream = FileOutputStream(databaseFile)
                val buffer = ByteArray(1024)

                var read: Int
                while (inputStream.read(buffer).also { read = it } != -1) {
                    outputStream.write(buffer, 0, read)
                }

                outputStream.flush()
                outputStream.close()
                inputStream.close()

                Log.e("QuranViewModel", "copyDatabase pages success")
                withContext(Dispatchers.Main) { onResult(true) }

            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("QuranViewModel", "copyDatabase pages failed: ${e.message}")
                withContext(Dispatchers.Main) { onResult(false) }
            }
        }
    }

    fun copyDatabasePagesIndopak(onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.e("QuranViewModel", "Starting copyDatabase pages")

                val databaseDirectory = File(
                    appContext.filesDir.toString() + "/" + appContext.getString(R.string.app_folder_path)
                )

                if (!databaseDirectory.exists()) {
                    databaseDirectory.mkdirs()
                }

                val databaseFile = File(databaseDirectory, "quranpages_info_indopak.db")
                databaseFile.parentFile?.mkdirs()

                if (!databaseFile.exists()) {
                    databaseFile.createNewFile()
                }

                val inputStream = appContext.assets.open("quranpages_info_indopak.db")
                val outputStream = FileOutputStream(databaseFile)
                val buffer = ByteArray(1024)

                var read: Int
                while (inputStream.read(buffer).also { read = it } != -1) {
                    outputStream.write(buffer, 0, read)
                }

                outputStream.flush()
                outputStream.close()
                inputStream.close()

                Log.e("QuranViewModel", "copyDatabase pages success")
                withContext(Dispatchers.Main) { onResult(true) }

            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("QuranViewModel", "copyDatabase pages failed: ${e.message}")
                withContext(Dispatchers.Main) { onResult(false) }
            }
        }
    }

    fun isDatabaseAlreadyCopied(): Boolean {
        val databaseFile = File(appContext.filesDir.toString() + "/" +
                appContext.getString(R.string.app_folder_path) + "/quran.sqlite"
        )
        val databaseFilePages = File(appContext.filesDir.toString() + "/" +
                appContext.getString(R.string.app_folder_path) + "/quranpages.sqlite"
        )
        val databaseFilePagesIndopak = File(appContext.filesDir.toString() + "/" +
                appContext.getString(R.string.app_folder_path) + "/quranpages_info_indopak.db"
        )

        return databaseFile.exists() && databaseFilePages.exists() && databaseFilePagesIndopak.exists()
    }

}
