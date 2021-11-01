package com.martbangunan.app.ui.activity.customer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.martbangunan.app.R
import com.martbangunan.app.ui.fragment.customer.AccountFragment
import com.martbangunan.app.ui.fragment.customer.CartFragment
import com.martbangunan.app.ui.fragment.customer.CategoryFragment
import com.martbangunan.app.ui.fragment.customer.HomeFragment

class MainActivity : AppCompatActivity() {

    private val bottomNavigation: BottomNavigationView by lazy { findViewById(R.id.bottomNavigation) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        loadFragment(HomeFragment())
        bottomNavigation.setOnItemSelectedListener { id ->
            when (id.itemId) {
                R.id.navHome -> loadFragment(HomeFragment())
                R.id.navCategory -> loadFragment(CategoryFragment())
                R.id.navCart -> loadFragment(CartFragment())
                R.id.navAccount -> loadFragment(AccountFragment())
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame, fragment)
            commit()
        }
    }
}