package com.martbangunan.app.ui.activity.selller

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.martbangunan.app.R
import com.martbangunan.app.ui.fragment.customer.AccountFragment
import com.martbangunan.app.ui.fragment.customer.CartFragment
import com.martbangunan.app.ui.fragment.customer.CategoryFragment
import com.martbangunan.app.ui.fragment.customer.HomeFragment
import com.martbangunan.app.ui.fragment.seller.*

class MainSellerActivity : AppCompatActivity() {
    private val bottomNavigation: BottomNavigationView by lazy { findViewById(R.id.bottomNavigationSeller) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_seller)
        supportActionBar?.hide()

        loadFragment(HomeSellerFragment())
        bottomNavigation.setOnItemSelectedListener { id ->
            when (id.itemId) {
                R.id.navHome -> loadFragment(HomeSellerFragment())
                R.id.navCart -> loadFragment(ChatSellerFragment())
                R.id.navProduct -> loadFragment(ProductSellerFragment())
                R.id.navSale -> loadFragment(SaleSellerFragment())
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
}