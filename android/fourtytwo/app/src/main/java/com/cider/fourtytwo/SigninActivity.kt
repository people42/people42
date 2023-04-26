package com.cider.fourtytwo

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.IntentSender
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.cider.fourtytwo.databinding.ActivitySigninBinding
import com.cider.fourtytwo.databinding.FragmentEmojiBinding
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import okhttp3.OkHttpClient
import retrofit2.Retrofit

class SigninActivity : AppCompatActivity() {
    private var _binding : ActivitySigninBinding? = null
    private val binding get() = _binding!!

    private lateinit var GoogleSignResultLauncher: ActivityResultLauncher<Intent>
    private val viewModel : SelectViewModel by viewModels()

//    override fun onStart() {
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
            val email = account?.email.toString()
            val googletokenAuth = account?.idToken.toString()
            
            // 회원이면 메인으로
//            viewModel.getUser()
//            if(email == "member"){
//                val intent = Intent(this, MainActivity::class.java)
////                val intent = Intent(requireContext(), MainActivity::class.java)
//                startActivity(intent)
//            }else {
//                // 회원이 아니면 회원가입으로
//                val intent = Intent(this, SignupActivity::class.java)
//                startActivity(intent)
//            }
        } catch (e: ApiException){
            // 취소 시 로그인 창으로
//            if (e.statusCode == GoogleSignInStatusCodes.CANCELED) {
//                Log.e("Google account", "signInResult: canceled")
//                val intent = Intent(this, SigninActivity::class.java)
//                startActivity(intent)
//                Toast.makeText(applicationContext, "로그인이 필요한 서비스입니다.", Toast.LENGTH_SHORT).show()
//            // 외에는 아직 없음
//            } else {
//                Log.e("Google account", "signInResult:failed Code = " + e.statusCode)
//            }
        }
    }
//    fun checkUser() {
//        val retrofit = Retrofit.Builder()
//            .baseUrl("http://people42.com") // baseUrl 설정
//            .client(OkHttpClient()) // OkHttpClient 인스턴스 설정
//            .addConverterFactory(GsonConverterFactory.create()) // Converter 설정
//            .build()
//    }
}