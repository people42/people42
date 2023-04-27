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
//    private val client = RetrofitInstance.getInstance().create(Api::class.java)
//    fun getUser(params: HashMap<String, String>) = client.getUserGoogle(params)
suspend fun getAccessToken(idToken: String): String {
    val requestBody: RequestBody = FormBody.Builder()
        .add("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer")
        .add("assertion", idToken)
        .build()
    val request: Request = Request.Builder()
        .url("https://oauth2.googleapis.com/token")
        .post(requestBody)
        .build()
    val client = OkHttpClient()
    val response = withContext(Dispatchers.IO) {
        client.newCall(request).execute()
    }
    val responseData = response.body()?.string()
    val jsonObject = JSONObject(responseData)
    return jsonObject.getString("access_token")
}
//    suspend fun getAccessToken(idToken: String): String {
//        val requestBody: RequestBody = FormBody.Builder()
//            .add("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer")
//            .add("assertion", idToken)
//            .build()
//        val request: Request = Request.Builder()
//            .url("https://oauth2.googleapis.com/token")
//            .post(requestBody)
//            .build()
//        val client = OkHttpClient()
//        val response = withContext(Dispatchers.IO) {
//            client.newCall(request).execute()
//        }
//        val responseData = response.body()?.string()
//        val jsonObject = JSONObject(responseData)
//        return jsonObject.getString("access_token")
//    }
}
