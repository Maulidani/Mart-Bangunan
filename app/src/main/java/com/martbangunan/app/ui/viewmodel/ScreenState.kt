package com.martbangunan.app.ui.viewmodel

sealed class ScreenState<T>(val data: T? = null, val message: String? = null) {

    class Loading<T>(data: T? = null) : ScreenState<T>(data)

    class Success<T>(data: T?) : ScreenState<T>(data)

    class Error<T>(message: String, data: T? = null) : ScreenState<T>(data, message)
}