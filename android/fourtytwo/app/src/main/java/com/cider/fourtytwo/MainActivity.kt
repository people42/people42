package com.cider.fourtytwo

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
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
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.*


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
    // 소켓
    private lateinit var webSocket: WebSocket
    var markerList =  ArrayList<Marker>()

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
        val soketToggleOff = findViewById<ImageView>(R.id.main_radar_off)
        val soketToggleOn = findViewById<ImageView>(R.id.main_radar_on)
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
        val emojiBox = findViewById<RelativeLayout>(R.id.home_emoji_box)
        val newText = newMessage.text
        newMessageButton.setOnClickListener {
            val content = newText.toString()
            myOpinion.text = content
            emojiBox.visibility = GONE
            if (content == " ") {
                Toast.makeText(this, "당신의 스쳐가는 생각을 남겨주세요", Toast.LENGTH_SHORT).show()
            }else{
                lifecycleScope.launch {
                    setMessage(userDataStore.get_access_token.first(), content)
                    newMessage.text.clear()
                }
                hideKeyboard()
            }
        }
        // 텍스트 작성 중 enter키 눌렀을 때도 메세지 전송 로직 실행
        newMessage.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val content = newText.toString()
                myOpinion.text = content
                emojiBox.visibility = GONE
                if (content == " ") {
                    Toast.makeText(this, "당신의 스쳐가는 생각을 남겨주세요", Toast.LENGTH_SHORT).show()
                }else{
                    lifecycleScope.launch {
                        setMessage(userDataStore.get_access_token.first(), content)
                        newMessage.text.clear()
                    }
                    hideKeyboard()
                }
                true
            } else {
                false
            }
        }
        newMessage.setOnFocusChangeListener { view, b ->
            //Variable 'b' represent whether this view has focus.
            //If b is true, that means "This view is having focus"
            lifecycleScope.launch {
                if (userDataStore.get_webSocket.first()) {
                    if (b) {
                        val json = JSONObject().apply {
                            put("method", "CHANGE_STATUS")
                            put("status", "writing")
                        }
                        sendMessage(json.toString())
                    } else {
                        val json = JSONObject().apply {
                            put("method", "CHANGE_STATUS")
                            put("status", "watching")
                        }
                        sendMessage(json.toString())
                    }
                }
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
            if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                val myLocation = map?.myLocation
                if (myLocation != null) {
                    val currentLatLng = LatLng(myLocation.latitude, myLocation.longitude)
                    val cameraUpdate =
                        CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM.toFloat())
                    map?.animateCamera(cameraUpdate)
                }
            }else {
                Toast.makeText(applicationContext, "위치 권한이 없습니다.", Toast.LENGTH_SHORT).show()
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
                                    if (userDataStore.get_webSocket.first()){
                                        val json = JSONObject().apply {
                                            put("method", "Move")
                                            put("latitude", location.latitude)
                                            put("longitude", location.longitude)
                                            put("status", "watching")
                                        }
                                        sendMessage(json.toString())
                                    }}}}}
                handler.postDelayed(this, 60000)
            }}
        handler.post(runnable)

// 소켓
        val socketText = findViewById<TextView>(R.id.socket_toggle_text)
        soketToggleOff.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                socket()
            } else {
                Toast.makeText(applicationContext, "위치 권한이 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        soketToggleOn.setOnClickListener {
            lifecycleScope.launch {
                userDataStore.setWebSocket(false)
            }
            // 레이더 애니메이션 끄기
            val blueRing = findViewById<ImageView>(R.id.blue_ring)
            blueRing.clearAnimation()
            // 토글 버튼 이미지 바꾸기
            socketText.visibility = VISIBLE
            soketToggleOff.visibility = VISIBLE
            soketToggleOn.visibility = GONE
            //소켓 연결 닫겠다고 백서버에 전달
            val json = JSONObject().apply {
                put("method", "CLOSE")
            }
            sendMessage(json.toString())
            // 소켓 닫음
            stop()
            // 모든 마커 삭제
            map?.clear()
            markerList.clear()
        }

//피드
        val bottomSheet = findViewById<CardView>(R.id.bottom_sheet)
        // 현재 접힌 상태에서의 BottomSheet 귀퉁이의 둥글기 저장
        val cornerRadius = bottomSheet.radius
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        bottomSheetBehavior.addBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback() {
            val expandText = findViewById<TextView>(R.id.feed_expand_text)
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // 상태가 변함에 따라서 할일
                if (newState == STATE_HALF_EXPANDED) {
                    expandText.visibility = GONE
                } else if (newState == STATE_COLLAPSED) {
                    expandText.visibility = VISIBLE
                    lifecycleScope.launch {
                        val token = userDataStore.get_access_token.first()
                        // 24시간 피드 가져오기
                        feedList = getRecentFeed(token)
                    }
                }
            }
            override fun onSlide(bottomSheetView: View, slideOffset: Float) {
                // slideOffset 접힘 -> 펼쳐짐: 0.0 ~ 1.0
                if (slideOffset >= 0) {
                    // 둥글기는 펼칠수록 줄어들도록
                    bottomSheet.radius = cornerRadius - (cornerRadius * slideOffset)
                    // 화살표는 완전히 펼치면 180도 돌아가게
//                    expandArrow.rotation =  (1 - slideOffset) * 180F
                    // 글자는 조금더 빨리 사라지도록
                    expandText.alpha = 1 - slideOffset * 2.3F
                    // 내용의 투명도도 같이 조절...
                    findViewById<FrameLayout>(R.id.feed_expand_content).alpha = Math.min(slideOffset * 2F, 1F)
                }
            }
        })
