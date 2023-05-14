package com.cider.fourtytwo.myHistory

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cider.fourtytwo.MainActivity
import com.cider.fourtytwo.R
import com.cider.fourtytwo.SettingsActivity
import com.cider.fourtytwo.dataStore.UserDataStore
import com.cider.fourtytwo.network.Api
import com.cider.fourtytwo.network.Model.MessageResponse
import com.cider.fourtytwo.network.Model.NotiCntResponse
import com.cider.fourtytwo.network.RetrofitInstance
import com.cider.fourtytwo.signIn.UserInfo
import com.cider.fourtytwo.signIn.UserResponse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MyMessagesActivity : AppCompatActivity(){
    private lateinit var userDataStore: UserDataStore
    val api: Api = RetrofitInstance.getInstance().create(Api::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        // 화면이 구성될 때, 스플래시 테마에서 메인 테마로 변경
        setTheme(R.style.Theme_Fourtytwo)
        // 로고 장착
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayUseLogoEnabled(true)
        supportActionBar?.title = "나의 생각 기록"      // 타이틀 삭제
        supportActionBar?.elevation = 0.0F  // 상자 그림자 삭제
        supportActionBar?.setLogo(R.drawable.baseline_arrow_back_ios_new_24)
        setContentView(R.layout.activity_my_messages)
        super.onCreate(savedInstanceState)

// 데이터 불러오기
        userDataStore = UserDataStore(this)
        lifecycleScope.launch {
            // 이모지
            val myEmojiView = findViewById<ImageView>(R.id.main_guide_emoji)
            Log.d(TAG, "onCreate: $myEmojiView")
            Log.d(TAG, "onCreate: $myEmojiView")
            setEmoji(userDataStore.get_emoji.first(), myEmojiView)
            // 히스토리
            val token = userDataStore.get_access_token.first()
            getHistory(token)
        }
// 메세지 전송
        val messageButton = findViewById<Button>(R.id.main_think_cloud_button)
        val newMessage = findViewById<EditText>(R.id.main_guide_text)
        messageButton.setOnClickListener {
            val content = newMessage.text.toString()
            if (content == " ") {
                Toast.makeText(this, "당신의 스쳐가는 생각을 남겨주세요", Toast.LENGTH_SHORT).show()
            }else{
                lifecycleScope.launch {
                    setMessage(userDataStore.get_access_token.first(), content)
                    newMessage.text.clear()
                }
            }
        }
        newMessage.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val content = newMessage.text.toString()
                if (content == " ") {
                    Toast.makeText(this, "당신의 스쳐가는 생각을 남겨주세요", Toast.LENGTH_SHORT).show()
                }else{
                    lifecycleScope.launch {
                        setMessage(userDataStore.get_access_token.first(), content)
                        newMessage.text.clear()
                    }
                }
                true
            } else {
                false
            }
        }
    }
    private fun setMessage(Header: String, myMessage: String){
        val params = HashMap<String, String>()
        params.put("message", myMessage)
        api.setMessage(Header, params).enqueue(object : Callback<MessageResponse> {
            override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                if (response.code() == 200) {
                    lifecycleScope.launch {
                        getHistory(userDataStore.get_access_token.first())
                    }
                } else if (response.code() == 401){
                    Log.i(TAG, "메세지 전송 401: 토큰 만료")
                    getToken(myMessage)
                } else {
                    Log.i(TAG, "메세지 전송 기타: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                Log.d("메세지 전송 실패: ", t.message.toString())
            }
        })
    }
    private fun setEmoji(myEmoji:String, myEmojiView:ImageView){
        Glide.with(this).load("https://peoplemoji.s3.ap-northeast-2.amazonaws.com/emoji/animate/${myEmoji}.gif").into(myEmojiView)
    }
    fun getHistory(header : String) {
        val currentDate = LocalDate.now(ZoneId.of("Asia/Seoul"))
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDate = currentDate.format(formatter)
        api.getHistory(header, formattedDate).enqueue(object : Callback<HistoryResponse> {
            override fun onResponse(call: Call<HistoryResponse>, response: Response<HistoryResponse>) {
                if (response.code() == 200) {
                    val result = response.body()?.data!!
                    Log.d(TAG, "getHistory onResponse: ${response.body()}")
                    Log.d(TAG, "getHistory onResponse: $result")
                    val history = findViewById<RecyclerView>(R.id.history_recyclerView)
                    val historyAdapter = MyMessagesAdapter(this@MyMessagesActivity, result)

                    history.adapter = historyAdapter
                    history.layoutManager = LinearLayoutManager(this@MyMessagesActivity, LinearLayoutManager.VERTICAL, false)
                    historyAdapter.setOnHistoryClickListener(object : MyMessagesAdapter.OnHistoryClickListener{
                        override fun onHistoryClick(view: View, position: Int, messageIdx: Int) {
                            Log.d(TAG, "onRightClick: 액티비티 눌림")
                            lifecycleScope.launch {
                                val token = userDataStore.get_access_token.first()
                                deleteMessage(token, messageIdx)
                            }
                        }
                    })
                } else if (response.code() == 401){
                    Log.i(TAG, "401: 토큰 만료")
                    getToken(" ")
                } else {
                    Log.i(TAG, "기타: $response")
                }
            }
            override fun onFailure(call: Call<HistoryResponse>, t: Throwable) {
                Log.d("실패: ", t.message.toString())
            }
        })
    }
    private fun deleteMessage(header: String, messageIdx : Int){
        var params = HashMap<String, Int>()
        params["messageIdx"] = messageIdx
        api.deleteMessage(header, params).enqueue(object : Callback<MessageResponse> {
            override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                if (response.code() == 200) {
                    Log.i(TAG, "onResponse: 메세지 삭제 성공")
                } else if (response.code() == 401){
                    Log.i(TAG, "메세지 전송 401: 토큰 만료")
                    deleteToken(messageIdx)
                } else {
                    Log.i(TAG, "메세지 전송 기타: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                Log.d("메세지 전송 실패: ", t.message.toString())
            }
        })
    }
    fun getToken(myMessage: String) {
        lifecycleScope.launch {
            val refreshToken = userDataStore.get_refresh_token.first()
            api.setAccessToken(refreshToken).enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    response.body()?.let {
                        if (it.status == 200) {
                            Log.i(TAG, "토큰 전송 200: 유저 정보 저장")
                                response.body()?.data?.let {
                                    it1 -> saveUserInfo(it1, myMessage)
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
    fun deleteToken(messageIdx: Int) {
        lifecycleScope.launch {
            val refreshToken = userDataStore.get_refresh_token.first()
            api.setAccessToken(refreshToken).enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    response.body()?.let {
                        if (it.status == 200) {
                            Log.i(TAG, "토큰 전송 200: 유저 정보 저장")
                            response.body()?.data?.let {
                                    it1 -> deleteUserInfo(it1, messageIdx)
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
    fun saveUserInfo(payload : UserInfo, myMessage: String){
        lifecycleScope.launch {
            userDataStore.setUserData(payload)
            val token = userDataStore.get_access_token.first()
            if (myMessage == " "){
                getHistory(token)
            } else {
                setMessage(token, myMessage)
            }
        }
    }
    fun deleteUserInfo(payload : UserInfo, messageIdx: Int){
        lifecycleScope.launch {
            userDataStore.setUserData(payload)
            val token = userDataStore.get_access_token.first()
            deleteMessage(token, messageIdx)
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        lifecycleScope.launch {
            val token = userDataStore.get_access_token.first()
            getNotiCnt(token, menu)
        }
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
//            R.id.action_notifications -> {
//                Toast.makeText(applicationContext, "알림 준비 중..", Toast.LENGTH_SHORT).show()
//                true
//            }
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            android.R.id.home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun getNotiCnt(header : String, menu:Menu){
        api.getNotiCnt(header).enqueue(object : Callback<NotiCntResponse> {
            override fun onResponse(call: Call<NotiCntResponse>, response: Response<NotiCntResponse>) {
                Log.d("getNoti 응답", response.toString())
                if (response.code() == 200) {
                    val notiCnt = response.body()!!.data.notificationCnt
                    if (notiCnt > 0){
                        val menuItem = menu.findItem(R.id.action_notifications)
                        menuItem.setIcon(R.drawable.icon_notification_true)
                    }
                } else if (response.code() == 401){
                    Log.i(ContentValues.TAG, "getRecentFeed_onResponse 401: 토큰 만료")
                    lifecycleScope.launch {
                        val refreshToken = userDataStore.get_refresh_token.first()
                        api.setAccessToken(refreshToken).enqueue(object : Callback<UserResponse> {
                            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                                response.body()?.let {
                                    if (it.status == 200) {
                                        Log.i(ContentValues.TAG, "토큰 전송 응답 바디 ${response.body()?.data?.accessToken}")
                                        response.body()?.data?.let {
                                                it1 -> {
                                            lifecycleScope.launch {
                                                userDataStore.setUserData(it1)
                                                val token =
                                                    userDataStore.get_access_token.first()
                                                getNotiCnt(token, menu)
                                            }
                                        }
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
                } else {
                    Log.i(ContentValues.TAG, "getRecentFeed_onResponse 코드: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<NotiCntResponse>, t: Throwable) {
                Log.d("gerRecentFeed_onFailure", t.message.toString())
            }
        })
    }
}