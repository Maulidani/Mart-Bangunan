package com.martbangunan.app.ui.activity.selller

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.martbangunan.app.R

class RegistrationSeller2Activity : AppCompatActivity() {
    private val TAG = this.toString()

    private val btnRegistration: MaterialButton by lazy { findViewById(R.id.btnRegistrationSeller) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_seller2)
        supportActionBar?.hide()

        btnRegistration.setOnClickListener {

        }

    }

}