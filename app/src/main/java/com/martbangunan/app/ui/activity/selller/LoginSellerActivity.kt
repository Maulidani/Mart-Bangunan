package com.martbangunan.app.ui.activity.selller

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.martbangunan.app.R
import com.martbangunan.app.ui.activity.customer.LoginActivity

class LoginSellerActivity : AppCompatActivity() {
    private val TAG = this.toString()

    private val btnLogin: MaterialButton by lazy { findViewById(R.id.btnLoginSeller) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_seller)
        supportActionBar?.hide()

        btnLogin.setOnClickListener {
            startActivity(Intent(this, MainSellerActivity::class.java))
        }

    }

}