// 당겨서 새로고침
//        var swipe = findViewById<SwipeRefreshLayout>(R.id.swipe)
//        swipe.setOnRefreshListener {
//            finish() //인텐트 종료
//            overridePendingTransition(0, 0) //인텐트 효과 없애기
//            val intent = intent //인텐트
//            startActivity(intent) //액티비티 열기
//            overridePendingTransition(0, 0) //인텐트 효과 없애기
//            swipe.isRefreshing = false
//        }
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
                    Log.i(ContentValues.TAG, "getNotiCnt 401: 토큰 만료")
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
                    Log.i(ContentValues.TAG, "getNotiCnt 코드: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<NotiCntResponse>, t: Throwable) {
                Log.d("gerRecentFeed_onFailure", t.message.toString())
            }
        })
    }
    fun socket(){
        lifecycleScope.launch {
            val locationManager = this@MainActivity.getSystemService(LOCATION_SERVICE) as LocationManager
            val userIdx = userDataStore.get_userIdx.first()
            if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    Toast.makeText(applicationContext, "위치 정보를 켜주세요", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                } else {
                    // Connect to the WebSocket server
                    start("wss://www.people42.com/be42/socket?type=user&user_idx=$userIdx")
                    lifecycleScope.launch {
                        userDataStore.setWebSocket(true)
                    }
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
                    // 토글 버튼 이미지 바꾸기
                    val socketText = findViewById<TextView>(R.id.socket_toggle_text)
                    val soketToggleOff = findViewById<ImageView>(R.id.main_radar_off)
                    val soketToggleOn = findViewById<ImageView>(R.id.main_radar_on)
                    socketText.visibility = GONE
                    soketToggleOff.visibility = GONE
                    soketToggleOn.visibility = VISIBLE
                    map?.isMyLocationEnabled = true
                    fusedLocationProviderClient.lastLocation
                        .addOnSuccessListener { location: Location? ->
                            if (location != null) {
                                val json = JSONObject().apply {
                                    put("method", "INIT")
                                    put("latitude", location.latitude)
                                    put("longitude", location.longitude)
                                    put("status", "watching")
                                }
                                sendMessage(json.toString())
                            }
                        }
                    val myLocation = map?.myLocation
                    if (myLocation != null) {
                        val currentLatLng = LatLng(myLocation.latitude, myLocation.longitude)
                        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM.toFloat())
                        map?.animateCamera(cameraUpdate)
                    }
                }
            }
        }
    }
    fun start(userUrl:String) {
        Log.i(TAG, "웹소켓: $userUrl")
        val client = OkHttpClient.Builder()
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .build()

        val request = Request.Builder()
            .url(userUrl)
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
                super.onOpen(webSocket, response)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                println("웹소켓 Received message: $text")

                val response = Gson().fromJson(text, WebSocketInfo::class.java)

                when (response.method) {
                    "INFO" -> {
                        info(response.data.nearUsers, response.data.farUsers)
                    }
                    "NEAR" -> {
                        addMarker(response.data)
                    }
                    "CHANGE_STATUS" -> changeStatus(response.data)
                    "MESSAGE_CHANGED" -> messageChanged(response.data)
                    "CLOSE" -> {
                        deleteMarker(response.data)
                    }
                    "PING" -> pong()
                    else -> Log.e(TAG, "웹소켓 : 알 수 없는 method입니다.")
                }

            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                super.onMessage(webSocket, bytes)
                println("Received bytes: ${bytes.hex()}")
                Log.i(TAG, "웹소켓: onMessage")

            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosing(webSocket, code, reason)
                webSocket.close(1000, null)
                println("Closing: $code / $reason")
                Log.i(TAG, "웹소켓: onClosing")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
                super.onFailure(webSocket, t, response)
                println("Error: ${t.message}")
            }
        })
    }
    fun stop() {
        Log.i(TAG, "웹소켓: stop")
        webSocket.close(1000, null)
    }
    fun sendMessage(message: String) {
        Log.i(TAG, "웹소켓: sendMessage $message")
        webSocket.send(message)
    }
    fun info(near:ArrayList<NearFarUsers>, far:ArrayList<NearFarUsers>){
        // 이모지 추가
        if (near.isNotEmpty()){
            runOnUiThread {
                val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
                mapFragment.getMapAsync{ googleMap ->
            // 가까운 유저
                for (user in near) {
                    // 이미지 로딩 라이브러리(Glide)를 사용하여 URL에서 이미지를 로드
                    Glide.with(this@MainActivity)
                        .asBitmap()
                        .load("https://peoplemoji.s3.ap-northeast-2.amazonaws.com/emoji/animate/${user.emoji}.gif")
                        .override(100, 100)
                        .into(object : SimpleTarget<Bitmap>() {
                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap>?
                            ) {
                                // 로드한 이미지로 마커 아이콘 생성
                                val markerIcon =
                                    BitmapDescriptorFactory.fromBitmap(resource)
                                // 위치 랜덤하게 변경
                                val location = getRandomLocation(user.latitude, user.longitude, 30.0)
                                val newLocation = LatLng(location.first, location.second)
                                // 마커 추가
                                val markerOptions = MarkerOptions()
                                    .position(newLocation)
                                    .icon(markerIcon)
                                    .title(user.nickname)
                                    .snippet(user.message)
                                val marker = googleMap.addMarker(markerOptions)
                                if (marker != null) {
                                    markerList.add(marker)
                                }
                            }
                        })
                }}
            }
        }
        if(far.isNotEmpty()){
            for (user in far) {
                val farMarkers = markerList.filter { it.title == user.nickname }
                for (marker in farMarkers) {
                    marker.remove()
                    markerList.remove(marker)
                    Log.d(TAG, "웹소켓 : 멀어진 마커 지움$markerList")
                }
            }
        }
    }
    fun addMarker(user : InfoData){
        runOnUiThread {
            val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync{ googleMap ->
                // 이미지 로딩 라이브러리(Glide)를 사용하여 URL에서 이미지를 로드
                Glide.with(this@MainActivity)
                    .asBitmap()
                    .load("https://peoplemoji.s3.ap-northeast-2.amazonaws.com/emoji/animate/${user.emoji}.gif")
                    .override(100, 100)
                    .into(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            // 로드한 이미지로 마커 아이콘 생성
                            val markerIcon =
                                BitmapDescriptorFactory.fromBitmap(resource)
                            // 위치 랜덤하게 변경
                            val location = getRandomLocation(user.latitude, user.longitude, 30.0)
                            val newLocation = LatLng(location.first, location.second)
                            // 마커 추가
                            val markerOptions = MarkerOptions()
                                .position(newLocation) // 마커 위치
                                .icon(markerIcon) // 마커 아이콘
                                .title(user.nickname)
                            val marker = googleMap.addMarker(markerOptions)
                            if (marker != null) {
                                markerList.add(marker)
                            }
                        }
                    })
                }
        }
    }
    fun changeStatus(user: InfoData){
        runOnUiThread {
            val farMarkers = markerList.lastOrNull { it.title == user.nickname }
            // 쓰는 중이면 말풍선 이모지로 변경
            if (user.status == "writing"){
                if (farMarkers != null) {
                    val drawableList = listOf(
                        "left_speech_bubble",
                        "right_anger_bubble",
                        "thought_balloon",
                        "speech_balloon"
                    )
                    val randomIndex = Random().nextInt(drawableList.size)
                    val resourceId =
                        resources.getIdentifier(drawableList[randomIndex], "drawable", packageName)
                    val bitmap = BitmapFactory.decodeResource(resources, resourceId)
                    val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 130, 130, false)
                    val markerIcon = BitmapDescriptorFactory.fromBitmap(resizedBitmap)
                    farMarkers.setIcon(markerIcon)
                    farMarkers.snippet="생각중..."
                    farMarkers.showInfoWindow()
                    val handler = Handler()
                    handler.postDelayed({
                        farMarkers.hideInfoWindow()
                    }, 3000)
                }
            // 보는 중이면 사람 이모지로 변경
            } else{
                if (farMarkers != null) {
                    Glide.with(this@MainActivity)
                        .asBitmap()
                        .load("https://peoplemoji.s3.ap-northeast-2.amazonaws.com/emoji/animate/${user.emoji}.gif")
                        .override(100, 100)
                        .into(object : SimpleTarget<Bitmap>() {
                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap>?
                            ) {
                                val markerIcon = BitmapDescriptorFactory.fromBitmap(resource)
                                farMarkers.setIcon(markerIcon)
                                // 메세지 바뀌었으면 3초간 띄워주기
                                if (farMarkers.snippet == user.message){
                                    farMarkers.snippet = user.message
                                    farMarkers.showInfoWindow()
                                    val handler = Handler()
                                    handler.postDelayed({
                                        farMarkers.hideInfoWindow()
                                    }, 3000)}
//                                // 메세지 안 바뀌었으면 말풍선 숨기기
//                                else{
//                                    farMarkers.hideInfoWindow()
//                                }
                            }
                        })
                }
            }
        }
    }
    fun messageChanged(user: InfoData){
        //이모지로 변경 + title 달아주기?
        runOnUiThread {
            val farMarkers = markerList.lastOrNull { it.title == user.nickname }
            if (farMarkers != null) {
                farMarkers.snippet = user.message
//                farMarkers.showInfoWindow()
            }
        }
    }
    fun deleteMarker(user: InfoData){
        // 마커 삭제
        runOnUiThread {
        val farMarkers = markerList.filter { it.title == user.nickname }
        for (marker in farMarkers) {
            marker.remove()
            markerList.remove(marker)
        }}
    }
    fun pong(){
        val json = JSONObject().apply {
            put("method", "PONG")
        }
        sendMessage(json.toString())
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
        params["message"] = myMessage
        api.setMessage(Header, params).enqueue(object : Callback<MessageResponse> {
            override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                if (response.code() == 200) {
                    findViewById<View>(R.id.layout_opinion).visibility = VISIBLE
                    findViewById<View>(R.id.layout_edit_opinion).visibility = GONE
                    // 새로 작성한 메세지 소켓에 전송
                    lifecycleScope.launch {
                        if (userDataStore.get_webSocket.first()) {
                            val json = JSONObject().apply {
                                put("method", "MESSAGE_CHANGED")
                                put("status", "watching")
                            }
                            sendMessage(json.toString())
                        }
                    }
                } else if (response.code() == 401){
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
    private fun getRecentFeed(header : String) : ArrayList<RecentFeedData> {
        var feedList : ArrayList<RecentFeedData> = ArrayList()
        api.getRecentFeed(header).enqueue(object : Callback<RecentFeedResponse> {
            override fun onResponse(call: Call<RecentFeedResponse>, response: Response<RecentFeedResponse>) {
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
                } else if (response.code() == 401){
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
    fun setEmotion(params : HashMap<String, Any>){
        lifecycleScope.launch {
            val token = userDataStore.get_access_token.first()
            val refreshToken = userDataStore.get_refresh_token.first()
            api.setEmotion(token, params).enqueue(object : Callback<MessageResponse> {
                override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                    response.body()?.let {
                        if (it.status == 200) {
                        } else if (response.code() == 401){
                            api.setAccessToken(refreshToken).enqueue(object : Callback<UserResponse> {
                                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                                    response.body()?.let {
                                        if (it.status == 200) {
                                            response.body()?.data?.let {
                                                it1 -> lifecycleScope.launch {
                                                    userDataStore.setUserData(it1)
                                                    setEmotion(params)
                                        } }
                                        } else {
                                            Log.i(TAG, "토큰 전송 실패 코드: ${response.code()}")
                                } } }
                                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                                    Log.d("토큰 전송 on failure: ", t.message.toString())
                        } })
                        }else {
                            Log.i(TAG, "공감 실패 코드: ${response.code()}")
                } } }
                override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                    Log.d("공감 on failure: ", t.message.toString())
    } }) } }
    fun getToken(type : String, double : HashMap<String, Double>?) {
        lifecycleScope.launch {
            val refreshToken = userDataStore.get_refresh_token.first()
            api.setAccessToken(refreshToken).enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    response.body()?.let {
                        if (it.status == 200) {
                            response.body()?.data?.let {
                                it1 -> saveUserInfo(it1, type, double)
                            }
                        } else if (it.status == 404 || it.status == 401) {
                            Toast.makeText(this@MainActivity, "로그아웃 후, 다시 로그인 해주세요", Toast.LENGTH_SHORT).show()
                        } else if (it.status == 500) {
                            Toast.makeText(this@MainActivity, "서비스 점검중입니다. 잠시 서비스 이용이 중단됩니다. 양해 부탁드립니다!", Toast.LENGTH_SHORT).show()
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
            else -> super.onOptionsItemSelected(item)
        }
    }
private var backButtonPressedTime: Long = 0
    private val backButtonThreshold: Long = 2000
    override fun onBackPressed() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - backButtonPressedTime < backButtonThreshold) {
            finishAffinity()
        } else {
            backButtonPressedTime = currentTime
            Toast.makeText(this, "한번 더 클릭 시 앱이 종료됩니다.", Toast.LENGTH_SHORT).show()
        }
    }
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
        getLocationPermission()
        updateLocationUI()
        getDeviceLocation()
        // 마커 클릭해도 이동하지 말기
        map.setOnMarkerClickListener { marker ->
//            if (marker.isInfoWindowShown){ marker.showInfoWindow() }
//            else{ marker.hideInfoWindow() }
            true // 이벤트 처리 완료
        }
        // 소켓 디폴트 설정이 true면 소켓 on 상태로 전환
        lifecycleScope.launch {
            val soketToggleOff = findViewById<ImageView>(R.id.main_radar_off)
            val soketToggleOn = findViewById<ImageView>(R.id.main_radar_on)
            val soketText = findViewById<TextView>(R.id.socket_toggle_text)
            val blueRing = findViewById<ImageView>(R.id.blue_ring)
            if (userDataStore.get_webSocket.first()) {
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
                blueRing.startAnimation(scaleAnimation)
                // 토글 버튼 이미지 바꾸기
                soketToggleOff.visibility = GONE
                soketText.visibility = GONE
                soketToggleOn.visibility = VISIBLE
                //소켓 연결
                socket()
            } else {
                soketToggleOff.visibility = VISIBLE
                soketText.visibility = VISIBLE
                soketToggleOn.visibility = GONE
                blueRing.clearAnimation()
            }
        }
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
        if (ContextCompat.checkSelfPermission(this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true
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
                map?.uiSettings?.isZoomGesturesEnabled = false // 줌막기
                map?.uiSettings?.isScrollGesturesEnabled = false // 드래그 막기
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }
    companion object {
        private const val DEFAULT_ZOOM = 19
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
        private const val KEY_CAMERA_POSITION = "camera_position"
        private const val KEY_LOCATION = "location"
    }

    // 반지금 radius m내의 랜덤 위경도로 변경
    fun getRandomLocation(latitude: Double, longitude: Double, radius: Double): Pair<Double, Double> {
        // 반경(m)으로 지구 상의 1도의 거리(km) 계산
        val kmPerDegree = 111.319

        // 반경(m)을 km로 변환
        val kmRadius = radius / 1000.0

        // 랜덤한 각도(라디안) 생성
        val angle = 2.0 * Math.PI * Math.random()

        // 랜덤한 거리(0~1 사이) 생성
        val u = Math.random() + Math.random()

        // 반경 내의 새로운 위도 계산
        val latitudeOffset = (u * kmRadius / kmPerDegree) * cos(angle)
        val newLatitude = latitude + latitudeOffset

        // 반경 내의 새로운 경도 계산
        val longitudeOffset = (u * kmRadius / kmPerDegree) * sin(angle)
        val newLongitude = longitude + longitudeOffset

        // 새로운 위도와 경도 반환
        return Pair(newLatitude, newLongitude)
    }

    override fun onPause() {
        super.onPause()
        lifecycleScope.launch {
            if (userDataStore.get_webSocket.first()){
                //소켓 연결 닫겠다고 백서버에 전달
                val json = JSONObject().apply {
                    put("method", "CLOSE")
                }
                sendMessage(json.toString())
                // 소켓 닫음
                stop()
                // 모든 마커 삭제
                map?.clear()
                markerList.clear()
            }
        }
    }
    fun hideKeyboard() {
        val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }
    fun madeBrushCnt(latitude:Double, longitude:Double){
        // 1분에 한번씩 위치 전송
        val backgrounduserDataStore = UserDataStore(this)
        var mylocation = HashMap<String, Double>()
        mylocation["latitude"] = latitude
        mylocation["longitude"] = longitude
        lifecycleScope.launch {
            setLocation(backgrounduserDataStore.get_access_token.first(), mylocation)
        }
    }
}