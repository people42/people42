package com.cider.fourtytwo

import android.Manifest
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cider.fourtytwo.dataStore.UserDataStore
import com.cider.fourtytwo.feed.FeedAdapter
import com.cider.fourtytwo.feed.RecentFeedData
import com.cider.fourtytwo.feed.RecentFeedResponse
import com.cider.fourtytwo.map.SetLocationResponse
import com.cider.fourtytwo.network.Api
import com.cider.fourtytwo.network.Model.*
import com.cider.fourtytwo.network.RetrofitInstance
import com.cider.fourtytwo.signIn.UserInfo
import com.cider.fourtytwo.signIn.UserResponse
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(){
    val api: Api = RetrofitInstance.getInstance().create(Api::class.java)
    //유저
    private lateinit var userDataStore: UserDataStore
    private lateinit var myEmoji: String
    //피드
    private lateinit var feedAdapter: FeedAdapter
    //지도
    private lateinit var locationManager: LocationManager
    private lateinit var myLocationListener: MainActivity.MyLocationListener
    companion object {
        val locationPermissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val responsePermissions = permissions.entries.filter {
                it.key in locationPermissions
            }
            if (responsePermissions.filter { it.value == true }.size == locationPermissions.size) {
                setLocationListener()
            } else {
                Toast.makeText(this, "위치 정보를 알 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        // 화면이 구성될 때, 메인 테마로 변경
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

// 데이터 가져오기
        val myHistory = findViewById<ImageView>(R.id.my_opinion_emoji)
        val messageEmojiView = findViewById<ImageView>(R.id.main_guide_emoji)
        lifecycleScope.launch {
            val token = userDataStore.get_access_token.first()
            // 24시간 피드 가져오기
            getRecentFeed(token)
            // 내 현재 메세지 가져오기
            getNowMessage(token)
            // 내 이모지 가져오기
            myEmoji = userDataStore.get_emoji.first()
            setEmoji(myEmoji, myHistory)
            setEmoji(myEmoji, messageEmojiView)
        }

// 메세지 보내기
        val newMessageButton = findViewById<Button>(R.id.main_think_cloud_button)
        val myOpinion = findViewById<TextView>(R.id.my_opinion_text)
        val newMessage = findViewById<EditText>(R.id.main_guide_text)
        val newText = newMessage.text
        newMessageButton.setOnClickListener {
            val content = newText.toString()
            myOpinion.text = content
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
                // 실행할 메소드 호출
                val content = newText.toString()
                myOpinion.text = content
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
// 내 메세지 누르면 메세지 작성 폼 뜨기
        myOpinion.setOnClickListener {
            findViewById<View>(R.id.layout_opinion).visibility = GONE
            findViewById<View>(R.id.layout_edit_opinion).visibility = VISIBLE
        }
// 메세지 작성 폼 누르면 현재 메세지 뜨기
        val messageBackground = findViewById<ImageView>(R.id.main_guide_backgroundTint)
        messageBackground.setOnClickListener {
            findViewById<View>(R.id.layout_opinion).visibility = VISIBLE
            findViewById<View>(R.id.layout_edit_opinion).visibility = GONE
        }
// 내 이모지 누르면 히스토리 이동
        myHistory.setOnClickListener {
            val intent = Intent(this, MyMessagesActivity::class.java)
            startActivity(intent)
        }
        messageEmojiView.setOnClickListener {
            val intent = Intent(this, MyMessagesActivity::class.java)
            startActivity(intent)
        }

// 피드
//        피드가 있으면 R.id.cardLine visibility 보이게
//        val feed = findViewById<RecyclerView>(R.id.feed)
//        feedAdapter = FeedAdapter(this, feedList)
//        //feedAdapter.notifyDataSetChanged()
//        feed.adapter = feedAdapter
//        feed.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

// 지도
        val mapFragment = supportFragmentManager.findFragmentById(
            R.id.map_fragment
        ) as? SupportMapFragment
        mapFragment?.getMapAsync { googleMap ->
            googleMap.uiSettings.isScrollGesturesEnabled = false // 지도 드래그 막기
            googleMap.uiSettings.isCompassEnabled = true // 나침반
//            googleMap.setOnMapLoadedCallback {
//            // 마커 이미지
//                val bitmap = BitmapFactory.decodeResource(resources, R.drawable.marker)
//                val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap)
//                //val bounds = LatLngBounds.builder()
//            // 마커 위치
//                val marker = LatLng(36.354946759143,127.29980994578)
//                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 19f))
//                //googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 20))
            // 레이더 애니메이션
                val scaleAnimation = ScaleAnimation(
                    0f, // 시작 X 스케일
                    1f, // 끝 X 스케일
                    0f, // 시작 Y 스케일
                    1f, // 끝 Y 스케일
                    Animation.RELATIVE_TO_SELF, // X 스케일 기준
                    0.5f, // X 스케일 기준 위치 (0 ~ 1)
                    Animation.RELATIVE_TO_SELF, // Y 스케일 기준
                    0.5f // Y 스케일 기준 위치 (0 ~ 1)
                ).apply {
                    duration = 4000 // 애니메이션 시간 (ms)
                    repeatMode = Animation.RESTART // 애니메이션 반복 모드
                    repeatCount = Animation.INFINITE // 애니메이션 반복 횟수
                }
                val blueRing = findViewById<ImageView>(R.id.blue_ring)
                blueRing.startAnimation(scaleAnimation)
//            }
//            //addMarkers(googleMap)
        }
        getMylocation()
    }
    private fun getMylocation() {
        if (::locationManager.isInitialized.not()) {
            locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
        val isGpsEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (isGpsEnable) {
            permissionLauncher.launch(MainActivity.locationPermissions)
        }
    }
    @Suppress("MissingPermission")
    private fun setLocationListener() {
        val minTime: Long = 1500
        val minDistance = 100f

        if (::myLocationListener.isInitialized.not()) {
            myLocationListener = MyLocationListener()
        }
        with(locationManager) {
            requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                minTime, minDistance, myLocationListener
            )
            requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                minTime, minDistance, myLocationListener
            )
        }
    }
    inner class MyLocationListener : LocationListener {
        override fun onLocationChanged(location: Location) {
            Toast
                .makeText(this@MainActivity, "${location.latitude}, ${location.longitude}", Toast.LENGTH_SHORT)
                .show()
            lifecycleScope.launch {
                var params = HashMap<String, Double>()
                params.put("latitude", location.latitude)
                params.put("longitude", location.longitude)
                setLocation(userDataStore.get_access_token.first(), params)
            }
            removeLocationListener()
        }
        private fun removeLocationListener() {
            if (::locationManager.isInitialized && ::myLocationListener.isInitialized) {
                locationManager.removeUpdates(myLocationListener)
            }
        }
    }
    fun setLocation(header : String, params : HashMap<String, Double>?){
        api.setLocation(header, params).enqueue(object : Callback<SetLocationResponse> {
            override fun onResponse(call: Call<SetLocationResponse>, response: Response<SetLocationResponse>) {
                Log.d("위치 전송 응답", response.toString())
                if (response.code() == 200) {
                    Log.i(TAG, "위치 전송 응답 잘 보내졌다네")
                    // 지도의 내 위치 변경
                    val mapFragment = supportFragmentManager.findFragmentById(
                        R.id.map_fragment
                    ) as? SupportMapFragment
                    mapFragment?.getMapAsync { googleMap ->
                        val myLocation = LatLng(params?.get("latitude")!!, params?.get("longitude")!!)
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 19f))
                    }
                } else if (response.code() == 401){
                    Log.i(TAG, "위치 전송 응답 토큰 만료")
                    getToken("location", params)
                } else {
                    Log.i(TAG, "위치 전송 응답 기타: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<SetLocationResponse>, t: Throwable) {
                // 실패
                Log.d("메세지 전송 2 실패: ", t.message.toString())
            }
        })
    }
    private fun setMessage(Header: String, myMessage: String){
        val params = HashMap<String, String>()
        params.put("message", myMessage)
        Log.i(TAG, "setMessage: $params")
        api.setMessage(Header, params).enqueue(object : Callback<MessageResponse> {
            override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                if (response.code() == 200) {
                    Log.i(TAG, "메세지 전송 200: 잘 보내졌다네")
                    findViewById<View>(R.id.layout_opinion).visibility = VISIBLE
                    findViewById<View>(R.id.layout_edit_opinion).visibility = GONE
                } else if (response.code() == 401){
                    Log.i(TAG, "메세지 전송 401: 토큰 만료")
                    getToken(myMessage, null)
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
        this.myEmoji = myEmoji
        Glide.with(this).load("https://peoplemoji.s3.ap-northeast-2.amazonaws.com/emoji/animate/${myEmoji}.gif").into(myEmojiView)
    }
    private fun getNowMessage(header : String) {
        api.getNowMessage(header).enqueue(object : Callback<NowMessageResponse> {
            override fun onResponse(call: Call<NowMessageResponse>, response: Response<NowMessageResponse>) {
                Log.d(TAG, "onResponse: getNowMessage $response")
                if (response.code() == 200) {
                    // 메세지가 있으면
                    if(response.body()?.data?.messageCnt!! > 0) {
                        findViewById<TextView>(R.id.my_opinion_text).text = response.body()?.data!!.message
                        // 공감 크기 정하기
                        val firstView = findViewById<ImageView>(R.id.reation_first)
                        val secondView = findViewById<ImageView>(R.id.reation_second)
                        val thirdView = findViewById<ImageView>(R.id.reation_third)
                        val fourthView = findViewById<ImageView>(R.id.reation_fourth)

                        if (response.body()!!.data.heart > 0
                            || response.body()!!.data.tear > 0
                            || response.body()!!.data.thumbsUp > 0
                            || response.body()!!.data.fire > 0){
                            val layoutParams = RelativeLayout.LayoutParams(30,30)
                            firstView.layoutParams = layoutParams
                            secondView.layoutParams = layoutParams
                            thirdView.layoutParams = layoutParams
                            fourthView.layoutParams = layoutParams
                        }
                        if (response.body()!!.data.heart > 0) {
                            val first = response.body()!!.data.heart
                            val layoutParams1 : RelativeLayout.LayoutParams
                            if (first > 6) {
                                layoutParams1 = RelativeLayout.LayoutParams(60,60)
                            } else {
                                layoutParams1 = RelativeLayout.LayoutParams(5*(first) + 30,5*(first) + 30)
                            }
                            firstView.layoutParams = layoutParams1
                        }
                        if (response.body()!!.data.tear > 0) {
                            val second = response.body()!!.data.tear
                            var layoutParams2 = RelativeLayout.LayoutParams(second * 10,second * 10)
                            if (second > 6) {
                                layoutParams2 = RelativeLayout.LayoutParams(60,60)
                            } else {
                                layoutParams2 = RelativeLayout.LayoutParams(5*(second) + 30,5*(second) + 30)
                            }
                            secondView.layoutParams = layoutParams2
                        }
                        if (response.body()!!.data.thumbsUp > 0) {
                            val third = response.body()!!.data.thumbsUp
                            var layoutParams3 = RelativeLayout.LayoutParams(third * 10,third * 10)
                            if (third > 6) {
                                layoutParams3 = RelativeLayout.LayoutParams(60,60)
                            } else {
                                layoutParams3 = RelativeLayout.LayoutParams(5*(third) + 30,5*(third) + 30)
                            }
                            thirdView.layoutParams = layoutParams3
                        }
                        if (response.body()!!.data.fire > 0) {
                            val fourth = response.body()!!.data.fire
                            var layoutParams4 = RelativeLayout.LayoutParams(fourth * 10,fourth * 10)
                            if (fourth > 6) {
                                layoutParams4 = RelativeLayout.LayoutParams(60,60)
                            } else {
                                layoutParams4 = RelativeLayout.LayoutParams(5*(fourth) + 30,5*(fourth) + 30)
                            }
                            fourthView.layoutParams = layoutParams4
                        }
                        // 메세지 여러개면 그림자 생기기
                        if (response.body()?.data?.messageCnt!! > 1) {
                            findViewById<ImageView>(R.id.my_opinion_text_shadow1).visibility = VISIBLE
                            findViewById<ImageView>(R.id.my_opinion_text_shadow2).visibility = VISIBLE
                        }
                    }
                } else if (response.code() == 401){
                    Log.i(TAG, "NowMessage_onResponse 401: 토큰 만료")
                    getToken(" ", null)
                } else {
                    Log.i(TAG, "NowMessage_onResponse 기타 코드: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<NowMessageResponse>, t: Throwable) {
                Log.d("NowMessage_onFailure", t.message.toString())
            }
        })
    }
    private fun getRecentFeed(header : String) : List<RecentFeedData> {
        var feedList : List<RecentFeedData> = ArrayList()
        api.getRecentFeed(header).enqueue(object : Callback<RecentFeedResponse> {
            override fun onResponse(call: Call<RecentFeedResponse>, response: Response<RecentFeedResponse>) {
                Log.d("getRecentFeed 응답", response.toString())
                if (response.code() == 200) {
                    feedList = response.body()!!.data
                    if (feedList.isNotEmpty()){
                        findViewById<TextView>(R.id.noFeed).visibility = GONE
                        findViewById<View>(R.id.cardLine).visibility = VISIBLE
                        val feed = findViewById<RecyclerView>(R.id.feed)
                        feedAdapter = FeedAdapter(this@MainActivity, feedList)
                        //feedAdapter.notifyDataSetChanged()
                        feed.adapter = feedAdapter
                        feed.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
                    }
                    Log.i(TAG, "getRecentFeed_onResponse feedList: $feedList")
                } else if (response.code() == 401){
                    Log.i(TAG, "getRecentFeed_onResponse 401: 토큰 만료")
                    getToken(" ", null)
                } else {
                    Log.i(TAG, "getRecentFeed_onResponse 코드: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<RecentFeedResponse>, t: Throwable) {
                Log.d("gerRecentFeed_onFailure", t.message.toString())
            }
        })
        if (feedList.isEmpty()) {
            val nofeedText = findViewById<TextView>(R.id.noFeed)
            nofeedText.visibility = VISIBLE
        }
        return feedList
    }
    fun getToken(type : String, double : HashMap<String, Double>?) {
        lifecycleScope.launch {
            val refreshToken = userDataStore.get_refresh_token.first()
            api.setAccessToken(refreshToken).enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    response.body()?.let {
                        if (it.status == 200) {
                            Log.i(TAG, "토큰 전송 응답 바디 ${response.body()?.data?.accessToken}")
                            response.body()?.data?.let {
                                it1 -> saveUserInfo(it1, type, double)
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
    fun saveUserInfo(payload : UserInfo, type : String, double : HashMap<String, Double>?){
        lifecycleScope.launch {
            userDataStore.setUserData(payload)
            val token = userDataStore.get_access_token.first()
            Log.d(TAG, "saveUserInfo: $type")
            if (type == " "){
                getRecentFeed(token)
                getNowMessage(token)
            } else if (double != null){
                setLocation(token, double)
            } else {
                setMessage(token, type)
            }
        }
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
    var backPressedTime : Long = 0
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