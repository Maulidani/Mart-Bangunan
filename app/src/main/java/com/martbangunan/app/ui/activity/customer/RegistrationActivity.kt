package com.martbangunan.app.ui.activity.customer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.martbangunan.app.R

class RegistrationActivity : AppCompatActivity() {
    private val TAG = this.toString()

    private val btnRegistration: MaterialButton by lazy { findViewById(R.id.btnRegistration) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        supportActionBar?.hide()

        btnRegistration.setOnClickListener {

        }

    }
}