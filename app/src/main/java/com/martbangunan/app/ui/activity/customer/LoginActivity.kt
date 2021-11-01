package com.martbangunan.app.ui.activity.customer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.martbangunan.app.R
import com.martbangunan.app.network.model.LoginModel
import com.martbangunan.app.ui.viewmodel.Enqueue
import com.martbangunan.app.ui.viewmodel.ScreenState
import kotlin.properties.Delegates

class LoginActivity : AppCompatActivity() {
    private val TAG = this.toString()

    private val btnLogin: MaterialButton by lazy { findViewById(R.id.btnLogin) }

    private val viewModel: Enqueue.ApiLogin by lazy {
        ViewModelProvider(this).get(Enqueue.ApiLogin::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        btnLogin.setOnClickListener {
            viewModel.loginLiveData.observe(this, {
                processLogin(it)
            })
        }

    }

    private fun processLogin(state: ScreenState<LoginModel>) {

        when (state) {
            is ScreenState.Loading -> {
                Log.e(TAG, "processLogin: loading...")
            }
            is ScreenState.Success -> {
                Log.e(TAG, "processLogin: Success!")
                if (state.data != null) {
                    // action when success
                    Toast.makeText(this, "sukses, ${state.data}", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, RegistrationActivity::class.java))
                }
            }
            is ScreenState.Error -> {
                Log.e(TAG, "processLogin: Failed!")
            }
        }
    }
}