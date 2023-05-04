package com.cider.fourtytwo

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cider.fourtytwo.dataStore.UserDataStore
import com.cider.fourtytwo.databinding.ActivitySigninBinding
import com.cider.fourtytwo.network.Api
import com.cider.fourtytwo.network.Model.UserResponse
import com.cider.fourtytwo.network.RetrofitInstance
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.launch
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class SigninActivity : AppCompatActivity() {
    private var _binding: ActivitySigninBinding? = null
    private val binding get() = _binding!!

    private lateinit var GoogleSignResultLauncher: ActivityResultLauncher<Intent>
    val api = RetrofitInstance.getInstance().create(Api::class.java)
    private val viewModel: UserViewModel by viewModels()
    private lateinit var userDataStore: UserDataStore

    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(this)
        Log.d(TAG, "onStart Google account: ${account}")
        if (account == null) {
            Log.e("onStart Google account", "로그인 안 되어있음")
        } else {
            Log.e("onStart Google account", "로그인 완료된 상태")
            // 메인으로 이동
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
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
            var signIntent: Intent = mGoogleSignInClient.signInIntent
            GoogleSignResultLauncher.launch(signIntent)
        }
    }

    fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val googletokenAuth = account?.idToken.toString()
            // 유저 정보 저장
            userDataStore = UserDataStore(this)
            lifecycleScope.launch {
                account.email?.let {
                    val check = userDataStore.setUserEmail(it)
                    userDataStore.setUserIdToken(googletokenAuth)
                }
                userDataStore.getUserEmail()
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

    fun checkUser(idToken: String, context : Context) {
        var params = HashMap<String, String>()
        params.put("o_auth_token", idToken)
        api.getGoogleUser(params).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                response.body()?.data?.let {
                    if (it.user_idx > 0) {
                        val intent = Intent(context, MainActivity::class.java)
                        startActivity(intent)
                    } else {
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
}