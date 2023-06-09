package com.cider.fourtytwo

import android.Manifest
import android.app.ActivityManager
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.cider.fourtytwo.dataStore.UserDataStore
import com.cider.fourtytwo.databinding.ActivitySigninBinding
import com.cider.fourtytwo.network.Api
import com.cider.fourtytwo.network.Model.MessageResponse
import com.cider.fourtytwo.network.Model.SignOutResponse
import com.cider.fourtytwo.network.RetrofitInstance
import com.cider.fourtytwo.signIn.UserInfo
import com.cider.fourtytwo.signIn.UserResponse
import com.cider.fourtytwo.signup.SignupActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import kotlin.system.exitProcess

class SigninActivity : AppCompatActivity() {
    private var _binding: ActivitySigninBinding? = null
    private val binding get() = _binding!!

    private lateinit var GoogleSignResultLauncher: ActivityResultLauncher<Intent>
    val api: Api = RetrofitInstance.getInstance().create(Api::class.java)
    private lateinit var userDataStore: UserDataStore

    override fun onStart() {
        super.onStart()
        userDataStore = UserDataStore(this)
        // 권한을 거절한 유저에게 필요성을 게시하고 권한 재요청 -> 0 : 설정창 열기, X : 그냥 지나감
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            val builder = AlertDialog.Builder(this)
                .setTitle("42는 위치 기반 서비스로 위치 권한을 필요로 합니다.")
                .setMessage("위치 권한을 허용하시겠습니까?")
                .setNegativeButton("허용", positiveButtonClick)
                .setPositiveButton("거부",negativeButtonClick)
                .show()
            builder.create()
        }
        // 권한 없으면 권한 요청 팝업
        if (ContextCompat.checkSelfPermission(this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
        }
        // 권한 있으면
        else {
            // 로그인 되어있는 유저인지 확인
            val account = GoogleSignIn.getLastSignedInAccount(this)
            if (account == null) {
                Log.e("onStart Google account", "로그인 안 되어있음")
            } else {
                Log.e("onStart Google account", "로그인 완료된 상태")
                // 백그라운드 위치 전송 시작
                startLocationService()
                // 토큰 보내고
                lifecycleScope.launch {
                    val token = userDataStore.get_access_token.first()
                    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            return@OnCompleteListener
                        }
                        val fcmtoken = task.result
                        setFcmToken(token, fcmtoken)
                        getToken(fcmtoken)
                    })
                }
            }
        }
    }
    // 다이얼로그 유저 확인에 쓰이는 함수
    val positiveButtonClick = { dialogInterface: DialogInterface, i: Int ->
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        startActivity(intent)
        dialogInterface.cancel()
    }
    val negativeButtonClick = { dialogInterface: DialogInterface, i: Int ->
        dialogInterface.cancel()
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 권한이 허용되었을 때 처리할 내용
                    // 로그인 되어있는 유저인지 확인
                    val account = GoogleSignIn.getLastSignedInAccount(this)
                    if (account == null) {
                        Log.e("onStart Google account", "로그인 안 되어있음")
                    } else {
                        Log.e("onStart Google account", "로그인 완료된 상태")
                        // 백그라운드 위치 전송 시작
                        startLocationService()
                        // 토큰 보내고
                        lifecycleScope.launch {
                            val token = userDataStore.get_access_token.first()
                            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                                if (!task.isSuccessful) {
                                    return@OnCompleteListener
                                }
                                val fcmtoken = task.result
                                setFcmToken(token, fcmtoken)
                                getToken(fcmtoken)
                            })
                        }
                    }
                }
                return
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(androidx.appcompat.R.style.Theme_AppCompat_Light_NoActionBar) // 화면이 구성될 때, 스플래시 -> 노액션바 테마로 변경
        super.onCreate(savedInstanceState)
        _binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

