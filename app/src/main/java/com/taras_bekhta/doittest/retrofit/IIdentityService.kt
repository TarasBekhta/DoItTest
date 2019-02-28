package com.taras_bekhta.doittest.retrofit

import com.taras_bekhta.doittest.BuildConfig
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface IIdentityService {
    @POST("${BuildConfig.API_URL}users")
    @FormUrlEncoded
    fun registerUser(@Field("email") userName: String,
                  @Field("password") password: String): Call<AuthToken>

    @POST("${BuildConfig.API_URL}auth")
    @FormUrlEncoded
    fun loginUser(@Field("email") userName: String,
                     @Field("password") password: String): Call<AuthToken>
}