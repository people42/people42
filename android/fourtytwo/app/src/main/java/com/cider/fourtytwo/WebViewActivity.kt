package com.cider.fourtytwo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient

class WebViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        val webView = findViewById<WebView>(R.id.webview)
//        webView.settings.javaScriptEnabled = true
//        webView.settings.domStorageEnabled = true
//        webView.webViewClient = WebViewClient()
        webView.loadUrl("https://www.people42.com/policy?nav=false")

    }
}