package com.cider.fourtytwo

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cider.fourtytwo.dataStore.UserDataStore
import com.cider.fourtytwo.myHistory.HistoryResponse
import com.cider.fourtytwo.myHistory.MyMessagesAdapter
import com.cider.fourtytwo.network.Api
import com.cider.fourtytwo.network.RetrofitInstance
import com.cider.fourtytwo.signIn.UserInfo
import com.cider.fourtytwo.signIn.UserResponse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MyMessagesActivity : AppCompatActivity(){
    private lateinit var userDataStore: UserDataStore
    val api = RetrofitInstance.getInstance().create(Api::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        // 화면이 구성될 때, 스플래시 테마에서 메인 테마로 변경
        setTheme(R.style.Theme_Fourtytwo)
        // 로고 장착
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayUseLogoEnabled(true)
        supportActionBar?.setLogo(R.drawable.baseline_arrow_back_ios_new_24)
        supportActionBar?.title = "나의 생각 기록"      // 타이틀
        supportActionBar?.elevation = 0.0F  // 그림자 삭제

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_messages)

        val myEmojiView = findViewById<ImageView>(R.id.main_guide_emoji)
//        val intent = intent
//        val bundle = intent.extras
//        val myEmoji = bundle!!.getString("myEmoji")
        val myEmoji = "robot"
        Glide.with(this).load("https://peoplemoji.s3.ap-northeast-2.amazonaws.com/emoji/animate/${myEmoji}.gif").into(myEmojiView)

        userDataStore = UserDataStore(this)
        lifecycleScope.launch {
            val token = userDataStore.get_access_token.first()
            getHistory(token)
        }
    }
    fun getHistory(header : String) {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDate = currentDate.format(formatter)
        api.getHistory(header, formattedDate).enqueue(object : Callback<HistoryResponse> {
            override fun onResponse(call: Call<HistoryResponse>, response: Response<HistoryResponse>) {
                if (response.code() == 200) {
                    val result = response.body()?.data!!
                    val history = findViewById<RecyclerView>(R.id.history_recyclerView)
                    val historyAdapter = MyMessagesAdapter(result)

//                    val itemTouchHelper = ItemTouchHelper(SwipeController(historyAdapter))
//                    // itemTouchHelper에 RecyclerView 부착
//                    itemTouchHelper.attachToRecyclerView(history)

                    history.adapter = historyAdapter
                    history.layoutManager = LinearLayoutManager(this@MyMessagesActivity, LinearLayoutManager.VERTICAL, false)

                } else if (response.code() == 401){
                    Log.i(TAG, "401: 토큰 만료")
                    getToken()
                } else {
                    Log.i(TAG, "기타: $response")
                }
            }
            override fun onFailure(call: Call<HistoryResponse>, t: Throwable) {
                Log.d("실패: ", t.message.toString())
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
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                return true
            }
            else -> {}
        }
        return super.onOptionsItemSelected(item)
    }
}