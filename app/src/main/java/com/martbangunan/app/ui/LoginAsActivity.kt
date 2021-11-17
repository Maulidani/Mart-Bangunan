package com.martbangunan.app.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.button.MaterialButton
import com.martbangunan.app.R
import com.martbangunan.app.ui.activity.customer.LoginActivity
import com.martbangunan.app.ui.activity.customer.MainActivity
import com.martbangunan.app.ui.activity.selller.LoginSellerActivity
import com.martbangunan.app.ui.activity.selller.MainSellerActivity

class LoginAsActivity : AppCompatActivity() {
    private val TAG = this.toString()

    private val btnSeller: MaterialButton by lazy { findViewById(R.id.btnSeller) }
    private val btnCustomer: MaterialButton by lazy { findViewById(R.id.btnCustomer) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_as)

        btnSeller.setOnClickListener {
            startActivity(Intent(this, LoginSellerActivity::class.java))
        }
        btnCustomer.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}