package com.cider.fourtytwo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cider.fourtytwo.dataStore.UserDataStore
import com.cider.fourtytwo.network.repository.NetworkRepository
import kotlinx.coroutines.launch

class IntroViewModel : ViewModel() {



    fun checkFirstFlag() = viewModelScope.launch {
        val getData = UserDataStore().getFirstData()
    }
    fun setUpFirstFlag() = viewModelScope.launch {
        UserDataStore().setupFirstData()
    }
}