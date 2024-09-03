package com.example.mysubmission3.data.api.retrofit

import com.example.mysubmission3.data.api.response.DetailStoryResponse
import com.example.mysubmission3.data.api.response.GetAllStoriesResponse
import com.example.mysubmission3.data.api.response.LoginResponse
import com.example.mysubmission3.data.api.response.RegisterResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("stories")
    suspend fun getAllStories(): GetAllStoriesResponse

    @GET("stories/{id}")
    suspend fun detailStory(
        @Path("id") id: String
    ): DetailStoryResponse

}