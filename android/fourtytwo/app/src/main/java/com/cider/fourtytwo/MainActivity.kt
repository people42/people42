package com.cider.fourtytwo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cider.fourtytwo.feed.FeedAdapter
import com.cider.fourtytwo.feed.FeedItem

class MainActivity : AppCompatActivity() {
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

        val feed = findViewById<RecyclerView>(R.id.feed)
        val feedList = ArrayList<FeedItem>()
        feedList.add(FeedItem("메세지가"))
        feedList.add(FeedItem("도착"))
        feedList.add(FeedItem("했습니다"))

        val feedAdapter = FeedAdapter(feedList)
        feedAdapter.notifyDataSetChanged()

        feed.adapter = feedAdapter
        feed.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }
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
                Toast.makeText(applicationContext, "설정 준비 중..", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}