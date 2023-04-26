package com.cider.fourtytwo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "설정" // 타이틀 삭제
        supportActionBar?.elevation = 0.0F  // 상자 그림자 삭제

    }

}