package com.martbangunan.app.ui.viewmodel.seller

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.martbangunan.app.network.ApiClient
import com.martbangunan.app.network.model.LoginModel
import com.martbangunan.app.network.model.RegistrationModel
import com.martbangunan.app.network.model.RegistrationSellerModel
import com.martbangunan.app.repository.Repository
import com.martbangunan.app.ui.viewmodel.ScreenState

class EnqueueSeller {

    class ApiLoginSeller(private val repository: Repository = Repository(ApiClient.instances)) :
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

    class ApiRegistrationSeller(private val repository: Repository = Repository(ApiClient.instances)) :
        ViewModel() {

        private var getRegistrationLiveData = MutableLiveData<ScreenState<RegistrationSellerModel>>()

        val registrationLiveData: LiveData<ScreenState<RegistrationSellerModel>>
            get() = getRegistrationLiveData

        init {
            registration()
        }

        private fun registration() {
            getRegistrationLiveData.postValue(ScreenState.Success(RegistrationSellerModel("nama lengkap")))

//            getLoginLiveData.postValue(ScreenState.Loading(null))

            //enqueue
            //getUserLiveData.postValue(ScreenState.Success(response.body())) // if success
        }
    }

}