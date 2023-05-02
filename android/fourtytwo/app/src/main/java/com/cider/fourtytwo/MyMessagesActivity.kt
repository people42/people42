package com.cider.fourtytwo

import android.content.ContentValues
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.cider.fourtytwo.dataStore.UserDataStore
import com.cider.fourtytwo.feed.FeedAdapter
import com.cider.fourtytwo.network.Api
import com.cider.fourtytwo.network.Model.*
import com.cider.fourtytwo.network.RetrofitInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MyMessagesActivity : AppCompatActivity() {
    private lateinit var userDataStore: UserDataStore
    val api = RetrofitInstance.getInstance().create(Api::class.java)
    private lateinit var feedAdapter: FeedAdapter
    private var feedList: List<RecentFeedData> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_messages)
        userDataStore = UserDataStore(this)
        lifecycleScope.launch {
            val token = userDataStore.get_access_token.first()
            getHistory(token)
        }
    }
    fun getHistory(header : String){
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDate = currentDate.format(formatter)
        Log.d(TAG, "getHistory: ${currentDate}")

        api.getHistory(header, formattedDate).enqueue(object : Callback<HistoryResponse> {
            override fun onResponse(call: Call<HistoryResponse>, response: Response<HistoryResponse>) {
                if (response.code() == 200) {
                    Log.i(ContentValues.TAG, "${response.body()?.data}")


                } else if (response.code() == 401){
                    Log.i(ContentValues.TAG, "401: 토큰 만료")
                    // 토큰 다시 받기
                    getToken()
                } else {
                    Log.i(ContentValues.TAG, "기타: $response")
                    Log.i(ContentValues.TAG, "기타: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<HistoryResponse>, t: Throwable) {
                // 실패
                Log.d("메세지 전송 2 실패: ", t.message.toString())
            }
        })
    }
    fun getToken() {
        lifecycleScope.launch {
            val refreshToken = userDataStore.get_refresh_token.first()
            api.setAccessToken(refreshToken).enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    response.body()?.let {
                        if (it.status == 200) {
                            Log.i(TAG, "토큰 전송 200: 유저 정보 저장")
                            Log.i(TAG, "토큰 전송 응답 바디 ${response.body()?.data?.accessToken}")
                            response.body()?.data?.let {
                                    it1 -> saveUserInfo(it1)
                            }
                        } else {
                            Log.i(TAG, "토큰 전송 실패 코드: ${response.code()}")
                        }
                    }
                }
                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    Log.d("토큰 전송 on failure: ", t.message.toString())
                }
            })
        }
    }
    fun saveUserInfo(payload : UserInfo){
        lifecycleScope.launch {
            userDataStore.setUserData(payload)
            val token = userDataStore.get_access_token.first()
            getHistory(token)
        }
    }
}