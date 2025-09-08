package com.cit.mycomposeapplication.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cit.mycomposeapplication.R
import com.cit.mycomposeapplication.database.DatabaseAccess
import com.cit.mycomposeapplication.models.ReciterUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class ReciterViewModel(application: Application) : AndroidViewModel(application) {

    private val _reciters = MutableLiveData<List<ReciterUiModel>>()
    val reciters: LiveData<List<ReciterUiModel>> = _reciters

    fun loadReciters(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val readersList = DatabaseAccess(context).allReaders
            val filteredReaders = readersList.filter { reader ->
                reader.audioType == 0 &&
                        (Locale.getDefault().displayLanguage == "العربية" || Locale.getDefault().displayLanguage != "العربية")
            }

            val readersImages = listOf(
                R.drawable.qari_1_abdbasit,
                R.drawable.qari_1_abdbasit,
                R.drawable.qari_2_abdsudais,
                R.drawable.qari_5_basfar,
                R.drawable.qari_6_shatri,
                R.drawable.qari_7_alirahman,
                R.drawable.qari_8_rifai,
                R.drawable.qari_9_unknown,
                R.drawable.qari_10_muaiqly,
                R.drawable.qari_11_khaleel,
                R.drawable.qari_3_rashidalfasy,
                R.drawable.qari_9_unknown,
                R.drawable.qari_12_jibreel,
                R.drawable.qari_9_unknown,
                R.drawable.qari_13_minshawi,
                R.drawable.qari_15_sadghamdi,
                R.drawable.qari_14_shuraym,
                R.drawable.qari_16_yassir
            )

            val uiList = filteredReaders.mapIndexedNotNull { index, reader ->
                readersImages.getOrNull(index)?.let { imageRes ->
                    ReciterUiModel(reader, imageRes)
                }
            }

            withContext(Dispatchers.Main) {
                _reciters.value = uiList
            }
        }
    }
}
