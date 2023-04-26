package com.cider.fourtytwo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cider.fourtytwo.network.repository.NetworkRepository
import kotlinx.coroutines.launch

class SelectViewModel : ViewModel() {
    private val networkRepository = NetworkRepository()

    fun getUser() = viewModelScope.launch {
//        val result = networkRepository.getUser()
    }
}