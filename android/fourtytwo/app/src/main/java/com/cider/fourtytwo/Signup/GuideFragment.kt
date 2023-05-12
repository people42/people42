package com.cider.fourtytwo.Signup

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.cider.fourtytwo.MainActivity
import com.cider.fourtytwo.R
import com.cider.fourtytwo.dataStore.UserDataStore
import com.cider.fourtytwo.databinding.FragmentGuideBinding
import com.cider.fourtytwo.network.Api
import com.cider.fourtytwo.network.Model.MessageResponse
import com.cider.fourtytwo.signIn.UserInfo
import com.cider.fourtytwo.signIn.UserResponse
import com.cider.fourtytwo.network.RetrofitInstance
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GuideFragment : Fragment() {
    private var _binding : FragmentGuideBinding? = null
    private val binding get() = _binding!!
    private lateinit var userDataStore: UserDataStore
    val api = RetrofitInstance.getInstance().create(Api::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        askNotificationPermission()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGuideBinding.inflate(inflater, container, false)

// 유저 데이터 가져오기
        userDataStore = UserDataStore(requireContext())
        val myEmojiView: ImageView = binding.guideEmoji
        var myEmoji = ""
        var myColor = ""
        lifecycleScope.launch {
            myEmoji = userDataStore.get_emoji.first()
            myColor = userDataStore.get_color.first()
        }
        // 내 이모지
        Glide.with(this).load("https://peoplemoji.s3.ap-northeast-2.amazonaws.com/emoji/animate/${myEmoji}.gif").into(myEmojiView)

        binding.thinkCloudButton.setOnClickListener {
            // 내 생각 전송
            lifecycleScope.launch {
                setMessage(userDataStore.get_access_token.first(), binding.guideText.text.toString())
                Log.d(TAG, "메세지 전송 1 : 토큰 ${userDataStore.get_access_token.first()}")
                Log.d(TAG, "메세지 전송 1: 메세지 ${binding.guideText.text.toString()}")
            }
            // 메인으로 이동
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }

        binding.guideText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // 내 생각 전송
                lifecycleScope.launch {
                    setMessage(userDataStore.get_access_token.first(), binding.guideText.text.toString())
                    Log.d(TAG, "메세지 전송 1 : 토큰 ${userDataStore.get_access_token.first()}")
                    Log.d(TAG, "메세지 전송 1: 메세지 ${binding.guideText.text.toString()}")
                }
                // 메인으로 이동
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                true
            } else {
                false
            }
        }
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.writeLater.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_guideFragment_to_MainActivity)
        }
    }
    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that your app will not show notifications.
            val snackBar = Snackbar.make((binding.snackbarLayout), "소중한 사이의 생각 알림을 받을 수 없어요", Snackbar.LENGTH_INDEFINITE)
            snackBar.setAction("확인") {}
            snackBar.setActionTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            snackBar.show()
        }
    }
    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
    // [END ask_post_notifications]

    fun setMessage(Header: String, myMessage: String){
        var params = HashMap<String, String>()
        params.put("message", myMessage)
        api.setMessage(Header, params).enqueue(object : Callback<MessageResponse> {
            override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                Log.d("메세지 전송 2 응답", response.toString())
                if (response.code() == 200) {
                    Log.i(TAG, "메세지 전송 2 200: 잘 보내졌다네")
                } else if (response.code() == 401){
                    Log.i(TAG, "메세지 전송 2 401: 토큰 만료")
                    getToken(myMessage)
                } else {
                    Log.i(TAG, "메세지 전송 기타: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                Log.d("메세지 전송 2 실패: ", t.message.toString())
            }
        })
    }
    fun getToken(myMessage: String){
        lifecycleScope.launch {
            val refreshToken = userDataStore.get_refresh_token.first()

            api.setAccessToken(refreshToken).enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    response.body()?.let {
                        if (it.status == 200) {
                            Log.i(TAG, "토큰 전송 200: 잘 보내졌다네")
                            response.body()?.data?.let { it1 -> saveUserInfo(it1, myMessage) }
                        } else {
                            Log.i(TAG, "토큰 전송 응답 코드: ${response.code()}")
                        }
                    }
                }
                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    // 실패
                    Log.d("토큰 전송 on failure: ", t.message.toString())
                }
            })
        }
    }
    fun saveUserInfo(payload : UserInfo, myMessage: String){
        lifecycleScope.launch {
            userDataStore.setUserData(payload)
            setMessage(userDataStore.get_access_token.first(), myMessage)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
