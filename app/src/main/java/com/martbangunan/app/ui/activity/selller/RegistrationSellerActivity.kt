package com.martbangunan.app.ui.activity.selller

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.martbangunan.app.R
import com.martbangunan.app.ui.activity.customer.Registration2Activity
import com.martbangunan.app.utils.Constant
import com.martbangunan.app.utils.PreferencesHelper

class RegistrationSellerActivity : AppCompatActivity() {
    private lateinit var sharedPref: PreferencesHelper
    private val parentView: ConstraintLayout by lazy { findViewById(R.id.parentRegistrationSeller) }

    private val btnNext: MaterialButton by lazy { findViewById(R.id.btnNext) }
    private val fullName: TextInputEditText by lazy { findViewById(R.id.inputFullName) }
    private val email: TextInputEditText by lazy { findViewById(R.id.inputEmail) }
    private val password: TextInputEditText by lazy { findViewById(R.id.inputPassword) }
    private val phone: TextInputEditText by lazy { findViewById(R.id.inputPhone) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_seller)
        sharedPref = PreferencesHelper(this)

        btnNext.setOnClickListener {
            if (fullName.text.toString().isEmpty() || email.text.toString()
                    .isEmpty() || password.text.toString().isEmpty() || phone.text.toString()
                    .isEmpty()
            ) {
                Snackbar.make(
                    parentView,
                    "Lengkapi data", Snackbar.LENGTH_SHORT
                ).show()
            } else {

                startActivity(
                    Intent(applicationContext, RegistrationSeller2Activity::class.java)
                        .putExtra("fullname", fullName.text.toString())
                        .putExtra("email", email.text.toString())
                        .putExtra("password", password.text.toString())
                        .putExtra("phone", phone.text.toString())
                )
            }

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