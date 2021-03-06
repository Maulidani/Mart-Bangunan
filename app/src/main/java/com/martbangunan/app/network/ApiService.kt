package com.martbangunan.app.network

import com.martbangunan.app.network.model.*
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

    @POST("seller")
    fun seller(
        @Header("Authorization") token: String
    ): Call<SellerModel>

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

    @FormUrlEncoded
    @POST("edit")
    fun edit(
        @Header("Authorization") token: String,
        @Field("email") email: String,
        @Field("address_name") address: String,
        @Field("province") province: String,
        @Field("city") city: String,
        @Field("districts") districts: String,
        @Field("name") name: String,
        @Field("phone") phone: Int,
        @Field("type") type: String,
        @Field("user_id") userId: Int,
        @Field("address_id") addressId: Int,
    ): Call<RegisterModel>

    @Multipart
    @POST("edit-image")
    fun editImage(
        @Header("Authorization") token: String,
        @Part parts: MultipartBody.Part,
        @Part("user_id") id: RequestBody,
    ): Call<RegisterModel>

    @FormUrlEncoded
    @POST("all-product")
    fun allProduct(
        @Header("Authorization") token: String,
        @Field("category") category: String,
        @Field("search") search: String,
        @Field("seller") seller: String,
    ): Call<ProductModel>

    @FormUrlEncoded
    @POST("seller-product")
    fun sellerProduct(
        @Header("Authorization") token: String,
        @Field("seller_id") id: String,
        @Field("search") search: String,
    ): Call<ProductModel>

    @FormUrlEncoded
    @POST("image-product")
    fun imageProduct(
        @Header("Authorization") token: String,
        @Field("id") id: String,
    ): Call<SliderItem>

    @Multipart
    @POST("upload-product")
    fun uploadProduct(
        @Header("Authorization") token: String,
        @Part("name") name: RequestBody,
        @Part("category") category: RequestBody,
        @Part("quantity") quantity: RequestBody,
        @Part("price") price: RequestBody,
        @Part parts: ArrayList<MultipartBody.Part>,
        @Part("description") description: RequestBody,
    ): Call<ProductModel>

    @FormUrlEncoded
    @POST("delete-product")
    fun deleteProduct(
        @Header("Authorization") token: String,
        @Field("id") name: Int,
    ): Call<ProductModel>

    @FormUrlEncoded
    @POST("delete-image-product")
    fun deleteImageProduct(
        @Header("Authorization") token: String,
        @Field("id") id: Int,
    ): Call<ProductModel>

    @FormUrlEncoded
    @POST("edit-product")
    fun editProduct(
        @Header("Authorization") token: String,
        @Field("name") name: String,
        @Field("category") category: Int,
        @Field("quantity") quantity: Int,
        @Field("price") price: Int,
        @Field("id") id: Int,
        @Field("description") description: String,
    ): Call<ProductModel>

    @Multipart
    @POST("add-image-product")
    fun addImageProduct(
        @Header("Authorization") token: String,
        @Part("product_id") id: RequestBody,
        @Part parts: ArrayList<MultipartBody.Part>,
    ): Call<ProductModel>

    @FormUrlEncoded
    @POST("add-cart")
    fun addCart(
        @Header("Authorization") token: String,
        @Field("product_id") id: Int,
    ): Call<ProductModel>

    @POST("my-cart")
    fun myCart(
        @Header("Authorization") token: String,
    ): Call<ProductModel>

    @FormUrlEncoded
    @POST("delete-cart")
    fun deleteCart(
        @Header("Authorization") token: String,
        @Field("id") id: Int,
    ): Call<ProductModel>

//    @FormUrlEncoded
//    @POST("add-order")
//    fun addOrder(
//        @Header("Authorization") token: String,
//        @Field("shipping") shipping: Int,
//        @Field("note") note: String,
//        @Field("order_id_midtrans") orderIdMidtrans: String,
//        @Field("total") total: Int,
//        @Field("product_id") productId: Array<Int>,
//    ): Call<ProductModel>

    @POST("list-chat")
    fun listChat(
        @Header("Authorization") token: String,
    ): Call<ChatModel>

    @FormUrlEncoded
    @POST("chat")
    fun getChat(
        @Header("Authorization") token: String,
        @Field("to_user_id") id: Int,
    ): Call<ChatModel>

    @FormUrlEncoded
    @POST("add-chat")
    fun sendChat(
        @Header("Authorization") token: String,
        @Field("to_user_id") id: Int,
        @Field("message") msg: String,
    ): Call<ChatModel>

    @GET("banner")
    fun banner(): Call<BannerModel>

    // api location
    @GET("provinsi")
    fun getProvince(): Call<LocationModel>

    @GET("kota")
    fun getCity(
        @Query("id_provinsi") id: String
    ): Call<LocationModel>

    @GET("kecamatan")
    fun getDistrict(
        @Query("id_kota") id: String
    ): Call<LocationModel>

}