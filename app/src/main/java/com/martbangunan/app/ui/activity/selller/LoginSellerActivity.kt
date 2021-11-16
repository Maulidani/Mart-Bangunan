package com.martbangunan.app.ui.activity.selller

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.martbangunan.app.R

class LoginSellerActivity : AppCompatActivity() {
    private val TAG = this.toString()

    private val btnLogin: MaterialButton by lazy { findViewById(R.id.btnLoginSeller) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_seller)
        supportActionBar?.hide()

        btnLogin.setOnClickListener {

        }

    }

}