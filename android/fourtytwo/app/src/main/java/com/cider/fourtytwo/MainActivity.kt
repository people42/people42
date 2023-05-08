package com.cider.fourtytwo

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cider.fourtytwo.dataStore.UserDataStore
import com.cider.fourtytwo.feed.FeedAdapter
import com.cider.fourtytwo.feed.RecentFeedData
import com.cider.fourtytwo.feed.RecentFeedResponse
import com.cider.fourtytwo.map.SetLocationResponse
import com.cider.fourtytwo.myHistory.MyMessagesActivity
import com.cider.fourtytwo.network.Api
import com.cider.fourtytwo.network.Model.*
import com.cider.fourtytwo.network.RetrofitInstance
import com.cider.fourtytwo.place.PlaceActivity
import com.cider.fourtytwo.signIn.UserInfo
import com.cider.fourtytwo.signIn.UserResponse
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    val api: Api = RetrofitInstance.getInstance().create(Api::class.java)
    //유저
    private lateinit var userDataStore: UserDataStore
    private lateinit var myEmoji: String
    //피드
    private lateinit var feedList: List<RecentFeedData>
    private lateinit var feedAdapter: FeedAdapter
    //지도
    private var map: GoogleMap? = null
    private var cameraPosition: CameraPosition? = null
    // The entry point to the Fused Location Provider.
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private val defaultLocation = LatLng(36.355324, 127.298268)
    private var locationPermissionGranted = false
    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private var lastKnownLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
//        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
//                return@OnCompleteListener
//            }
//
//            // Get new FCM registration token
//            val token = task.result
//
//            Log.d(TAG, "파이어베이스 $token")
//        })

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
            feedList = getRecentFeed(token)
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
//        feedAdapter.setItemClickListener(object: FeedAdapter.OnItemClickListener{
//            override fun onClick(v: View, position: Int) {
//                // 클릭 시 이벤트 작성
//                Log.d(TAG, "onClick: 작동하긴함")
//                Log.d(TAG, "onClick: ${feedList[position].placeWithTimeInfo.placeName}")

