package com.martbangunan.app.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.martbangunan.app.R
import com.martbangunan.app.ui.activity.customer.MainActivity
import com.martbangunan.app.ui.activity.selller.MainSellerActivity
import com.martbangunan.app.utils.Constant
import com.martbangunan.app.utils.PreferencesHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var sharedPref: PreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        sharedPref = PreferencesHelper(this)

        CoroutineScope(Dispatchers.Main).launch {
            delay(2500)
            if (sharedPref.getBoolean(Constant.PREF_IS_LOGIN)) {
                Log.e(this.toString(), "login: Anda sudah login...")

                if (sharedPref.getString(Constant.PREF_TYPE) == "seller") {
                    startActivity(Intent(applicationContext, MainSellerActivity::class.java))
                } else {
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                }

            } else {
                startActivity(Intent(this@SplashScreenActivity, LoginAsActivity::class.java))
            }
            finish()
        }
    }
}