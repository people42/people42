package com.cider.fourtytwo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebView
import android.webkit.WebViewClient

class WebViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // 화면이 구성될 때, 스플래시 테마에서 메인 테마로 변경
        setTheme(R.style.Theme_Fourtytwo)
        // 로고 장착
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayUseLogoEnabled(true)
        supportActionBar?.title = null      // 타이틀 삭제
        supportActionBar?.elevation = 0.0F  // 상자 그림자 삭제
        supportActionBar?.setLogo(R.drawable.baseline_arrow_back_ios_new_24)
        setContentView(R.layout.activity_my_messages)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
//        val webView = findViewById<WebView>(R.id.webview)
//        webView.settings.javaScriptEnabled = true
//        webView.settings.domStorageEnabled = true
//        webView.webViewClient = WebViewClient()
//        webView.loadUrl("https://www.people42.com/policy?nav=false")

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
//                val intent = Intent(this, MainActivity::class.java)
//                startActivity(intent)
                onBackPressed()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}