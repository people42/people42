package com.cider.fourtytwo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PackageManagerCompat.LOG_TAG
import com.cider.fourtytwo.databinding.ActivitySigninBinding
import com.cider.fourtytwo.network.Api
import com.cider.fourtytwo.network.RetrofitInstance
import com.cider.fourtytwo.network.repository.NetworkRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
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
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException


class SigninActivity : AppCompatActivity() {
    private var _binding : ActivitySigninBinding? = null
    private val binding get() = _binding!!

    private lateinit var GoogleSignResultLauncher: ActivityResultLauncher<Intent>
    private val viewModel : IntroViewModel by viewModels()
    private val networkRepository = NetworkRepository()
//    private val client = RetrofitInstance.getInstance().create(Api::class.java)

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
    //==================

    //==================
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
            val googleAuth = account?.serverAuthCode.toString()

            Log.e("Login Google Account", email)
            Log.e("Login Google Account", googletokenAuth)
            Log.e("Login Google Account", googleAuth)


            // Get AccessToken
            account?.let {
            runBlocking {
                val accessToken = googletokenAuth?.let {
                    networkRepository.getAccessToken(it)
                }
                Log.e("Login Google Account", "AccessToken: $accessToken")
            }
//                val client = OkHttpClient()
//                val requestBody: RequestBody = FormBody.Builder()
//                    .add("client_id", "YOUR_CLIENT_ID_HERE")
//                    .add("client_secret", "YOUR_CLIENT_SECRET_HERE")
//                    .add("grant_type", "refresh_token")
//                    .add("refresh_token", it.serverAuthCode)
//                    .build()
//                val request: Request = Request.Builder()
//                    .url("https://oauth2.googleapis.com/token")
//                    .post(requestBody)
//                    .build()
//                val response = client.newCall(request).execute()
//                val responseData = response.body()?.string()
//                val jsonObject = JSONObject(responseData)
//                val accessToken = jsonObject.getString("access_token")
//                Log.e("Login Google Account", "AccessToken: $accessToken")
            }

//            val client = OkHttpClient()
//            val requestBody: RequestBody = FormEncodingBuilder()
//                .add("grant_type", "authorization_code")
//                .add(
//                    "client_id",
//                    "812741506391-h38jh0j4fv0ce1krdkiq0hfvt6n5amrf.apps.googleusercontent.com"
//                )
//                .add("client_secret", "{clientSecret}")
//                .add("redirect_uri", "")
//                .add("code", "4/4-GMMhmHCXhWEzkobqIHGG_EnNYYsAkukHspeYUk9E8")
//                .build()
//            val request: Request = Builder()
//                .url("https://www.googleapis.com/oauth2/v4/token")
//                .post(requestBody)
//                .build()
//            client.newCall(request).enqueue(object : Callback<Any?> {
//                override fun onFailure(request: Request?, e: IOException) {
//                    Log.e("LOG_TAG", e.toString())
//                }
//
//                @Throws(IOException::class)
//                fun onResponse(response: Response<*>) {
//                    try {
//                        val jsonObject = JSONObject(response.body().string())
//                        val message: String = jsonObject.toString(5)
//                        Log.i("LOG_TAG", message)
//                    } catch (e: JSONException) {
//                        e.printStackTrace()
//                    }
//                }
//            })

//            var data = HashMap<String, String>()
//            data.put("o_auth_token", googletokenAuth)
//            client.getUserGoogle(data).enqueue(object : Callback<UserInfo> {
//                override fun onResponse(call: Call<UserInfo>, response: Response<UserInfo>) {
//                    Log.d("log",response.toString())
//                    Log.d("log", response.body().toString())
//                    if(!response.body().toString().isEmpty())
//                        Log.d("log","hihihihihihihiihihiihi")
//                }
//
//                override fun onFailure(call: Call<PostResult>, t: Throwable) {
//                    // 실패
//                    Log.d("log",t.message.toString())
//                    Log.d("log","fail")
//                }
//            })

//            val httpClient: HttpClient = DefaultHttpClient()
//            val httpPost = HttpPost("https://yourbackend.example.com/tokensignin")
//
//            try {
//                val nameValuePairs: MutableList<NameValuePair> = ArrayList(1)
//                nameValuePairs.add(BasicNameValuePair("idToken", idToken))
//                httpPost.setEntity(UrlEncodedFormEntity(nameValuePairs))
//
//                val response = httpClient.execute(httpPost)
//                val statusCode = response.getStatusLine().getStatusCode()
//                val responseBody = EntityUtils.toString(response.getEntity())

//                Log.i(TAG, "Signed in as: $responseBody")
//            } catch (e: ClientProtocolException) {
//                Log.e(TAG, "Error sending ID token to backend.", e)
//            } catch (e: IOException) {
//                Log.e(TAG, "Error sending ID token to backend.", e)
//            }

            // 회원이면 메인으로
//            if(getUser(googletokenAuth)){
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
//            }else {
                Log.e("Google account", "signInResult:failed Code = " + e.statusCode)
//            }
        } catch (e: JSONException) {
            Log.e("Google account", "Error getting AccessToken from response", e)
        } catch (e: IOException) {
            Log.e("Google account", "Error getting AccessToken from response", e)
        }
    }
//    fun getUser(value : String): Boolean {
//        val params = hashMapOf("o_auth_token" to value)
//        val user = networkRepository.getUser(params)
//        Log.e("Login Google Account", user.toString())
//        Log.e("Login Google Account", user.message)
//        if (user.user_idx != null){

//            유저 정보 저장하고

//            return true
//        }
//        return false
//    }
//    fun checkUser() {
//        val retrofit = Retrofit.Builder()
//            .baseUrl("http://people42.com") // baseUrl 설정
//            .client(OkHttpClient()) // OkHttpClient 인스턴스 설정
//            .addConverterFactory(GsonConverterFactory.create()) // Converter 설정
//            .build()
//    }
}