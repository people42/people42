package com.cider.fourtytwo

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cider.fourtytwo.dataStore.UserDataStore
import com.cider.fourtytwo.feed.FeedAdapter
import com.cider.fourtytwo.feed.RecentFeedData
import com.cider.fourtytwo.feed.RecentFeedResponse
import com.cider.fourtytwo.network.Api
import com.cider.fourtytwo.network.RetrofitInstance
import com.cider.fourtytwo.notification.NotiAdapter
import com.cider.fourtytwo.notification.NotiItem
import com.cider.fourtytwo.notification.NotiResponse
import com.cider.fourtytwo.place.PlaceActivity
import com.cider.fourtytwo.signIn.UserInfo
import com.cider.fourtytwo.signIn.UserResponse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationActivity : AppCompatActivity() {
    private lateinit var userDataStore: UserDataStore
    val api: Api = RetrofitInstance.getInstance().create(Api::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        // 화면이 구성될 때, 메인 테마로 변경
        setTheme(R.style.Theme_Fourtytwo)
        // 로고 장착
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayUseLogoEnabled(true)
        supportActionBar?.title = "새로운 알림"
        supportActionBar?.elevation = 0.0F  // 상자 그림자 삭제
        supportActionBar?.setLogo(R.drawable.baseline_arrow_back_ios_new_24) // 뒤로가기이미지

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        userDataStore = UserDataStore(this)
        lifecycleScope.launch {
            getNoti(userDataStore.get_access_token.first())
        }
    }

    private fun getNoti(header : String){
        var notiList : List<NotiItem> = ArrayList()
        api.getNoti(header).enqueue(object : Callback<NotiResponse> {
            override fun onResponse(call: Call<NotiResponse>, response: Response<NotiResponse>) {
                Log.d("getNoti 응답", response.toString())
                if (response.code() == 200) {
                    notiList = response.body()!!.data
                    Log.i(ContentValues.TAG, "getRecentFeed_onResponse feedList: $notiList")
                    if (notiList.isNotEmpty()){
                        val noti = findViewById<RecyclerView>(R.id.notification_recyclerview)
                        var notiAdapter: NotiAdapter = NotiAdapter(this@NotificationActivity, notiList)
                        //feedAdapter.notifyDataSetChanged()
                        noti.adapter = notiAdapter
                        noti.layoutManager = LinearLayoutManager(this@NotificationActivity, LinearLayoutManager.VERTICAL, false)
                    }
                } else if (response.code() == 401){
                    Log.i(ContentValues.TAG, "getRecentFeed_onResponse 401: 토큰 만료")
                    getToken()
                } else {
                    Log.i(ContentValues.TAG, "getRecentFeed_onResponse 코드: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<NotiResponse>, t: Throwable) {
                Log.d("gerRecentFeed_onFailure", t.message.toString())
            }
        })
        if (notiList.isEmpty()) {

        }
    }
    fun getToken() {
        lifecycleScope.launch {
            val refreshToken = userDataStore.get_refresh_token.first()
            api.setAccessToken(refreshToken).enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    response.body()?.let {
                        if (it.status == 200) {
                            Log.i(ContentValues.TAG, "토큰 전송 응답 바디 ${response.body()?.data?.accessToken}")
                            response.body()?.data?.let {
                                    it1 -> saveUserInfo(it1)
                            }
                        } else {
                            Log.i(ContentValues.TAG, "토큰 전송 실패 코드: ${response.code()}")
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
            getNoti(token)
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}