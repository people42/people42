package com.cider.fourtytwo

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.media.metrics.LogSessionId
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.cider.fourtytwo.dataStore.UserDataStore
import com.cider.fourtytwo.databinding.ActivitySettingsBinding
import com.cider.fourtytwo.network.Api
import com.cider.fourtytwo.network.Model.NotiCntResponse
import com.cider.fourtytwo.network.Model.SignOutResponse
import com.cider.fourtytwo.signIn.UserInfo
import com.cider.fourtytwo.signIn.UserResponse
import com.cider.fourtytwo.network.RetrofitInstance
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingsActivity : AppCompatActivity() {
    private val binding: ActivitySettingsBinding by lazy { ActivitySettingsBinding.inflate(layoutInflater) }
    private lateinit var userDataStore: UserDataStore
    val api: Api = RetrofitInstance.getInstance().create(Api::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Fourtytwo)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        userDataStore = UserDataStore(this)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.web_client_id)
            .requestServerAuthCode(BuildConfig.web_client_id)
            .requestEmail()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(this,gso)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayUseLogoEnabled(true)
        supportActionBar?.title = "설정"
        supportActionBar?.elevation = 0.0F  // 상자 그림자 삭제
        supportActionBar?.setLogo(R.drawable.baseline_arrow_back_ios_new_24) // 뒤로가기이미지

        binding.privacyPolicy.setOnClickListener {
            val intent = Intent(this, WebViewActivity::class.java)
            startActivity(intent)
//            if(binding.privacyPolicyWebview.visibility == VISIBLE) {
//                binding.privacyPolicyWebview.visibility = GONE
////                binding.layoutBtn01.animate().apply {
////                    duration = 300
////                    rotation(0f)
////                }
//            } else {
//                binding.privacyPolicyWebview.visibility = VISIBLE
//                binding.termsConditionsWebview.visibility = GONE
//                binding.layoutBtn01.animate().apply {
//                    duration = 300
//                    rotation(180f)
//                }
//            }
        }
//        binding.termsConditions.setOnClickListener {
//            if (binding.termsConditionsWebview.visibility == View.VISIBLE) {
//                binding.termsConditionsWebview.visibility = View.GONE
////                binding.layoutBtn01.animate().apply {
////                    duration = 300
////                    rotation(0f)
////                }
//            } else {
//                binding.termsConditionsWebview.visibility = View.VISIBLE
//                binding.privacyPolicyWebview.visibility = GONE
//
////                binding.layoutBtn01.animate().apply {
////                    duration = 300
////                    rotation(180f)
////                }
//            }
//        }
        binding.signout.setOnClickListener{
//            binding.privacyPolicyWebview.visibility = GONE
//            binding.termsConditionsWebview.visibility = View.GONE
            lifecycleScope.launch {
                signOut(userDataStore.get_access_token.first())
            }
            mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, OnCompleteListener {
                    val intent = Intent(this, SigninActivity::class.java)
                    startActivity(intent)
                })
        }
        binding.withdrawal.setOnClickListener{
//            binding.privacyPolicyWebview.visibility = GONE
//            binding.termsConditionsWebview.visibility = View.GONE
            lifecycleScope.launch {
                withdrawal(userDataStore.get_access_token.first())
            }
            mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, OnCompleteListener {
                    Toast.makeText(this@SettingsActivity, "회원 탈퇴가 완료 되었습니다.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, SigninActivity::class.java)
                    startActivity(intent)
                })
        }
    }
    private fun signOut(header : String){
        api.signOut(header).enqueue(object : Callback<SignOutResponse> {
            override fun onResponse(call: Call<SignOutResponse>, response: Response<SignOutResponse>) {
                Log.d("signOut 응답", response.toString())
                if (response.code() == 200) {
                    Log.i(ContentValues.TAG, "signOut 응답 바디: ${response.body()}")
                    Toast.makeText(this@SettingsActivity, "안전하게 로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                } else if (response.code() == 401){
                    Log.i(ContentValues.TAG, "signOut 401: 토큰 만료")
                    getToken(1)
                } else {
                    Log.i(ContentValues.TAG, "signOut 코드: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<SignOutResponse>, t: Throwable) {
                Log.d("signOut 실패", t.message.toString())
            }
        })
    }
    private fun withdrawal(header : String){
        Log.i(TAG, "withdrawal: 들어옴")
        api.withdrawal(header).enqueue(object : Callback<SignOutResponse> {
            override fun onResponse(call: Call<SignOutResponse>, response: Response<SignOutResponse>) {
                Log.d("withdrawal 응답", response.toString())
                if (response.code() == 200) {
                    Log.i(ContentValues.TAG, "withdrawal 응답 바디: ${response.body()}")
                } else if (response.code() == 401){
                    Log.i(ContentValues.TAG, "withdrawal 401: 토큰 만료")
                    getToken(2)
                } else {
                    Log.i(ContentValues.TAG, "withdrawal 코드: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<SignOutResponse>, t: Throwable) {
                Log.d("withdrawal 실패", t.message.toString())
            }
        })
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        lifecycleScope.launch {
            val token = userDataStore.get_access_token.first()
            getNotiCnt(token, menu)
        }
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.action_notifications -> {
                val intent = Intent(this, NotificationActivity::class.java)
                startActivity(intent)
                true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    fun getToken(logic : Int) {
        lifecycleScope.launch {
            val refreshToken = userDataStore.get_refresh_token.first()
            Log.i(TAG, "getToken: ${refreshToken}")
            api.setAccessToken(refreshToken).enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    Log.d("토큰 전송 on response", response.toString())
                    Log.d("토큰 전송 on response", response.body().toString())
                    response.body()?.let {
                        if (it.status == 200) {
                            Log.i(ContentValues.TAG, "토큰 전송 응답 바디 ${response.body()?.data?.accessToken}")
                            response.body()?.data?.let {
                                    it1 -> saveUserInfo(it1, logic)
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
    }
    fun saveUserInfo(payload : UserInfo, logic : Int){
        lifecycleScope.launch {
            userDataStore.setUserData(payload)
            val token = userDataStore.get_access_token.first()
            if (logic == 1) {
                signOut(token)
            } else {
                withdrawal(token)
            }
        }
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
                    Log.i(ContentValues.TAG, "getRecentFeed_onResponse 401: 토큰 만료")
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
                    Log.i(ContentValues.TAG, "getRecentFeed_onResponse 코드: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<NotiCntResponse>, t: Throwable) {
                Log.d("gerRecentFeed_onFailure", t.message.toString())
            }
        })
    }
}