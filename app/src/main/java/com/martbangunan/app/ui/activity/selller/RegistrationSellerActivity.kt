package com.martbangunan.app.ui.activity.selller

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.martbangunan.app.R

class RegistrationSellerActivity : AppCompatActivity() {
    private val btnRegistration: MaterialButton by lazy { findViewById(R.id.btnNext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_seller)
        supportActionBar?.hide()

        btnRegistration.setOnClickListener {
            startActivity(Intent(this, RegistrationSeller2Activity::class.java))
        }
    }
}