//                val intent = Intent(this@MainActivity, PlaceActivity::class.java)
//                intent.putExtra("placeIdx", feedList[position].placeWithTimeInfo.placeIdx)
//                intent.putExtra("time", feedList[position].placeWithTimeInfo.time)
//                intent.putExtra("placeName", feedList[position].placeWithTimeInfo.placeName)
//                startActivity(intent)
//            }
//        })

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
// 지도
        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION)
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION)
        }
        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        // Build the map.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        // 가운데 클릭 시 내 위치로 이동
        findViewById<ImageView>(R.id.myLocation).setOnClickListener {
            val myLocation = map?.myLocation
            if (myLocation != null) {
                val currentLatLng = LatLng(myLocation.latitude, myLocation.longitude)
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f)
                map?.animateCamera(cameraUpdate)
            }
        }
        // 1분에 한번씩 위치 전송
        val handler = Handler()
        val runnable = object : Runnable {
            override fun run() {
                if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationProviderClient.lastLocation
                        .addOnSuccessListener { location: Location? ->
                            if (location != null) {
                                var mylocation = HashMap<String, Double>()
                                mylocation.put("latitude", location.latitude)
                                mylocation.put("longitude", location.longitude)
                                lifecycleScope.launch {
                                    setLocation(userDataStore.get_access_token.first(), mylocation)
                                }
                            }
                        }
                }
                handler.postDelayed(this, 60000)
            }
        }
        handler.post(runnable)
    }
    private fun setLocation(header : String, params : HashMap<String, Double>?){
        api.setLocation(header, params).enqueue(object : Callback<SetLocationResponse> {
            override fun onResponse(call: Call<SetLocationResponse>, response: Response<SetLocationResponse>) {
                Log.d("위치 전송 응답", response.toString())
                if (response.code() == 200) {
                } else if (response.code() == 401){
                    Log.i(TAG, "위치 전송 응답 토큰 만료")
                    getToken("location", params)
                } else {
                    Log.i(TAG, "위치 전송 응답 기타: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<SetLocationResponse>, t: Throwable) {
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

                        if (response.body()!!.data.heart > 0) {
                            val fourth = response.body()!!.data.heart
                            val width = 5*(fourth) + 90
                            val layoutParams = firstView.layoutParams
                            layoutParams.width = width // 가로 크기 지정
                            layoutParams.height = width // 세로 크기 지정
                            firstView.layoutParams = layoutParams
                            firstView.visibility = VISIBLE
                        }
                        if (response.body()!!.data.tear > 0) {
                            val fourth = response.body()!!.data.tear
                            val width = 5*(fourth) + 90
                            val layoutParams = secondView.layoutParams
                            layoutParams.width = width // 가로 크기 지정
                            layoutParams.height = width // 세로 크기 지정
                            secondView.layoutParams = layoutParams
                            secondView.visibility = VISIBLE
                        }
                        if (response.body()!!.data.thumbsUp > 0) {
                            val fourth = response.body()!!.data.thumbsUp
                            val width = 5*(fourth) + 90
                            val layoutParams = thirdView.layoutParams
                            layoutParams.width = width // 가로 크기 지정
                            layoutParams.height = width // 세로 크기 지정
                            thirdView.layoutParams = layoutParams
                            thirdView.visibility = VISIBLE
                        }
                        if (response.body()!!.data.fire > 0) {
                            val fourth = response.body()!!.data.fire
                            val width = 5*(fourth) + 90
                            val layoutParams = fourthView.layoutParams
                            layoutParams.width = width // 가로 크기 지정
                            layoutParams.height = width // 세로 크기 지정
                            fourthView.layoutParams = layoutParams
                            fourthView.visibility = VISIBLE
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
                        feedAdapter.setItemClickListener(object: FeedAdapter.OnItemClickListener {
                            override fun onClick(v: View, position: Int) {
                                // 클릭 시 이벤트 작성
                                val intent = Intent(this@MainActivity, PlaceActivity::class.java)
                                intent.putExtra("placeIdx", feedList[position].placeWithTimeInfo.placeIdx)
                                intent.putExtra("time", feedList[position].placeWithTimeInfo.time)
                                intent.putExtra("placeName", feedList[position].placeWithTimeInfo.placeName)
                                startActivity(intent)
                            }
                        })
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
// 옵션 메뉴
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

    // [START ask_post_notifications]
    // Declare the launcher at the top of your Activity/Fragment:
//    private val requestPermissionLauncher = registerForActivityResult(
//        ActivityResultContracts.RequestPermission()
//    ) { isGranted: Boolean ->
//        if (isGranted) {
//            // FCM SDK (and your app) can post notifications.
//        } else {
//            // TODO: Inform user that that your app will not show notifications.
//        }
//    }
//
//    private fun askNotificationPermission() {
//        // This is only necessary for API level >= 33 (TIRAMISU)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
//                PackageManager.PERMISSION_GRANTED
//            ) {
//                // FCM SDK (and your app) can post notifications.
//            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
//                // TODO: display an educational UI explaining to the user the features that will be enabled
//                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
//                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
//                //       If the user selects "No thanks," allow the user to continue without notifications.
//            } else {
//                // Directly ask for the permission
//                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
//            }
//        }
//    }
    // [END ask_post_notifications]
    /**
     * Saves the state of the map when the activity is paused.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        map?.let { map ->
            outState.putParcelable(KEY_CAMERA_POSITION, map.cameraPosition)
            outState.putParcelable(KEY_LOCATION, lastKnownLocation)
        }
        super.onSaveInstanceState(outState)
    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    override fun onMapReady(map: GoogleMap) {
        this.map = map
        // Prompt the user for permission.
        getLocationPermission()
        // Turn on the My Location layer and the related control on the map.
        updateLocationUI()
        // Get the current location of the device and set the position of the map.
        getDeviceLocation()
    }
    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                LatLng(lastKnownLocation!!.latitude,
                                    lastKnownLocation!!.longitude), DEFAULT_ZOOM.toFloat()))
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.")
                        Log.e(TAG, "Exception: %s", task.exception)
                        map?.moveCamera(CameraUpdateFactory
                            .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat()))
                        map?.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
        updateLocationUI()
    }

    /**
     * Prompts the user to select the current place from a list of likely places, and shows the
     * current place on the map - provided the user has granted location permission.
     */

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    @SuppressLint("MissingPermission")
    private fun updateLocationUI() {
        if (map == null) {
            return
        }
        try {
            if (locationPermissionGranted) {
                map?.isMyLocationEnabled = true
                map?.uiSettings?.isMyLocationButtonEnabled = false // 내 위치 버튼 숨기기
//                map?.uiSettings?.isZoomGesturesEnabled = false // 줌막기
//                map?.uiSettings?.isScrollGesturesEnabled = false // 드래그 막기

            } else {
                map?.isMyLocationEnabled = false
                map?.uiSettings?.isMyLocationButtonEnabled = false
                lastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }
    companion object {
        private const val DEFAULT_ZOOM = 15
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
        // Keys for storing activity state.
        private const val KEY_CAMERA_POSITION = "camera_position"
        private const val KEY_LOCATION = "location"
    }
}