// 구글 로그인
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.web_client_id)
            .requestServerAuthCode(BuildConfig.web_client_id)
            .requestEmail()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        GoogleSignResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val task: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleSignInResult(task)
        }
        binding.loginGoogle.setOnClickListener {
            val signIntent: Intent = mGoogleSignInClient.signInIntent
            GoogleSignResultLauncher.launch(signIntent)
        }
    }
    private fun setFcmToken(header:String, fcmToken:String){
        val params = HashMap<String, String>()
        params["token"] = fcmToken
        Log.i(TAG, "setFcmToken: $fcmToken")
        api.setFcmToken(header, params).enqueue(object : Callback<MessageResponse> {
            override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                Log.i(TAG, "setFcmToken: ${response.body()}")
                if (response.code() == 200) {
                    Log.i(TAG, "setFcmToken 보냄")
                } else if (response.code() == 401){
                    Log.i(TAG, "setFcmToken 토큰 만료")
                    getToken(fcmToken)
                } else {
                    Log.i(TAG, "setFcmToken 기타: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                Log.d("setFcmToken on failuare", t.message.toString())
            }
        })
    }
    fun getToken(fcmToken: String) {
        lifecycleScope.launch {
            val refreshToken = userDataStore.get_refresh_token.first()
            api.setAccessToken(refreshToken).enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    response.body()?.let {
                        if (it.status == 200) {
                            response.body()?.data?.let {it1 -> saveUserInfo(it1, fcmToken)}
                            val intent = Intent(this@SigninActivity, MainActivity::class.java)
                            startActivity(intent)
                        } else if (it.status == 401) {
                            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestIdToken(BuildConfig.web_client_id)
                                .requestServerAuthCode(BuildConfig.web_client_id)
                                .requestEmail()
                                .build()
                            val signIntent = GoogleSignIn.getClient(this@SigninActivity, gso)
                            signIntent.signOut()
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
    fun saveUserInfo(payload : UserInfo, fcmToken: String){
        lifecycleScope.launch {
            userDataStore.setUserData(payload)
            val token = userDataStore.get_access_token.first()
            setFcmToken(token, fcmToken)
        }
    }
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val googletokenAuth = account?.idToken.toString()
            // 유저 idToken, email 저장
            userDataStore = UserDataStore(this)
            lifecycleScope.launch {
                account.email?.let {
                    userDataStore.setUserIdToken(googletokenAuth)
                    userDataStore.setUserEmail(account?.email!!)
                }
            }
            // 회원인지 확인
            checkUser(googletokenAuth, this)
        } catch (e: ApiException) {
            // 취소 시 로그인 창으로
            if (e.statusCode == GoogleSignInStatusCodes.CANCELED) {
                val intent = Intent(this, SigninActivity::class.java)
                startActivity(intent)
                Toast.makeText(applicationContext, "로그인이 필요한 서비스입니다.", Toast.LENGTH_SHORT).show()
            } else {
                Log.e("Google account", "signInResult:failed Code = " + e.statusCode)
            }
        } catch (e: JSONException) {
            Log.e("Google account", "Error getting AccessToken from response", e)

        } catch (e: IOException) {
            Log.e("Google account", "Error getting AccessToken from response", e)
        }
    }
    private fun checkUser(idToken: String, context : Context) {
        val params = HashMap<String, String>()
        params["o_auth_token"] = idToken
        api.getGoogleUser(params).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                response.body()?.data?.let {
                    if (it.user_idx > 0) {
                        saveUserInfo(it)
                        // 백그라운드 위치 전송 시작
                        if (ContextCompat.checkSelfPermission(this@SigninActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            startLocationService()}
                        // 파이어베이스 메세지 토큰
                        lifecycleScope.launch {
                            val token = userDataStore.get_access_token.first()
                            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                                if (!task.isSuccessful) {
                                    return@OnCompleteListener
                                }
                                val fcmtoken = task.result
                                setFcmToken(token, fcmtoken)
                            })
                        }
                        val intent = Intent(context, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        // 백그라운드 위치 전송 시작
                        if (ContextCompat.checkSelfPermission(this@SigninActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            startLocationService()}
                        val intent = Intent(context, SignupActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Log.d("log", t.message.toString())
            }
        })
    }
    fun saveUserInfo(payload : UserInfo){
        lifecycleScope.launch {
            userDataStore.setUserData(payload)
        }
    }
    private fun isLocationServiceRunning(): Boolean {
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        if (activityManager != null) {
            for (service in activityManager.getRunningServices(Int.MAX_VALUE)) {
                if (LocationService::class.java.name == service.service.className) {
                    if (service.foreground) {
                        return true
                    }
                }
            }
            return false
        }
        return false
    }

    private fun startLocationService() {
        if (!isLocationServiceRunning()) {
            val intent = Intent(applicationContext, LocationService::class.java)
            intent.action = Constants.ACTION_START_LOCATION_SERVICE
            startService(intent)
            Toast.makeText(this, "Location service started", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopLocationService() {
        if (isLocationServiceRunning()) {
            val intent = Intent(applicationContext, LocationService::class.java)
            intent.action = Constants.ACTION_STOP_LOCATION_SERVICE
            startService(intent)
            Toast.makeText(this, "Location service stopped", Toast.LENGTH_SHORT).show()
        }
    }
}