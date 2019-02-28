package com.taras_bekhta.doittest.retrofit

import com.taras_bekhta.doittest.BuildConfig
import retrofit2.Call
import retrofit2.http.*

interface ITaskService {
    @GET("${BuildConfig.API_URL}tasks")
    fun getTasks(): Call<TasksResponse>

    @GET("${BuildConfig.API_URL}tasks/{task}")
    fun getTask(@Path("task") taskId: Int): Call<AddTaskResponse>

    @DELETE("${BuildConfig.API_URL}tasks/{task}")
    fun deleteTask(@Path("task") taskId: Int): Call<Void>

    @PUT("${BuildConfig.API_URL}tasks/{task}")
    @FormUrlEncoded
    fun updateTask(
        @Path("task") taskId: Int,
        @Field("title") title: String,
        @Field("dueBy") dueBy: Int,
        @Field("priority") priority: String
    ): Call<Void>

    @POST("${BuildConfig.API_URL}tasks")
    @FormUrlEncoded
    fun addTask(
        @Field("title") title: String,
        @Field("dueBy") dueBy: Int,
        @Field("priority") priority: String
    ): Call<AddTaskResponse>

}