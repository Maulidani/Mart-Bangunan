package com.martbangunan.app.ui.fragment.seller

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.material.snackbar.Snackbar
import com.martbangunan.app.R
import com.martbangunan.app.network.ApiClient
import com.martbangunan.app.network.model.LogoutModel
import com.martbangunan.app.network.model.UserModel
import com.martbangunan.app.ui.activity.AboutActivity
import com.martbangunan.app.ui.activity.EditAccountActivity
import com.martbangunan.app.ui.activity.LoginAsActivity
import com.martbangunan.app.ui.activity.SKActivity
import com.martbangunan.app.utils.Constant
import com.martbangunan.app.utils.PreferencesHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OthersSellerFragment : Fragment() {
    private lateinit var sharedPref: PreferencesHelper

    private val parentView: ConstraintLayout by lazy { requireActivity().findViewById(R.id.parentMainSeller) }
    private val swipeRefresh: SwipeRefreshLayout by lazy { requireActivity().findViewById(R.id.swapRefresh) }

    private val imgProfile: ImageView by lazy { requireActivity().findViewById(R.id.imgProfileAccount) }
    private val nameProfile: TextView by lazy { requireActivity().findViewById(R.id.tvNameProfileAccount) }
    private val profile: TextView by lazy { requireActivity().findViewById(R.id.tvProfileSeller) }
    private val about: TextView by lazy { requireActivity().findViewById(R.id.tvTentang) }
    private val sk: TextView by lazy { requireActivity().findViewById(R.id.tvSyaratKetentuan) }
    private val logout: TextView by lazy { requireActivity().findViewById(R.id.tvlogout) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_others_seller, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = PreferencesHelper(requireContext())
        val token = sharedPref.getString(Constant.PREF_AUTH_TOKEN)

        swipeRefresh.setOnRefreshListener {
            user(token)
        }

        about.setOnClickListener {
            startActivity(Intent(requireContext(), AboutActivity::class.java))
        }
        sk.setOnClickListener {
            startActivity(Intent(requireContext(), SKActivity::class.java))
        }

        logout.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Keluar")
            builder.setMessage("Yakin logout ?")

            builder.setPositiveButton("Ya") { _, _ ->
                logoutAccount(token)
            }

            builder.setNegativeButton("Tidak") { _, _ ->
                // cancel
            }
            builder.show()

        }

        CoroutineScope(Dispatchers.Main).launch {
            Log.e(this.toString(), "user: loading...")
            user(token)
        }
    }

    private fun user(token: String?) {
        swipeRefresh.isRefreshing = true

        ApiClient.instances.user("Bearer $token").enqueue(object : Callback<UserModel> {
            override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                val message = response.body()?.message
                val error = response.body()?.errors
                val user = response.body()?.user
                if (isAdded) {
                    if (response.isSuccessful) {

                        if (error == false) {

                            Log.e(this.toString(), "user: success")

                            imgProfile.load("${Constant.URL_IMAGE_USER}${user?.image}") {
                                transformations(CircleCropTransformation())
                            }
                            nameProfile.text = user?.user_name

                            profile.setOnClickListener {
                                startActivity(Intent(requireContext(), EditAccountActivity::class.java)
                                    .putExtra("email", user?.email)
                                    .putExtra("address", user?.address)
                                    .putExtra("province", user?.province)
                                    .putExtra("city", user?.city)
                                    .putExtra("districts", user?.districts)
                                    .putExtra("name", user?.user_name)
                                    .putExtra("phone", user?.phone)
                                    .putExtra("image", user?.image)
                                    .putExtra("type", user?.type)
                                    .putExtra("user_id", user?.user_id.toString())
                                    .putExtra("address_id", user?.address_id)
                                )
                            }
                        } else {

                            Snackbar.make(
                                parentView,
                                "Gogal memuat informasi", Snackbar.LENGTH_SHORT
                            ).show()
                            Log.e(this.toString(), "user: gagal")

                        }
                    } else {

                        Snackbar.make(
                            parentView,
                            "Gogal memuat informasi", Snackbar.LENGTH_SHORT
                        ).show()

                        Log.e(this.toString(), "user: gagal")

                    }

                    swipeRefresh.isRefreshing = false

                }
            }

            override fun onFailure(call: Call<UserModel>, t: Throwable) {

                if (isAdded) {
                    Snackbar.make(
                        parentView,
                        "Gogal memuat informasi", Snackbar.LENGTH_SHORT
                    ).show()

                    Log.e(this.toString(), "user: gagal")

                    swipeRefresh.isRefreshing = false

                }
            }

        })
    }

    private fun logoutAccount(token: String?) {
        swipeRefresh.isRefreshing = true

        ApiClient.instances.logout("Bearer $token").enqueue(object : Callback<LogoutModel> {
            override fun onResponse(call: Call<LogoutModel>, response: Response<LogoutModel>) {
                if (isAdded) {

                    val message = response.body()?.message
                    if (response.isSuccessful) {
                        if (message == "Unauthenticated.") {
                            sharedPref.logout()
                            startActivity(Intent(requireContext(), LoginAsActivity::class.java))
                            Snackbar.make(
                                parentView,
                                message.toString(), Snackbar.LENGTH_SHORT
                            ).show()
                        } else {
                            sharedPref.logout()
                            startActivity(Intent(requireContext(), LoginAsActivity::class.java))

                        }
                    } else {
                        sharedPref.logout()
                        Snackbar.make(
                            parentView,
                            "Gagal logout", Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    swipeRefresh.isRefreshing = false

                }
            }

            override fun onFailure(call: Call<LogoutModel>, t: Throwable) {
                if (isAdded) {
                    Snackbar.make(
                        parentView,
                        "Gogal memuat informasi", Snackbar.LENGTH_SHORT
                    ).show()

                    Log.e(this.toString(), "user: gagal")
                    sharedPref.logout()
                    swipeRefresh.isRefreshing = false

                }
            }

        })
    }

    override fun onResume() {
        super.onResume()
            val token = sharedPref.getString(Constant.PREF_AUTH_TOKEN)
            user(token)
    }
}