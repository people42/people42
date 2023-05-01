package com.cider.fourtytwo.network.repository

import com.cider.fourtytwo.network.Api
import com.cider.fourtytwo.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject

class NetworkRepository {
    private val client = RetrofitInstance.getInstance().create(Api::class.java)
//    suspend fun getGoogleUser(params: HashMap<String, String>) = client.getGoogleUser(params)
//    fun getGoogleUser(): Call<UserResponse> {
//
//    }
}
