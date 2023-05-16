package com.cider.fourtytwo.setting

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cider.fourtytwo.MainActivity
import com.cider.fourtytwo.NotificationActivity
import com.cider.fourtytwo.R
import com.cider.fourtytwo.SettingsActivity
import com.cider.fourtytwo.dataStore.UserDataStore
import com.cider.fourtytwo.network.Api
import com.cider.fourtytwo.network.Model.MessageResponse
import com.cider.fourtytwo.network.RetrofitInstance
import com.cider.fourtytwo.person.PersonAdapter
import com.cider.fourtytwo.place.PlaceAdapter
import com.cider.fourtytwo.signIn.UserResponse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap

class ChangeEmojiActivity : AppCompatActivity() {
    val api: Api = RetrofitInstance.getInstance().create(Api::class.java)
    private lateinit var userDataStore: UserDataStore
    private lateinit var changeEmojiAdapter: ChangeEmojiAdapter
    lateinit var selectedEmoji : String

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Fourtytwo)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayUseLogoEnabled(true)
        supportActionBar?.title = null     // 타이틀 삭제
        supportActionBar?.elevation = 0.0F  // 상자 그림자 삭제
        supportActionBar?.setLogo(R.drawable.baseline_arrow_back_ios_new_24)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_emoji)

        userDataStore = UserDataStore(this)

        val emojiView = findViewById<ImageView>(R.id.change_emoji_preview)
        lifecycleScope.launch {
            selectedEmoji = userDataStore.get_emoji.first()
            Glide.with(this@ChangeEmojiActivity).load("https://peoplemoji.s3.ap-northeast-2.amazonaws.com/emoji/animate/${selectedEmoji}.gif").into(emojiView)
        }

        val emojiList = findViewById<RecyclerView>(R.id.change_emoji_recycler)
        changeEmojiAdapter = ChangeEmojiAdapter(this)
        emojiList.adapter = changeEmojiAdapter
        emojiList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        changeEmojiAdapter.setOnItemClickListener(object : ChangeEmojiAdapter.OnItemClickListener{
            override fun onItemClicked(v: View, pickedEmoji: String, pos: Int) {
                Glide.with(this@ChangeEmojiActivity).load("https://peoplemoji.s3.ap-northeast-2.amazonaws.com/emoji/animate/${pickedEmoji}.gif").into(emojiView)
                selectedEmoji = pickedEmoji
            }
        })
        val btn = findViewById<Button>(R.id.change_emoji_button)
        btn.setOnClickListener {
            lifecycleScope.launch {
                val token = userDataStore.get_access_token.first()
                setEmoji(token)
            }
        }
    }
    private fun setEmoji(header : String){
        val params = HashMap<String, String>()
        params["emoji"] = selectedEmoji
        api.setEmoji(header, params).enqueue(object : Callback<MessageResponse> {
            override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                Log.d("setEmoji 응답", response.toString())
                if (response.code() == 200) {
                    Log.i(ContentValues.TAG, "setEmoji 응답 바디: ${response.body()}")
                    lifecycleScope.launch {
                        userDataStore.setUserEmoji(selectedEmoji)
                        val intent = Intent(this@ChangeEmojiActivity, MainActivity::class.java)
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
                                                setEmoji(token)
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