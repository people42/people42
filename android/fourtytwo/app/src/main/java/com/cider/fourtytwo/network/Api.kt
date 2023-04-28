package com.cider.fourtytwo.network

import com.cider.fourtytwo.network.Model.NicknameResponse
import com.cider.fourtytwo.network.Model.SignupForm
import com.cider.fourtytwo.network.Model.UserInfo
import com.cider.fourtytwo.network.Model.UserResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

interface Api {
    // 회원 체크 (구글)
    @POST("api/v1/auth/check/android/google")
    fun getUserGoogle(@Body params: HashMap<String, String>) : Call<UserResponse>
    // 회원가입 (구글)
    @POST("api/v1/auth/signup/google")
    suspend fun signUpGoogle(@Body params: SignupForm) : UserInfo
    // 랜덤 닉네임 생성
    @GET("api/v1/auth/nickname")
    fun getNickname() : Call<NicknameResponse>

    // 회원 탈퇴
    @PUT("api/v1/account/withdrawal")
    suspend fun withdrawal()
    // access 토큰 갱신
//    @POST("api/v1/auth/token")
}