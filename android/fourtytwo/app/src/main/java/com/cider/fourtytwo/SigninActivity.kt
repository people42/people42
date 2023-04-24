package com.cider.fourtytwo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class SigninActivity : AppCompatActivity() {
    private lateinit var GoogleSignResultLauncher: ActivityResultLauncher<Intent>

    override fun onStart() {
        super.onStart()

        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account == null) {
            Log.e("Google account", "로그인 안 되어있음")
        } else {
            Log.e("Google account", "로그인 완료된 상태")
            // 메인으로 이동
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(androidx.appcompat.R.style.Theme_AppCompat_Light_NoActionBar) // 화면이 구성될 때, 스플래시 -> 노액션바 테마로 변경
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

// 구글 로그인
        //사용자의 ID, 이메일 주소 및 기본정보를 요청하도록 로그인 구성
        //프로파일링 합니다. ID 및 기본 프로파일은 DEFAULT_SIGN_IN에 포함됩니다.
        //추가로 요청해야하는 정보는 requestScopes를 지정하여 요청함. 꼭 필요한 것들만 요청하도록 한다.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(BuildConfig.google_debug)
//            .requestServerAuthCode(BuildConfig.google_debug)
            .requestEmail()
            .build()

        val mGoogleSignInClient = GoogleSignIn.getClient(this,gso)


        GoogleSignResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()){ result ->
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleSignInResult(task)
        }
        val googleLoginButton = findViewById<SignInButton>(R.id.sign_in_button)
        googleLoginButton.setOnClickListener {
            var signIntent: Intent = mGoogleSignInClient.getSignInIntent()
            GoogleSignResultLauncher.launch(signIntent)
        }
    }

    fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val email = account?.email.toString()
//            var googletoken = account?.idToken.toString()
//            var googletokenAuth = account?.serverAuthCode.toString()

            Log.e("Google account",email)
//            Log.e("Google account",googletoken)
//            Log.e("Google account", googletokenAuth)

            // 회원이면 메인으로
            if(email == "member"){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }else {
                // 회원이 아니면 회원가입으로
                val intent = Intent(this, SignupActivity::class.java)
                startActivity(intent)
            }
        } catch (e: ApiException){
            Log.e("Google account","signInResult:failed Code = " + e.statusCode)
        }
    }
}