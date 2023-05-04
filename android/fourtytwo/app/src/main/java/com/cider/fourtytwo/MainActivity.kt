package com.cider.fourtytwo

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cider.fourtytwo.dataStore.UserDataStore
import com.cider.fourtytwo.feed.FeedAdapter
import com.cider.fourtytwo.network.Api
import com.cider.fourtytwo.network.Model.*
import com.cider.fourtytwo.network.RetrofitInstance
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(){
    private lateinit var userDataStore: UserDataStore
    val api = RetrofitInstance.getInstance().create(Api::class.java)

    private lateinit var feedAdapter: FeedAdapter
    private var feedList: List<RecentFeedData> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        // 화면이 구성될 때, 스플래시 테마에서 메인 테마로 변경
        setTheme(R.style.Theme_Fourtytwo)
        // 로고 장착
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayUseLogoEnabled(true)
        supportActionBar?.setLogo(R.drawable.logo_first)
        supportActionBar?.title = null      // 타이틀 삭제
        supportActionBar?.elevation = 0.0F  // 상자 그림자 삭제

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        userDataStore = UserDataStore(this)

// 내 메세지, 피드 가져오기
        lifecycleScope.launch {
            val token = userDataStore.get_access_token.first()
            feedList = getRecentFeed(token)
            getNowMessage(token)
            var myEmoji : String = userDataStore.get_emoji.first()
            val myEmojiView = findViewById<ImageView>(R.id.my_opinion_emoji)
            setEmoji(myEmoji, myEmojiView)
        }
// 피드
//        피드가 있으면 R.id.cardLine visibility 보이게
        val feed = findViewById<RecyclerView>(R.id.feed)
        feedAdapter = FeedAdapter(this, feedList)
//        feedAdapter.notifyDataSetChanged()
        feed.adapter = feedAdapter
        feed.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // 지도
        val mapFragment = supportFragmentManager.findFragmentById(
            R.id.map_fragment
        ) as? SupportMapFragment

        mapFragment?.getMapAsync { googleMap ->
            googleMap.setOnMapLoadedCallback {
//                val bounds = LatLngBounds.builder()

                val marker = LatLng(37.568291,126.997780)
                googleMap.addMarker(
                    MarkerOptions()
                        .position(marker)
                        .title("여기")
                        .draggable(false)
                        .alpha(0.9f)
//                        .icon(BitmapDescriptorFactory.defaultMarker(R.drawable.robot))
                )
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(marker))
//                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 20))
            }
//            addMarkers(googleMap)
        }
    }
    fun setEmoji(myEmoji:String, myEmojiView:ImageView){
        Glide.with(this).load("https://peoplemoji.s3.ap-northeast-2.amazonaws.com/emoji/animate/${myEmoji}.gif").into(myEmojiView)
    }
    fun getNowMessage(header : String) {
        var myEmoji : String? = null
        api.getNowMessage(header).enqueue(object : Callback<NowMessageData> {
            override fun onResponse(call: Call<NowMessageData>, response: Response<NowMessageData>) {
                Log.d("NowMessage 응답", response.toString())
                if (response.code() == 200) {
                    Log.i(TAG, "NowMessage 응답 바디: ${response.body()}")
                    // 메세지가 있으면
                    if(response.body()?.messageCnt!! > 0) {
                        //그림자 생기기
                        if (response.body()?.messageCnt!! > 1) {
                            findViewById<ImageView>(R.id.my_opinion_text_shadow1).visibility =
                                VISIBLE
                            findViewById<ImageView>(R.id.my_opinion_text_shadow2).visibility =
                                VISIBLE
                        }
                        // 메세지, 이모지
                        findViewById<TextView>(R.id.my_opinion_text).text = response.body()?.message
                        Log.d(TAG, "NowMessage 이모지 전 : ${response.body()!!.emoji}")
                        myEmoji = response.body()!!.emoji
                        Log.d(TAG, "NowMessage 이모지 후 : ${myEmoji}")
                    }
                    // 공감 크기 정하기

                } else if (response.code() == 401){
                    Log.i(TAG, "NowMessage_onResponse 401: 토큰 만료")
                    getToken()
                } else {
                    Log.i(TAG, "NowMessage_onResponse 기타 코드: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<NowMessageData>, t: Throwable) {
                Log.d("NowMessage_onFailure", t.message.toString())
            }
        })
    }
    fun getRecentFeed(header : String) : List<RecentFeedData> {
        var feedList : List<RecentFeedData> = ArrayList()
        api.getRecentFeed(header).enqueue(object : Callback<RecentFeedResponse> {
            override fun onResponse(call: Call<RecentFeedResponse>, response: Response<RecentFeedResponse>) {
                Log.d("getRecentFeed 응답", response.toString())
                if (response.code() == 200) {
                    Log.i(ContentValues.TAG, "gerRecentFeed_onResponse 응답 바디: ${response.body()!!.data}")
                    feedList = response.body()!!.data
                    Log.i(ContentValues.TAG, "gerRecentFeed_onResponse feedList: $feedList")
                } else if (response.code() == 401){
                    Log.i(TAG, "gerRecentFeed_onResponse 401: 토큰 만료")
                    getToken()
                } else {
                    Log.i(TAG, "gerRecentFeed_onResponse 코드: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<RecentFeedResponse>, t: Throwable) {
                Log.d("gerRecentFeed_onFailure", t.message.toString())
            }
        })
        if (feedList.size < 1) {
//            val cardView = findViewById<CardView>(R.id.bottom_sheet)
//            cardView.layoutParams.height = 10
//            cardView.isEnabled = false

            val nofeedText = findViewById<TextView>(R.id.noFeed)
            nofeedText.visibility = VISIBLE
        }
        return feedList
    }
    fun getToken() {
        lifecycleScope.launch {
            val refreshToken = userDataStore.get_refresh_token.first()
            api.setAccessToken(refreshToken).enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    Log.d("토큰 전송 on response", response.toString())
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
                    // 실패
                    Log.d("토큰 전송 on failure: ", t.message.toString())
                }
            })

        }
    }
    fun saveUserInfo(payload : UserInfo){
        lifecycleScope.launch {
            userDataStore.setUserData(payload)
            val token = userDataStore.get_access_token.first()
            getRecentFeed(token)
            getNowMessage(token)
        }
        Log.d(TAG, "유저 정보 저장 완료: 444444444444444444444444444444444444")
    }
        //    override fun onMapReady(googleMap: GoogleMap) {
//        mMap = googleMap
//        val marker = LatLng(37.568291,126.997780)
//        mMap.addMarker(MarkerOptions().position(marker).title("여기"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker))
//        mMap.moveCamera(CameraUpdateFactory.zoomTo(15f))
//    }
//    override fun onStart() {
//        super.onStart()
//        mView.onStart()
//    }
//
//    override fun onStop() {
//        super.onStop()
//        mView.onStop()
//    }
//
//    override fun onResume() {
//        super.onResume()
//        mView.onResume()
//    }
//
//    override fun onPause() {
//        super.onPause()
//        mView.onPause()
//    }
//
//    override fun onLowMemory() {
//        super.onLowMemory()
//        mView.onLowMemory()
//    }
//
//    override fun onDestroy() {
//        mView.onDestroy()
//        super.onDestroy()
//    }
//    override fun onMapReady(googleMap: GoogleMap) {
//        mMap = googleMap
//        val seoul = LatLng(37.56, 126.97)
//        val markerOptions = MarkerOptions()
//        markerOptions.position(seoul)
//        markerOptions.title("서울")
//        markerOptions.snippet("한국의 수도")
//        mMap.addMarker(markerOptions)
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 10f))
//    }
//    private fun addMarkers(googleMap: GoogleMap) {
//        places.forEach { place ->
//            val marker = googleMap.addMarker(
//                MarkerOptions()
//                    .title(place.nickname)
//                    .position(place.latLng)
//            )
//        }
//    }
// 옵션 메뉴
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_notifications -> {
                Toast.makeText(applicationContext, "알림 준비 중..", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
//    var backPressedTime : Long = 0
//    override fun onBackPressed() {
//        //2.5초이내에 한 번 더 뒤로가기 클릭 시
//        if (System.currentTimeMillis() - backPressedTime < 1500) {
//            super.getOnBackPressedDispatcher()
//            return
//        }
//        Toast.makeText(this, "한번 더 클릭 시 홈으로 이동됩니다.", Toast.LENGTH_SHORT).show()
//        backPressedTime = System.currentTimeMillis()
//    }
}