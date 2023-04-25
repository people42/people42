package com.cider.fourtytwo

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.cider.fourtytwo.Signup.EmojiFragment
import com.cider.fourtytwo.Signup.NicknameFragment
import com.cider.fourtytwo.Signup.WelcomeFragment
import com.cider.fourtytwo.databinding.ActivitySignupBinding
import com.google.android.material.tabs.TabLayout

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    private lateinit var fragment1: NicknameFragment
    private lateinit var fragment2: EmojiFragment
    private lateinit var fragment3: WelcomeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}

//
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.content.DialogInterface
//import android.content.Intent
//import android.content.pm.ActivityInfo
//import android.os.Handler
//import android.os.Looper
//import android.util.Log
//import android.view.View
//import android.widget.Toast
//import androidx.appcompat.app.AlertDialog
//import androidx.core.graphics.drawable.toBitmap
//import androidx.core.view.isVisible
//import com.cider.fourtytwo.databinding.ActivitySignupBinding
//import java.util.*
//import kotlin.collections.ArrayList
//
//class SignupActivity : AppCompatActivity() {
//    private lateinit var binding: ActivitySignupBinding
//    private var isThread: Boolean? = null
//    private var isClicked: Boolean? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        setTheme(androidx.appcompat.R.style.Theme_AppCompat_Light_NoActionBar) // 화면이 구성될 때, 스플래시 -> 노액션바 테마로 변경
//        super.onCreate(savedInstanceState)
//        binding = ActivitySignupBinding.inflate(layoutInflater)
//        setContentView(R.layout.activity_signup)
//
//        val handler = Handler(Looper.getMainLooper())
//        val random = Random()
//
//        val pictureArrayList = ArrayList<Int>()
//        pictureArrayList.add(1)
//        pictureArrayList.add(2)
//        pictureArrayList.add(3)
//        pictureArrayList.add(4)
//        pictureArrayList.add(5)
//        pictureArrayList.add(6)
//        pictureArrayList.add(7)
//        pictureArrayList.add(8)
//
//        binding.slotOne.tag = 1
//        binding.slotTwo.tag = 1
//        binding.slotThree.tag = 1
//
//        // 슬롯 시작 버튼
//        binding.btnStart.setOnClickListener {
//            if (isThread != true) {
//                isThread = true
//
//                val thread = Thread {
//                    while (isThread!!) {
//                        val num1 = random.nextInt(pictureArrayList.size)
//                        val num2 = random.nextInt(pictureArrayList.size)
//                        val num3 = random.nextInt(pictureArrayList.size)
//                        Thread.sleep(80)
//
//                        handler.post {
//                            binding.slotOne.tag = num1
//                            binding.slotOne.setImageResource(R.drawable.logo_icon)
//
//                            binding.slotTwo.tag = num2
//                            binding.slotTwo.setImageResource(R.drawable.logo_first)
//
//                            binding.slotThree.tag = num3
//                            binding.slotThree.setImageResource(R.drawable.appicon)
//                        }
//                    }
//                }
//
//                thread.start()
//                isClicked = false
//            }
//        }
//
//        // 슬롯 멈춤 버튼
//        binding.btnStop.setOnClickListener {
//            isThread = false
//
//            if (isClicked == false) {
//                isClicked = true
//
//                Thread {
//                    Thread.sleep(85)
//                    val slotOneResult = binding.slotOne.tag
//                    val slotTwoResult = binding.slotTwo.tag
//                    val slotThreeResult = binding.slotThree.tag
//
//                    val resultArrayList = listOf(slotOneResult, slotTwoResult, slotThreeResult)
//                    val counts = resultArrayList.groupingBy { it }.eachCount()
//                    Log.v("로그", counts.toString())
//
//                }.start()
//
//                Thread {
//                    Thread.sleep(90)
//                }.start()
//            }
//        }
//    }
//}