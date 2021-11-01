package com.martbangunan.app.ui.viewmodel.customer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.martbangunan.app.network.ApiClient
import com.martbangunan.app.network.model.LoginModel
import com.martbangunan.app.network.model.RegistrationModel
import com.martbangunan.app.repository.Repository
import com.martbangunan.app.ui.viewmodel.ScreenState

class Enqueue {

    class ApiLogin(private val repository: Repository = Repository(ApiClient.instances)) :
        ViewModel() {

        private var getLoginLiveData = MutableLiveData<ScreenState<LoginModel>>()

        val loginLiveData: LiveData<ScreenState<LoginModel>>
            get() = getLoginLiveData

        init {
            login()
        }

        private fun login() {
            getLoginLiveData.postValue(ScreenState.Success(LoginModel("token")))

//            getLoginLiveData.postValue(ScreenState.Loading(null))

            //enqueue
            //getUserLiveData.postValue(ScreenState.Success(response.body())) // if success
        }
    }

    class ApiRegistration(private val repository: Repository = Repository(ApiClient.instances)) :
        ViewModel() {

        private var getRegistrationLiveData = MutableLiveData<ScreenState<RegistrationModel>>()

        val registrationLiveData: LiveData<ScreenState<RegistrationModel>>
            get() = getRegistrationLiveData

        init {
            registration()
        }

        private fun registration() {
            getRegistrationLiveData.postValue(ScreenState.Success(RegistrationModel("nama lengkap")))

//            getLoginLiveData.postValue(ScreenState.Loading(null))

            //enqueue
            //getUserLiveData.postValue(ScreenState.Success(response.body())) // if success
        }
    }

}