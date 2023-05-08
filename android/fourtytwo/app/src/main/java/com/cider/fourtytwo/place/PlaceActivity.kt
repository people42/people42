package com.cider.fourtytwo.place

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cider.fourtytwo.MainActivity
import com.cider.fourtytwo.person.PersonActivity
import com.cider.fourtytwo.R
import com.cider.fourtytwo.SettingsActivity
import com.cider.fourtytwo.dataStore.UserDataStore
import com.cider.fourtytwo.network.Api
import com.cider.fourtytwo.network.Model.MessageResponse
import com.cider.fourtytwo.network.RetrofitInstance
import com.cider.fourtytwo.signIn.UserInfo
import com.cider.fourtytwo.signIn.UserResponse
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class PlaceActivity : AppCompatActivity() {
    val api: Api = RetrofitInstance.getInstance().create(Api::class.java)
    //유저
    private lateinit var userDataStore: UserDataStore
    //피드
    private lateinit var placeAdapter: PlaceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        // 화면이 구성될 때, 스플래시 테마에서 메인 테마로 변경
        setTheme(R.style.Theme_Fourtytwo)

        // 넘겨받은 데이터
        val placeIdx : Int = intent.getIntExtra("placeIdx", 17)
        val time : String? = intent.getStringExtra("time")
        val placeName : String? = intent.getStringExtra("placeName")

        // 로고 장착
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayUseLogoEnabled(true)
        supportActionBar?.title = placeName      // 타이틀 삭제
        supportActionBar?.elevation = 0.0F  // 상자 그림자 삭제
        supportActionBar?.setLogo(R.drawable.baseline_arrow_back_ios_new_24)
//        setContentView(R.layout.activity_my_messages)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place)

        userDataStore = UserDataStore(this)
        lifecycleScope.launch {
            val token = userDataStore.get_access_token.first()
            if (time != null) {
                getPlaceFeed(token, placeIdx, time)
            }
        }
    }
    private fun setReport(header:String, messageIdx:Int, id:Int){
        val params = HashMap<String, Any>()
        params["messageIdx"] = messageIdx
        params["content"] = "신고합니다"
        api.setReport(header, params).enqueue(object : Callback<MessageResponse> {
            override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                response.body()?.let {
                    if (it.status == 200) {
                        Log.i(ContentValues.TAG, "신고 성공 ${response.body()?.data}")
                        Toast.makeText(applicationContext, "신고 완료", Toast.LENGTH_SHORT).show()
                    } else {
                        getToken2(messageIdx, -1, id)
                        Log.i(ContentValues.TAG, "신고 실패: ${response.code()}")
                    }
                }
            }
            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                Log.d("신고 on failure: ", t.message.toString())
            }
        })
    }
    private fun setBlock(header:String, userIdx:Int, id:Int){
        val params = HashMap<String, Int>()
        params["messageIdx"] = userIdx
        api.setBlock(header, params).enqueue(object : Callback<MessageResponse> {
            override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                response.body()?.let {
                    if (it.status == 200) {
                        Log.i(ContentValues.TAG, "차단 성공 ${response.body()?.data}")
                        Toast.makeText(applicationContext, "차단 완료", Toast.LENGTH_SHORT).show()
                    } else {
                        getToken2(-1, userIdx, id)
                        Log.i(ContentValues.TAG, "차단 실패: ${response.code()}")
                    }
                }
            }
            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                Log.d("차단 on failure: ", t.message.toString())
            }
        })
    }
    private fun getPlaceFeed(header : String, placeIdx : Int, time : String) : ArrayList<MessagesInfo> {
        var feedList : ArrayList<MessagesInfo> = ArrayList()
        api.getPlaceFeed(header, placeIdx, time, 0, 10).enqueue(object : Callback<PlaceResponse> {
            override fun onResponse(call: Call<PlaceResponse>, response: Response<PlaceResponse>) {
                Log.d("getRecentFeed 응답", response.toString())
                Log.d("getRecentFeed 응답", response.body().toString())
                if (response.code() == 200) {
                    // 피드 정보 보내기
                    val result = response.body()!!.data
                    feedList = result.messagesInfo
                    val feed = findViewById<RecyclerView>(R.id.place_feed)
                    placeAdapter = PlaceAdapter(this@PlaceActivity, feedList)
                    feed.adapter = placeAdapter
                    feed.layoutManager = LinearLayoutManager(this@PlaceActivity, LinearLayoutManager.VERTICAL, false)
                    placeAdapter.setOnPlaceClickListener(object  : PlaceAdapter.OnPlaceClickListener{
                        override fun onPlaceClick(
                            view: View,
                            position: Int,
                            userIdx: Int,
                            nickname:String
                        ) {
                            val intent = Intent(this@PlaceActivity, PersonActivity::class.java)
                            intent.putExtra("userIdx", userIdx)
                            intent.putExtra("nickname", nickname)
                            startActivity(intent)
                        }

                        override fun onPlaceLongClick(
                            view: View,
                            position: Int,
                            id: Int,
                            messageIdx: Int,
                            userIdx: Int
                        ) {
                            lifecycleScope.launch {
                                val token = userDataStore.get_access_token.first()
                                if(id == -2){
                                    // 신고
                                    setReport(token, messageIdx, id)
                                }else{
                                    //차단
                                    setBlock(token, userIdx, id)
                                }
                            }
                        }
                    })
                    // 지도 위치 이동
                    val mapFragment = supportFragmentManager.findFragmentById(R.id.place_map) as SupportMapFragment
                    mapFragment.getMapAsync { googleMap ->
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(result.placeWithTimeAndGpsInfo.placeLatitude, result.placeWithTimeAndGpsInfo.placeLongitude), 15f))
                    }
                    // 마커 추가
                    val frameLayout = findViewById<FrameLayout>(R.id.place_frame)
                    for(i: Int in 1..feedList.size){
                        val imageView = ImageView(this@PlaceActivity)
                        Glide.with(this@PlaceActivity).load("https://peoplemoji.s3.ap-northeast-2.amazonaws.com/emoji/animate/${result.messagesInfo[i-1].emoji}.gif").into(imageView)
                        val layoutParams = FrameLayout.LayoutParams(100, 100)
                        layoutParams.marginStart = rand(300, 627)
                        layoutParams.topMargin = rand(300, 600)
                        frameLayout.addView(imageView, layoutParams)
                    }
                    Log.i(ContentValues.TAG, "getRecentFeed_onResponse feedList: $feedList")
                } else if (response.code() == 401){
                    Log.i(ContentValues.TAG, "getRecentFeed_onResponse 401: 토큰 만료")
                    getToken(placeIdx, time)
                } else {
                    Log.i(ContentValues.TAG, "getRecentFeed_onResponse 코드: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<PlaceResponse>, t: Throwable) {
                Log.d("gerRecentFeed_onFailure", t.message.toString())
            }
        })
        return feedList
    }
    val random = Random()
    fun rand(from: Int, to: Int) : Int {
        return random.nextInt(to - from) + from
    }
    fun getToken(placeIdx : Int, time : String) {
        lifecycleScope.launch {
            val refreshToken = userDataStore.get_refresh_token.first()
            api.setAccessToken(refreshToken).enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    response.body()?.let {
                        if (it.status == 200) {
                            Log.i(ContentValues.TAG, "토큰 전송 200: 유저 정보 저장")
                            Log.i(ContentValues.TAG, "토큰 전송 응답 바디 ${response.body()?.data?.accessToken}")
                            response.body()?.data?.let {
                                    it1 -> saveUserInfo(it1, placeIdx,time)
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
    fun saveUserInfo(payload : UserInfo, placeIdx : Int, time : String){
        lifecycleScope.launch {
            userDataStore.setUserData(payload)
            val token = userDataStore.get_access_token.first()
            getPlaceFeed(token, placeIdx,time)
        }
    }
    fun getToken2(messageIdx: Int, userIdx: Int, id:Int) {
        lifecycleScope.launch {
            val refreshToken = userDataStore.get_refresh_token.first()
            api.setAccessToken(refreshToken).enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    response.body()?.let {
                        if (it.status == 200) {
                            Log.i(ContentValues.TAG, "토큰 전송 200: 유저 정보 저장")
                            Log.i(ContentValues.TAG, "토큰 전송 응답 바디 ${response.body()?.data?.accessToken}")
                            response.body()?.data?.let {
                                    it1 -> saveUserInfo2(it1, id, messageIdx, userIdx)
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
    fun saveUserInfo2(payload : UserInfo, id:Int, messageIdx: Int, userIdx: Int){
        lifecycleScope.launch {
            userDataStore.setUserData(payload)
            val token = userDataStore.get_access_token.first()
            if (id == -2) {
                setReport(token, messageIdx, id)
            } else {
                setBlock(token, userIdx, id)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
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
}