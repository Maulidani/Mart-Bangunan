package com.martbangunan.app.ui.activity.customer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.martbangunan.app.R

class LoginActivity : AppCompatActivity() {
    private val TAG = this.toString()

    private val btnLogin: MaterialButton by lazy { findViewById(R.id.btnLogin) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        btnLogin.setOnClickListener {
        }

    }
}
