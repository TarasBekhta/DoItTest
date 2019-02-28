package com.taras_bekhta.doittest.retrofit

import com.taras_bekhta.doittest.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitClient {
    val HTTP_CONNECT_TiMEOUT = 30L
    val HTTP_READ_TIMEOUT = 30L
    val HTTP_WRITE_TIMEOUT = 60L

    private val retrofit: Retrofit
    private var authToken: String = ""

    constructor() {
        retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .client(getHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    constructor(token: String) {
        authToken = token
        retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .client(getHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getRetrofit(): Retrofit {
        return retrofit
    }

    private fun getHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val builder = OkHttpClient().newBuilder()
        builder.addInterceptor(logging)
        builder.connectTimeout(HTTP_CONNECT_TiMEOUT, TimeUnit.SECONDS)
            .readTimeout(HTTP_READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(HTTP_WRITE_TIMEOUT, TimeUnit.SECONDS)
        val interceptor = getInterceptor()
        if (interceptor != null) {
            builder.addInterceptor(interceptor)
        }
        return builder.build()
    }

    private fun getInterceptor(): Interceptor? {
        var interceptor: Interceptor? = null
        interceptor = Interceptor { chain ->
            val request = chain.request()
                .newBuilder()
                .addHeader(BuildConfig.DEFAULT_AUTHORIZATION_HEADER, getAccessToken())
                .build()
            chain.proceed(request)
        }
        return interceptor
    }

    private fun getAccessToken(): String {
        return BuildConfig.DEFAULT_AUTHORIZATION_TYPE + " $authToken"
    }
}