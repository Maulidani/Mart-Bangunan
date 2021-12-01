package com.martbangunan.app.ui.activity.selller

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.martbangunan.app.R
import com.martbangunan.app.ui.activity.customer.ProductActivity
import com.martbangunan.app.ui.fragment.seller.*
import com.martbangunan.app.utils.Constant
import com.martbangunan.app.utils.PreferencesHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainSellerActivity : AppCompatActivity() {
    private lateinit var sharedPref: PreferencesHelper

    private val bottomNavigation: BottomNavigationView by lazy { findViewById(R.id.bottomNavigationSeller) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_seller)
        supportActionBar?.hide()
        sharedPref = PreferencesHelper(this)

        loadFragment(ProductSellerFragment())
        bottomNavigation.setOnItemSelectedListener { id ->
            when (id.itemId) {
                R.id.navProduct -> loadFragment(ProductSellerFragment())
                R.id.navChat -> loadFragment(ChatSellerFragment())
//                R.id.navSale -> loadFragment(SaleSellerFragment())
                R.id.navOthers -> loadFragment(OthersSellerFragment())
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frameSeller, fragment)
            commit()
        }
    }

    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.Main).launch {
            delay(2500)
            if (!sharedPref.getBoolean(Constant.PREF_IS_LOGIN)) {
                Log.e(this.toString(), "login: Anda sudah login...")
                finish()
            }
        }
    }
}