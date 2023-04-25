package com.cider.fourtytwo.retrofit

import com.google.gson.GsonBuilder
import retrofit2.Converter
import retrofit2.Retrofit

class RetrofitFactory {
    private val BASE_URL = "http://people42.com"

    fun create(): RetrofitService? {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(RetrofitService::class.java)
    }
}