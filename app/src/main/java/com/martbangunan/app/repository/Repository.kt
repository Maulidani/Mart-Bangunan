package com.martbangunan.app.repository

import com.martbangunan.app.network.ApiService


class Repository(private val apiService: ApiService) {

    fun postLogin(
        email: String,
        password: String
    ) =
        apiService.login(email, password)

//    fun postRegister(
//        name: String,
//        email: String,
//        password: String,
//        password_confirmation: String
//    ) =
//        apiService.register(name, email, password, password_confirmation)
//
//    fun postLogout(
//        token: String
//    ) =
//        apiService.logout(token)
//
//    fun getUser(
//        token: String
//    ) =
//        apiService.user(token)
}