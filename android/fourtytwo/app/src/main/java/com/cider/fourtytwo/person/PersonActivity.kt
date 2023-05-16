package com.cider.fourtytwo.person

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.cider.fourtytwo.MainActivity
import com.cider.fourtytwo.NotificationActivity
import com.cider.fourtytwo.R
import com.cider.fourtytwo.SettingsActivity
import com.cider.fourtytwo.dataStore.UserDataStore
import com.cider.fourtytwo.network.Api
import com.cider.fourtytwo.network.Model.MessageResponse
import com.cider.fourtytwo.network.Model.NotiCntResponse
import com.cider.fourtytwo.network.RetrofitInstance
import com.cider.fourtytwo.place.PlaceAdapter
import com.cider.fourtytwo.signIn.UserInfo
import com.cider.fourtytwo.signIn.UserResponse
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PersonActivity : AppCompatActivity() {
    val api: Api = RetrofitInstance.getInstance().create(Api::class.java)
    //유저
    private lateinit var userDataStore: UserDataStore
    //피드
    private lateinit var personAdapter: PersonAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Fourtytwo)
        // 로고 장착
        val userIdx : Int = intent.getIntExtra("userIdx", 5)
        val nickname : String? = intent.getStringExtra("nickname")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayUseLogoEnabled(true)
        supportActionBar?.title = "$nickname 님"      // 타이틀 삭제
        supportActionBar?.elevation = 0.0F  // 상자 그림자 삭제
        supportActionBar?.setLogo(R.drawable.baseline_arrow_back_ios_new_24)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_person)

        userDataStore = UserDataStore(this)
        lifecycleScope.launch {
            val token = userDataStore.get_access_token.first()
            getPersonFeed(token, userIdx)
        }
    }
    private fun getPersonFeed(header:String, userIdx:Int) {
        api.getPersonFeed(header, userIdx).enqueue(object : Callback<PersonResponse> {
            override fun onResponse(call: Call<PersonResponse>, response: Response<PersonResponse>) {
                Log.d("getPersonFeed 응답", response.toString())
                if (response.code() == 200) {
                    val result = response.body()!!.data
                    // 스침 수
                    findViewById<TextView>(R.id.person_brush).text = "${result.brushCnt}번   스침"
                    // 상단 이모지
                    val fab = findViewById<FloatingActionButton>(R.id.fab)
                    val personEmoji = result.emoji
                    Glide.with(this@PersonActivity)
                        .asDrawable()
                        .load("https://peoplemoji.s3.ap-northeast-2.amazonaws.com/emoji/animate/${personEmoji}.gif")
                        .into(object : CustomTarget<Drawable>() {
                            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                                fab.setImageDrawable(resource)
                            }
                            override fun onLoadCleared(placeholder: Drawable?) {}
                        })
                    // 지도에 이모지 마커 추가
                    val mapFragment = supportFragmentManager.findFragmentById(R.id.person_map) as SupportMapFragment
                    val builder = LatLngBounds.builder()
                    mapFragment.getMapAsync { googleMap ->
                        // 이미지 로딩 라이브러리(Glide)를 사용하여 URL에서 이미지를 로드
                        Glide.with(this@PersonActivity)
                            .asBitmap()
                            .load("https://peoplemoji.s3.ap-northeast-2.amazonaws.com/emoji/animate/${personEmoji}.gif")
                            .override(100, 100)
                            .into(object : SimpleTarget<Bitmap>() {
                                override fun onResourceReady(
                                    resource: Bitmap,
                                    transition: Transition<in Bitmap>?
                                ) {
                                    // 로드한 이미지로 마커 아이콘 생성
                                    val markerIcon =
                                        BitmapDescriptorFactory.fromBitmap(resource)
                                    for(i in result.placeResDtos) {
                                        val newLocation = LatLng(i.placeLatitude, i.placeLongitude)
                                        // 마커 추가
                                        val markerOptions = MarkerOptions()
                                            .position(newLocation) // 마커 위치
                                            .icon(markerIcon) // 마커 아이콘
                                            .title("${i.placeIdx}")
                                        val marker = googleMap.addMarker(markerOptions)
                                        if (marker != null) {
                                            builder.include(newLocation)
                                        }
                                } // 마커를 포함하는 위치로 지도 이동
                                val bounds = builder.build()
                                val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 300) // 100: padding 값
                                googleMap.moveCamera(cameraUpdate)
                            }
                        })
                        googleMap.setOnMarkerClickListener { marker ->
                            Log.d(TAG, "Marker clicked: ${marker.title}")
                            lifecycleScope.launch {
                                getPersonPlaceFeed(userDataStore.get_access_token.first(), userIdx, marker.title!!.toInt())
                            }
                            true // 이벤트 처리 완료
                        }

                    }
                } else if (response.code() == 401){
                    Log.i(ContentValues.TAG, "getPersonFeed 401: 토큰 만료")
                    getToken(userIdx, -1)
                } else {
                    Log.i(ContentValues.TAG, "getPersonFeed 코드: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<PersonResponse>, t: Throwable) {
                Log.d("getPersonFeed", t.message.toString())
            }
        })
    }
    private fun getPersonPlaceFeed(header: String, userIdx: Int, placeIdx:Int){
        api.getPersonPlaceFeed(header, userIdx, placeIdx).enqueue(object : Callback<PersonPlaceResponse> {
            override fun onResponse(call: Call<PersonPlaceResponse>, response: Response<PersonPlaceResponse>) {
                response.body()?.let {
                    if (it.status == 200) {
                        Log.i(ContentValues.TAG, "getPersonPlaceFeed 성공 ${response.body()?.data}")
                        val result = response.body()?.data!!.messagesInfo
                        Log.d(TAG, "onResponse: $result")
                        val feed = findViewById<RecyclerView>(R.id.place_person_feed)
                        personAdapter = PersonAdapter(this@PersonActivity, result)
                        feed.adapter = personAdapter
                        feed.layoutManager = LinearLayoutManager(this@PersonActivity, LinearLayoutManager.HORIZONTAL, false)
                        personAdapter.setOnPersonClickListener(object  : PersonAdapter.OnPersonClickListener{
                            override fun onPersonLongClick(
                                view: View,
                                position: Int,
                                id: Int,
                                messageIdx: Int
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
                        feed.visibility= VISIBLE
                    } else {
                        getToken(userIdx, placeIdx)
                        Log.i(ContentValues.TAG, "getPersonPlaceFeed 실패: ${response.code()}")
                    }
                }
            }
            override fun onFailure(call: Call<PersonPlaceResponse>, t: Throwable) {
                Log.d("getPersonPlaceFeed on failure: ", t.message.toString())
            }
        })
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
    fun getToken(userIdx : Int, placeIdx: Int) {
        lifecycleScope.launch {
            val refreshToken = userDataStore.get_refresh_token.first()
            api.setAccessToken(refreshToken).enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    response.body()?.let {
                        if (it.status == 200) {
                            Log.i(ContentValues.TAG, "토큰 전송 200: 유저 정보 저장")
                            Log.i(ContentValues.TAG, "토큰 전송 응답 바디 ${response.body()?.data?.accessToken}")
                            response.body()?.data?.let {
                                    it1 -> saveUserInfo(it1, userIdx, placeIdx)
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
    fun saveUserInfo(payload : UserInfo, userIdx : Int, placeIdx: Int){
        lifecycleScope.launch {
            userDataStore.setUserData(payload)
            val token = userDataStore.get_access_token.first()
            if (placeIdx == -1){
            getPersonFeed(token, userIdx)
            } else {
                getPersonPlaceFeed(token, userIdx, placeIdx)
            }
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
        lifecycleScope.launch {
            val token = userDataStore.get_access_token.first()
            getNotiCnt(token, menu)
        }
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_notifications -> {
                val intent = Intent(this, NotificationActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            android.R.id.home -> {
//                val intent = Intent(this, MainActivity::class.java)
//                startActivity(intent)
                onBackPressed()
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
                        menuItem.setIcon(R.drawable.baseline_notifications_true24)
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