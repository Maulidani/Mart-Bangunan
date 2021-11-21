package com.martbangunan.app.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.martbangunan.app.R
import com.martbangunan.app.network.ApiClient
import com.martbangunan.app.network.model.LoginModel
import com.martbangunan.app.ui.activity.customer.MainActivity
import com.martbangunan.app.ui.activity.customer.RegistrationActivity
import com.martbangunan.app.ui.activity.selller.MainSellerActivity
import com.martbangunan.app.ui.activity.selller.RegistrationSellerActivity
import com.martbangunan.app.utils.Constant
import com.martbangunan.app.utils.PreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var sharedPref: PreferencesHelper
    private lateinit var typeLogin: String
    private val parentView: ConstraintLayout by lazy { findViewById(R.id.parent) }
    private val btnLogin: MaterialButton by lazy { findViewById(R.id.btnLogin) }
    private val inputEmail: TextInputEditText by lazy { findViewById(R.id.inputEmail) }
    private val inputPassword: TextInputEditText by lazy { findViewById(R.id.inputPassword) }
    private val tvMasuk: TextView by lazy { findViewById(R.id.tvMasuk) }
    private val tvRegister: TextView by lazy { findViewById(R.id.tvRegister) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        sharedPref = PreferencesHelper(this)

        typeLogin = intent.getStringExtra("type").toString()

        tvRegister.setOnClickListener {
            if (typeLogin == "seller"){
                startActivity(Intent(applicationContext,RegistrationSellerActivity::class.java))
            } else {
                startActivity(Intent(applicationContext, RegistrationActivity::class.java))
            }
        }

        btnLogin.setOnClickListener {
            val email = inputEmail.text.toString()
            val password = inputPassword.text.toString()
            if (email.isEmpty() || password.isEmpty()) {
                Snackbar.make(
                    parentView,
                    "Lengkapi data", Snackbar.LENGTH_SHORT
                ).show()
            } else {

                login(email, password)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (typeLogin == "seller") {
            tvMasuk.text = "Masuk Toko"
        } else {
            tvMasuk.text = "Masuk"
        }

        if (sharedPref.getBoolean(Constant.PREF_IS_LOGIN)) {
            Log.e(this.toString(), "login: Anda sudah login...")
            finish()
        }
    }

    private fun login(email: String, password: String) {

        ApiClient.instances.login(email, password, typeLogin)
            .enqueue(object : Callback<LoginModel> {
                override fun onResponse(call: Call<LoginModel>, response: Response<LoginModel>) {

                    val message = response.body()?.message
                    val error = response.body()?.errors
                    val token = response.body()?.api_token

                    if (response.isSuccessful) {
                        if (error == false) {

                            saveSession(token!!)
                        } else {

                            Snackbar.make(
                                parentView,
                                message.toString(), Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Snackbar.make(
                            parentView,
                            "the provided credentials do not match our records",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<LoginModel>, t: Throwable) {

                    Snackbar.make(
                        parentView,
                        t.message.toString(), Snackbar.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun saveSession(apiToken: String) {

        sharedPref.put(Constant.PREF_AUTH_TOKEN, apiToken)
        sharedPref.put(Constant.PREF_TYPE, typeLogin)
        sharedPref.put(Constant.PREF_IS_LOGIN, true)
        Log.e(this.toString(), "token: $apiToken")

        if (typeLogin == "seller") {
            startActivity(Intent(this, MainSellerActivity::class.java))
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }
        finish()
    }
}
