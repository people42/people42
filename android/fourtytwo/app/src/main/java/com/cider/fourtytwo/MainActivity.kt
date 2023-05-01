package com.cider.fourtytwo

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cider.fourtytwo.feed.FeedAdapter
import com.cider.fourtytwo.feed.FeedItem
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class MainActivity : AppCompatActivity(){
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

        // 내 이모지
        val myEmojiView: ImageView = findViewById(R.id.my_opinion_emoji)
        Glide.with(this).load(R.raw.robot).into(myEmojiView)

        // 피드
        val feed = findViewById<RecyclerView>(R.id.feed)
        val feedList = ArrayList<FeedItem>()
        feedList.add(FeedItem("메세지가"))
        feedList.add(FeedItem("도착"))
        feedList.add(FeedItem("했습니다"))

        val feedAdapter = FeedAdapter(feedList)
        feedAdapter.notifyDataSetChanged()
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
//                googleMap.moveCamera(CameraUpdateFactory.zoomTo(15f))
//                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 20))
            }
//            addMarkers(googleMap)
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