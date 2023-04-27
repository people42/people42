package com.cider.fourtytwo.network

import com.cider.fourtytwo.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

object RetrofitHeaderInstance {
    private const val BASE_URL = "https://people42.com/be42"
    private const val HEADER = "eyJhbGciOiJSUzI1NiIsImtpZCI6Ijg2OTY5YWVjMzdhNzc4MGYxODgwNzg3NzU5M2JiYmY4Y2Y1ZGU1Y2UiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiI5MDMyMzE3NjMyNDgtczc5c3QzdHFlbGVvZmFxbHFuYWU0a2o2aWs1ODR1ZjYuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdWQiOiI5MDMyMzE3NjMyNDgtM3Y4NmwzaGxrZ2cyNGc2OGo1cnFrYWQ2bzZ1OGZtNHQuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMDkxNDM1MDMwMDcxNDc4Mzc1MjUiLCJlbWFpbCI6Im1vbGlodWEzMjEuZHBAZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsIm5hbWUiOiLquYDsp4TtnawiLCJwaWN0dXJlIjoiaHR0cHM6Ly9saDMuZ29vZ2xldXNlcmNvbnRlbnQuY29tL2EvQUdObXl4WVRPd1hVYmxKMlhwZWpIc0E2RjhOZXQxc2tmV3hORWZXal9NeW09czk2LWMiLCJnaXZlbl9uYW1lIjoi7KeE7Z2sIiwiZmFtaWx5X25hbWUiOiLquYAiLCJsb2NhbGUiOiJrbyIsImlhdCI6MTY4MjU1MzQzMCwiZXhwIjoxNjgyNTU3MDMwfQ.OGA6g1yBRxdkuxJjuY0PgnximhbXDVg8tSEM0Fe63rXF530WJQKbZKYtdh6wLx3BeK8wH3QlUc5BZX1lHFDUcRRALrjBRvFQb5glLz0HfsM5zaAAy-k4r33L8RxufSCrNZaC9CYRO6tiTCSoDP7INKcJ7jmS6WNVgoDV6DLskiABYG66stRwBN8_Hs44MpFK-VXNiV181b4brDhrRkiuGNMJTUHDbbtSw6zh3eCh3hz35qIGM0aOlrmbjcWh0s2CTDB18pYSOMqViuu6hG2hkD9pOKwBoIhck4QcwoMhsaCzs_kubGIXr_5XJvecPqeDpOxS8HDd1AyZdcbndSzo-A"

    private val client = Retrofit
        .Builder()
        .baseUrl(BASE_URL)
        .client(provideOkHttpClient(AppInterceptor()))
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getInstance(): Retrofit {
        return client
    }

    private fun provideOkHttpClient(interceptor: AppInterceptor): OkHttpClient =
        OkHttpClient.Builder().run {
            addInterceptor(interceptor)
            build()
        }

    class AppInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response = with(chain) {
            val newRequest = request().newBuilder()
                .addHeader("ACCESS-TOKEN", HEADER)
                .build()
            proceed(newRequest)
        }
    }
}