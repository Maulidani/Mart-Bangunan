package com.martbangunan.app.ui.activity.selller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.martbangunan.app.R
import com.martbangunan.app.network.model.RegistrationSellerModel
import com.martbangunan.app.ui.activity.customer.MainActivity
import com.martbangunan.app.ui.viewmodel.ScreenState
import com.martbangunan.app.ui.viewmodel.customer.Enqueue
import com.martbangunan.app.ui.viewmodel.seller.EnqueueSeller

class RegistrationSeller2Activity : AppCompatActivity() {
    private val TAG = this.toString()

    private val btnRegistration: MaterialButton by lazy { findViewById(R.id.btnRegistrationSeller) }

    private val viewModel: EnqueueSeller.ApiRegistrationSeller by lazy {
        ViewModelProvider(this).get(EnqueueSeller.ApiRegistrationSeller::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_seller2)
        supportActionBar?.hide()

        btnRegistration.setOnClickListener {
            viewModel.registrationLiveData.observe(this, {
                processRegistration(it)
            })
        }

    }

    private fun processRegistration(state: ScreenState<RegistrationSellerModel>) {

        when (state) {
            is ScreenState.Loading -> {
                Log.e(TAG, "processRegistration: loading...")
            }
            is ScreenState.Success -> {
                Log.e(TAG, "processRegistration: Success!")
                if (state.data != null) {
                    // action when success
                    Toast.makeText(this, "sukses, ${state.data}", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainSellerActivity::class.java))
                }
            }
            is ScreenState.Error -> {
                Log.e(TAG, "processRegistration: Failed!")
            }
        }
    }
}