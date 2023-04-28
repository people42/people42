package com.cider.fourtytwo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.Dimension
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PackageManagerCompat.LOG_TAG
import com.cider.fourtytwo.databinding.ActivitySigninBinding
import com.cider.fourtytwo.network.Api
import com.cider.fourtytwo.network.Model.NicknameResponse
import com.cider.fourtytwo.network.Model.UserResponse
import com.cider.fourtytwo.network.RetrofitInstance
import com.cider.fourtytwo.network.repository.NetworkRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.*
import okhttp3.FormBody
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException


class SigninActivity : AppCompatActivity() {
    private var _binding : ActivitySigninBinding? = null
    private val binding get() = _binding!!

    private lateinit var GoogleSignResultLauncher: ActivityResultLauncher<Intent>
    val api = RetrofitInstance.getInstance().create(Api::class.java)

    private val viewModel : IntroViewModel by viewModels()
    private val networkRepository = NetworkRepository()


//    override fun onStart() {
        //viewModel.checkFirstFlag()
//    //앱이 시작되면 이 기기 또는 다른 기기에서 silentSignIn를 호출하여 사용자가 Google을 통해 이미 앱에 로그인했는지 확인합니다.
//    GoogleSignIn.silentSignIn()
//    .addOnCompleteListener(
//    this,
//    new OnCompleteListener<GoogleSignInAccount>() {
//        @Override
//        public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
//            handleSignInResult(task);
//        }
//    });

//        super.onStart()
//        val account = GoogleSignIn.getLastSignedInAccount(this)
//        if (account == null) {
//            Log.e("Google account", "로그인 안 되어있음")
//        } else {
//            Log.e("Google account", "로그인 완료된 상태")
//            // 메인으로 이동
////            val intent = Intent(this, MainActivity::class.java)
////            startActivity(intent)
//        }
//    }
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
        val mGoogleSignInClient = GoogleSignIn.getClient(this,gso)
        GoogleSignResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()){ result ->
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)
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
//            account?.let {
//            runBlocking {
//                val accessToken = googletokenAuth?.let {
//                    networkRepository.getAccessToken(it)
//                }
//            }
            // 회원이면 메인으로
            if(checkUser(googletokenAuth)){
                val intent = Intent(this, MainActivity::class.java)
//                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
            }else {
                // 회원이 아니면 회원가입으로
                val intent = Intent(this, SignupActivity::class.java)
                startActivity(intent)
            }

        } catch (e: ApiException){
            // 취소 시 로그인 창으로
            if (e.statusCode == GoogleSignInStatusCodes.CANCELED) {
                Log.e("Google account", "signInResult: canceled")
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
    fun checkUser(idToken:String) : Boolean{
        var params = HashMap<String, String>()
        var user = false
        params.put("o_auth_token", idToken)
        api.getUserGoogle(params).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                Log.d("log",response.toString())
                Log.d("log",response.body()?.data.toString())
                response.body()?.data?.let {
                    if(it.user_idx > 0) {
                        user = true
                    }
                }
            }
            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                // 실패
                Log.d("log",t.message.toString())
                Log.d("log","fail")
            }
        })
        return user
    }
}