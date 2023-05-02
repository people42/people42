package com.cider.fourtytwo.network

import com.cider.fourtytwo.network.Model.*
import kotlinx.coroutines.Job
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.PUT

interface Api {
    // 회원 체크 (구글)
    @POST("api/v1/auth/check/android/google")
    fun getGoogleUser(@Body params: HashMap<String, String>) : Call<UserResponse>
    // 회원가입 (구글)
    @POST("api/v1/auth/signup/android/google")
    fun signUpGoogle(@Body params: SignupForm) : Call<UserResponse>
    // 랜덤 닉네임 생성
    @GET("api/v1/auth/nickname")
    fun getNickname() : Call<NicknameResponse>
    // 상태메세지 등록
    @POST("api/v1/account/message")
    fun setMessage(@Header("ACCESS-TOKEN") accessToken: String, @Body params: HashMap<String, String>) : Call<MessageResponse>
    //access 토큰 갱신
    @POST("api/v1/auth/token")
    fun setAccessToken(@Header("REFRESH-TOKEN") refreshToken: String) : Call<UserResponse>

    // 최근 피드 조회
    @GET
    ("api/v1/feed/recent")
    fun getRecentFeed(@Header("ACCESS-TOKEN") accessToken: String) : Call<RecentFeedResponse>
    // 내 메세지 조회
    @GET
    ("api/v1/account/myinfo")
    fun getNowMessage(@Header("ACCESS-TOKEN") accessToken: String) : Call<NowMessageData>

    // 로그아웃
    @DELETE("api/v1/account/logout")
    fun signOut(@Header("ACCESS-TOKEN") accessToken: String) : Call<SignOutResponse>

    // 회원 탈퇴
    @DELETE("api/v1/account/withdrawal")
    fun withdrawal(@Header("ACCESS-TOKEN") accessToken: String) : Call<SignOutResponse>


}