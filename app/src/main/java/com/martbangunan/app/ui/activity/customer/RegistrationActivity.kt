package com.martbangunan.app.ui.activity.customer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.martbangunan.app.R
import com.martbangunan.app.network.model.RegistrationModel
import com.martbangunan.app.ui.viewmodel.Enqueue
import com.martbangunan.app.ui.viewmodel.ScreenState

class RegistrationActivity : AppCompatActivity() {
    private val TAG = this.toString()

    private val btnRegistration: MaterialButton by lazy { findViewById(R.id.btnRegistration) }

    private val viewModel: Enqueue.ApiRegistration by lazy {
        ViewModelProvider(this).get(Enqueue.ApiRegistration::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        supportActionBar?.hide()

        btnRegistration.setOnClickListener {
            viewModel.registrationLiveData.observe(this, {
                processRegistration(it)
            })
        }

    }

    private fun processRegistration(state: ScreenState<RegistrationModel>) {

        when (state) {
            is ScreenState.Loading -> {
                Log.e(TAG, "processRegistration: loading...")
            }
            is ScreenState.Success -> {
                Log.e(TAG, "processRegistration: Success!")
                if (state.data != null) {
                    // action when success
                    Toast.makeText(this, "sukses, ${state.data}", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }
            is ScreenState.Error -> {
                Log.e(TAG, "processRegistration: Failed!")
            }
        }
    }
}