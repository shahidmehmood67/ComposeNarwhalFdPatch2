package com.cit.mycomposeapplication.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.cit.mycomposeapplication.R
import com.cit.mycomposeapplication.database.AyahUiState
import com.cit.mycomposeapplication.repository.AyahRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AyahViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AyahRepository(application)

    private val _ayahLiveData = MutableLiveData<AyahUiState?>()
    val ayahLiveData: LiveData<AyahUiState?> get() = _ayahLiveData

    private val _tasbeehOfDay = MutableLiveData<String>()
    val tasbeehOfDay: LiveData<String> = _tasbeehOfDay

    // Fetch Ayah from API & update LiveData
    fun fetchAyah() {
        viewModelScope.launch {
//            if (!isInternetAvailable2(getApplication())) {
//                getRandomAyah()
//                return@launch
//            }

            // Wait for the API result before proceeding
            val ayah = withContext(Dispatchers.IO) { repository.getAyah() }

            if (ayah == null) {
//                getRandomAyah() // Fetch random Ayah from DB if API fails
                Log.d("AyahViewModel", "fetchAyah: (31) ");
                initGetTasbeehDaily()
            } else {
//                getLastAyah() // Always return the last saved Ayah from DB
                Log.d("AyahViewModel", "fetchAyah: (35) $ayah");
                _ayahLiveData.postValue(ayah)
                initGetTasbeehDaily()
            }
        }
    }

    fun initGetTasbeehDaily() {
        viewModelScope.launch(Dispatchers.IO) {
            val zikarList = getApplication<Application>().resources.getStringArray(R.array.zikar_ar)

            if (zikarList.isNotEmpty()) {
                val randomZikar = zikarList.random()
                _tasbeehOfDay.postValue(randomZikar)
            }
        }
    }
}
