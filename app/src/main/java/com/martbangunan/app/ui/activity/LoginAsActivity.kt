package com.martbangunan.app.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.material.button.MaterialButton
import com.martbangunan.app.R
import com.martbangunan.app.utils.Constant
import com.martbangunan.app.utils.PreferencesHelper

class LoginAsActivity : AppCompatActivity() {
    private lateinit var sharedPref: PreferencesHelper

    private val btnSeller: MaterialButton by lazy { findViewById(R.id.btnSeller) }
    private val btnCustomer: MaterialButton by lazy { findViewById(R.id.btnCustomer) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_as)
        sharedPref = PreferencesHelper(this)

        btnSeller.setOnClickListener {
            startActivity(
                Intent(this, LoginActivity::class.java)
                    .putExtra("type", "seller")
            )
        }
        btnCustomer.setOnClickListener {
            startActivity(
                Intent(this, LoginActivity::class.java)
                    .putExtra("type", "customer")
            )
        }
    }
    override fun onResume() {
        super.onResume()

        if (sharedPref.getBoolean(Constant.PREF_IS_LOGIN)) {
            Log.e(this.toString(), "login: Anda sudah login...")
            finish()
        }
    }
}