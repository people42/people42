package com.cider.fourtytwo

import androidx.lifecycle.ViewModel
import com.cider.fourtytwo.network.repository.NetworkRepository

class UserViewModel() : ViewModel() {
    private val networkRepository = NetworkRepository()

//    fun getGoogleUser() = viewModelScope.launch {
////        val result = networkRepository.getGoogleUser()
//    }
//
//    fun setUserData(payload: UserInfo) = viewModelScope.launch{
//        UserDataStore().setUserData(payload)
//    }
//    fun setUserEmail(email:String) = viewModelScope.launch {
//        val getData:Boolean = UserDataStore().setUserEmail(email)
//        Log.i(TAG, "setUserEmail: $getData")
//    }
//    fun setUserEmoji(emoji:String) = viewModelScope.launch {
//        UserDataStore().setUserEmoji(emoji)
//    }
//    fun setUserNickname(navController: NavController, nickname: String) = viewModelScope.launch {
//        UserDataStore().setUserNickname(navController, nickname)
//    }
//    fun setUserIdToken(email:String) = viewModelScope.launch {
//        UserDataStore().setUserIdToken(email)
//    }
//    fun setUserAccessToken(accessToken:String) = viewModelScope.launch {
//        UserDataStore().setUserAccessToken(accessToken)
//    }
//    fun setUserRefreshToken(refreshToken:String) = viewModelScope.launch {
//        UserDataStore().setUserRefreshToken(refreshToken)
//    }
//    fun getUserAccessToken() = viewModelScope.launch {
//        UserDataStore().getUserAccessToken()
//    }
//    fun getUserRefreshToken() = viewModelScope.launch {
//        UserDataStore().getUserRefreshToken()
//    }
//    fun getSignupInfo() = viewModelScope.launch {
//        UserDataStore().getSignupInfo()
//    }

}