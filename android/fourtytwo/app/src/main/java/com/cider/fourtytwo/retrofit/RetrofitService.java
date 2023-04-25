package com.cider.fourtytwo.retrofit;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface RetrofitService {
    // 회원가입 체크 (구글)
    @POST("/api/v1/auth/check/google")
    void getUser(@Body String o_auth_token);

//    // 회원가입 (구글)
//    @POST("/api/v1/auth/signup/google")
//    // 닉네임 생성
//    @GET("/api/v1/auth/nickname")
//    // access 토큰 갱신
//    @POST("/api/v1/auth/token")

// 일자별 메모
//@POST("maryfarm-calendar-service/api/calendar/day/search")
//Call<List<MemoModel>> getPlant(@Body CalendarPickModel CalendarPick);
//    @Multipart
//    @PUT("maryfarm-diary-service/api/diary/modify")
//    Call<DiaryModifyModel> setDiary(@Part DiaryModifyModel DiaryModify);
//
//    // 위젯 리스트 정보 받아오기
//    @GET("maryfarm-plant-service/api/plant/month/today/{userId}")
//    Call<List<ItemModel>> getWidget(@Path("userId") String userId);

}
