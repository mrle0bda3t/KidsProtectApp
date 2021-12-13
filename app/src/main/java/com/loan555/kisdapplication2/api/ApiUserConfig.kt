package com.loan555.kisdapplication2.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiUserConfig {
    private const val BaseUrl = "http://34.134.234.78/"
    private val okHttpClient = OkHttpClient.Builder()
        .callTimeout(2, TimeUnit.MINUTES)
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)

    private val builder = Retrofit.Builder()
        .baseUrl(BaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient.build())
    val retrofit = builder.build()!!
}