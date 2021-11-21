package com.martbangunan.app.network

import com.martbangunan.app.network.model.LoginModel
import com.martbangunan.app.network.model.LogoutModel
import com.martbangunan.app.network.model.RegisterModel
import com.martbangunan.app.network.model.UserModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("type") type: String,
    ): Call<LoginModel>

    @GET("user")
    fun user(
        @Header("Authorization") token: String
    ): Call<UserModel>

    @POST("logout")
    fun logout(
        @Header("Authorization") token: String
    ): Call<LogoutModel>

    @Multipart
    @POST("register")
    fun registration(
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part("address_name") address: RequestBody,
        @Part("province") province: RequestBody,
        @Part("city") city: RequestBody,
        @Part("districts") districts: RequestBody,
        @Part("name") name: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part parts: MultipartBody.Part,
        @Part("type") type: RequestBody,
    ): Call<RegisterModel>

}