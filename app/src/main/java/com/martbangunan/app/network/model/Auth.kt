package com.martbangunan.app.network.model

data class LoginModel(
    val api_token: String,
    val id: String,
    val errors: Boolean,
    val message: String,
)

data class RegisterModel(
    val message: String,
)

data class UserModel(
    val message: String,
    val errors: Boolean,
    val user: UserInfoModel
)

data class SellerModel(
    val message: String,
    val errors: Boolean,
    val user: List<UserInfoModel>
)

data class LogoutModel(
    val message: String,
)

data class UserInfoModel(
    val id: Int,
    val user_id: Int,
    val user_account_id: Int,
    val user_name: String,
    val phone: String,
    val address_id: Int,
    val address: String,
    val image: String,
    val country: String,
    val province: String,
    val districts: String,
    val city: String,
    val zip_code: String,
    val email: String,
    val email_verified_at: String,
    val type: String,
)

data class RegistrationSellerModel(
    val fullName: String
)