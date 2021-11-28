package com.martbangunan.app.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                101
            )
        }

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