package com.cider.fourtytwo.network

import com.cider.fourtytwo.Signup.NicknameResponse
import com.cider.fourtytwo.Signup.SignupForm
import com.cider.fourtytwo.feed.RecentFeedResponse
import com.cider.fourtytwo.map.SetLocationResponse
import com.cider.fourtytwo.myHistory.HistoryResponse
import com.cider.fourtytwo.network.Model.*
import com.cider.fourtytwo.person.PersonPlaceResponse
import com.cider.fourtytwo.person.PersonResponse
import com.cider.fourtytwo.place.PlaceResponse
import com.cider.fourtytwo.signIn.UserResponse
import retrofit2.Call
import retrofit2.http.*
import retrofit2.http.Header

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
    // 로그아웃
    @DELETE("api/v1/account/logout")
    fun signOut(@Header("ACCESS-TOKEN") accessToken: String) : Call<SignOutResponse>
    // 내 메세지 조회
    @GET("api/v1/account/myinfo")
    fun getNowMessage(@Header("ACCESS-TOKEN") accessToken: String) : Call<NowMessageResponse>
    // 내 상메 히스토리 조회
    @GET("api/v1/account/history")
    fun getHistory(@Header("ACCESS-TOKEN") accessToken: String, @Query("date") date : String) : Call<HistoryResponse>
    // 위치 갱신 & 스침 생성
    @POST("api/v1/background")
    fun setLocation(@Header("ACCESS-TOKEN") accessToken: String, @Body params: HashMap<String, Double>?) : Call<SetLocationResponse>
    // 최근 피드 조회
    @GET("api/v1/feed/recent")
    fun getRecentFeed(@Header("ACCESS-TOKEN") accessToken: String) : Call<RecentFeedResponse>
    // 회원 탈퇴
    @DELETE("api/v1/account/withdrawal")
    fun withdrawal(@Header("ACCESS-TOKEN") accessToken: String) : Call<SignOutResponse>
    // 장소 조회
    @GET("api/v1/feed/place")
    fun getPlaceFeed(@Header("ACCESS-TOKEN") accessToken: String,
                     @Query("placeIdx") placeIdx : Int,
                     @Query("time") time : String,
                     @Query("page") page : Int,
                     @Query("size") size : Int,
    ) : Call<PlaceResponse>
    // 히스토리 삭제
    @PUT("api/v1/account/message")
    fun deleteMessage(@Header("ACCESS-TOKEN") accessToken: String, @Body params: HashMap<String, Int>) : Call<MessageResponse>
    //신고
    @POST("api/v1/account/report")
    fun setReport(@Header("ACCESS-TOKEN") accessToken: String, @Body params: HashMap<String, Any>) : Call<MessageResponse>
    // 사람 조회
    @GET("api/v1/feed/user")
    fun getPersonFeed(@Header("ACCESS-TOKEN") accessToken: String,
                      @Query("userIdx") userIdx : Int
    ) : Call<PersonResponse>
// 사람 장소 피드 조회
    @GET("api/v1/feed/user/place")
    fun getPersonPlaceFeed(@Header("ACCESS-TOKEN") accessToken: String,
                      @Query("userIdx") userIdx : Int,
                      @Query("placeIdx") placeIdx : Int
    ) : Call<PersonPlaceResponse>

    // FCM 토큰 갱신
    @POST("api/v1/account/fcm_token")
    fun setFcmToken(@Header("ACCESS-TOKEN") accessToken: String, @Body params: HashMap<String, String>) : Call<MessageResponse>


    //차단
    @POST("api/v1/account/block")
    fun setBlock(@Header("ACCESS-TOKEN") accessToken: String, @Body params: HashMap<String, Int>) : Call<MessageResponse>

}