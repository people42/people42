package com.cider.fourtytwo.setting

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View.GONE
import android.widget.*
import androidx.core.view.marginTop
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.cider.fourtytwo.MainActivity
import com.cider.fourtytwo.R
import com.cider.fourtytwo.dataStore.UserDataStore
import com.cider.fourtytwo.network.Api
import com.cider.fourtytwo.network.Model.MessageResponse
import com.cider.fourtytwo.network.RetrofitInstance
import com.cider.fourtytwo.signIn.UserResponse
import com.cider.fourtytwo.signup.NicknameResponse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap

class ChangeNicknameActivity : AppCompatActivity() {
    val api = RetrofitInstance.getInstance().create(Api::class.java)
    private lateinit var userDataStore: UserDataStore
    lateinit var pickedNickname: String

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Fourtytwo)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayUseLogoEnabled(true)
        supportActionBar?.title = null
        supportActionBar?.elevation = 0.0F  // 상자 그림자 삭제
        supportActionBar?.setLogo(R.drawable.baseline_arrow_back_ios_new_24) // 뒤로가기이미지
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_nickname)

        userDataStore = UserDataStore(this)
        findViewById<LinearLayout>(R.id.nickname_tab).visibility = GONE

        findViewById<TextSwitcher>(R.id.nickname_adj).setFactory{
            val txt = TextView(this)
            txt.gravity = Gravity.CENTER
            txt.textSize = 25f
            txt.marginTop
            return@setFactory txt
        }
        findViewById<TextSwitcher>(R.id.nickname_noun).setFactory{
            val txt = TextView(this)
            txt.gravity = Gravity.CENTER
            txt.textSize = 25f
            return@setFactory txt
        }
        getNickname()

        // 글자 이미지 터치 시 요청
        findViewById<LinearLayout>(R.id.pick_nickname).setOnClickListener{
            getNickname()
        }
        // 다시 뽑기 버튼 터치 시 재요청
        findViewById<ImageView>(R.id.resetImage).setOnClickListener{
            getNickname()
        }

        val btn = findViewById<Button>(R.id.nickname_button)
        btn.setOnClickListener {
            lifecycleScope.launch {
                val token = userDataStore.get_access_token.first()
                setNickname(token)
            }
        }
    }
    fun getNickname() {
        var nicknameDivide : List<String>
        api.getNickname().enqueue(object : Callback<NicknameResponse> {
            override fun onResponse(call: Call<NicknameResponse>, response: Response<NicknameResponse>) {
                response.body()?.data?.let {
                    pickedNickname =  it.nickname
                    nicknameDivide = pickedNickname!!.split(" ")
                    findViewById<TextSwitcher>(R.id.nickname_adj).setText(nicknameDivide[0])
                    findViewById<TextSwitcher>(R.id.nickname_noun).setText(nicknameDivide[1])
                }
            }
            override fun onFailure(call: Call<NicknameResponse>, t: Throwable) {
                // 실패
                Log.d("log",t.message.toString())
                Log.d("log","fail")
            }
        })
    }
    private fun setNickname(header : String){
        val params = HashMap<String, String>()
        params["nickname"] = pickedNickname
        api.setNickname(header, params).enqueue(object : Callback<MessageResponse> {
            override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                Log.d("setNickname 응답", response.toString())
                if (response.code() == 200) {
                    Log.i(ContentValues.TAG, "setNickname 응답 바디: ${response.body()}")
                    lifecycleScope.launch {
                        userDataStore.setUserNickname(pickedNickname)
                        val intent = Intent(this@ChangeNicknameActivity, MainActivity::class.java)
                        startActivity(intent)
                    }
                } else if (response.code() == 401){
                    Log.i(ContentValues.TAG, "setEmoji 401: 토큰 만료")
                    lifecycleScope.launch {
                        val refreshToken = userDataStore.get_refresh_token.first()
                        Log.i(ContentValues.TAG, "getToken: ${refreshToken}")
                        api.setAccessToken(refreshToken).enqueue(object : Callback<UserResponse> {
                            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                                Log.d("토큰 전송 on response", response.body().toString())
                                response.body()?.let {
                                    if (it.status == 200) {
                                        Log.i(ContentValues.TAG, "토큰 전송 응답 바디 ${response.body()?.data?.accessToken}")
                                        response.body()?.data?.let {
                                                it1 -> {
                                            lifecycleScope.launch {
                                                userDataStore.setUserData(it1)
                                                val token = userDataStore.get_access_token.first()
                                                setNickname(token)
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
                    Log.i(ContentValues.TAG, "setEmoji 코드: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                Log.d("setEmoji 실패", t.message.toString())
            }
        })
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}