package com.cider.fourtytwo.Signup

import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.cider.fourtytwo.R
import com.cider.fourtytwo.UserViewModel
import com.cider.fourtytwo.dataStore.UserDataStore
import com.cider.fourtytwo.databinding.FragmentWelcomeBinding
import com.cider.fourtytwo.network.Api
import com.cider.fourtytwo.signIn.UserInfo
import com.cider.fourtytwo.signIn.UserResponse
import com.cider.fourtytwo.network.RetrofitInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WelcomeFragment : Fragment() {
    private var _binding : FragmentWelcomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel : UserViewModel by viewModels()
    val api = RetrofitInstance.getInstance().create(Api::class.java)
    private lateinit var userDataStore: UserDataStore
    var myEmail = ""
    var myIdToken = ""
    var myNickname = ""
    var myEmoji = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myNickname = arguments?.getString("myNickname").toString()
        myEmoji = arguments?.getString("myEmoji").toString()
        // 회원가입
        userDataStore = UserDataStore(requireContext())
        lifecycleScope.launch {
            signupGoogle(SignupForm(userDataStore.get_email.first(), myNickname, userDataStore.get_idToken.first(), myEmoji))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val handler = Handler()
        handler.postDelayed({
            Navigation.findNavController(view)
                .navigate(R.id.action_welcomeFragment_to_guideFragment)
        }, 1500)
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
    private fun signupGoogle(signupForm: SignupForm){
        api.signUpGoogle(signupForm).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                Log.d("SignupGoogle_onResponse", response.body()?.data.toString())
                if (response.code() == 200) {
                    response.body()?.data?.let {
                        saveUserInfo(it)
                    }
                } else if (response.code() == 401){
                    Log.i(TAG, "메세지 전송 401: 토큰 만료")
                    getToken(signupForm)
                } else {
                    Log.i(TAG, "메세지 전송 기타: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                // 실패
                Log.d("SignupGoogle_onFailure", t.message.toString())
                Log.d("SignupGoogle_onFailure", "fail")
            }
        })
    }
    fun getToken(signupForm: SignupForm) {
        lifecycleScope.launch {
            val refreshToken = userDataStore.get_refresh_token.first()
            api.setAccessToken(refreshToken).enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    if (response.body()?.status == 200) {
                        saveUserAccessToken(refreshToken, signupForm)
                    } else {
                        Log.i(TAG, "토큰 전송 실패 코드: ${response.code()}")
                    }
                }
                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    // 실패
                    Log.d("토큰 전송 on failure: ", t.message.toString())
                }
            })

        }
    }
    fun saveUserInfo(payload : UserInfo){
        lifecycleScope.launch {
            userDataStore.setUserData(payload)
        }
    }
    fun saveUserAccessToken(token : String, signupForm: SignupForm){
        lifecycleScope.launch {
            userDataStore.setUserAccessToken(token)
            signupGoogle(signupForm)
        }
    }
}