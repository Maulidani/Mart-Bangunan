package com.martbangunan.app.ui.fragment.customer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.material.snackbar.Snackbar
import com.martbangunan.app.R
import com.martbangunan.app.adapter.ProductAdapter
import com.martbangunan.app.network.ApiClient
import com.martbangunan.app.network.model.ProductModel
import com.martbangunan.app.ui.activity.customer.ProductActivity
import com.martbangunan.app.utils.Constant
import com.martbangunan.app.utils.PreferencesHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {
    private lateinit var sharedPref: PreferencesHelper
    private val parentView: ConstraintLayout by lazy { requireActivity().findViewById(R.id.parentMainHome) }
    private val swipeRefresh: SwipeRefreshLayout by lazy { requireActivity().findViewById(R.id.swapRefresh) }

    private val search: TextView by lazy { requireActivity().findViewById(R.id.tvSearch) }
    private val rv: RecyclerView by lazy { requireActivity().findViewById(R.id.rvProduct) }

    private val tvAllProduct: TextView by lazy { requireActivity().findViewById(R.id.tvAllProduct) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = PreferencesHelper(requireContext())
        val token = sharedPref.getString(Constant.PREF_AUTH_TOKEN)

        swipeRefresh.setOnRefreshListener {
            product(token)
        }

        search.setOnClickListener {
            startActivity(Intent(this.context, ProductActivity::class.java))
        }

        tvAllProduct.setOnClickListener {
            startActivity(Intent(requireContext(), ProductActivity::class.java))
        }
        CoroutineScope(Dispatchers.Main).launch {
            Log.e(this.toString(), "user: loading...")
            product(token)
        }
    }

    private fun product(token: String?) {
        swipeRefresh.isRefreshing = true

        ApiClient.instances.allProduct("Bearer $token", "", "")
            .enqueue(object : Callback<ProductModel> {
                override fun onResponse(
                    call: Call<ProductModel>,
                    response: Response<ProductModel>
                ) {
                    val message = response.body()?.message
                    val error = response.body()?.errors
                    val product = response.body()?.product

                    if (isAdded) {
                        if (response.isSuccessful) {

                            if (error == false) {
                                val adapter = product?.let { ProductAdapter(it, "home") }
                                rv.layoutManager = GridLayoutManager(requireContext(), 2)
                                rv.adapter = adapter

                            } else {
                                Snackbar.make(
                                    parentView,
                                    "Gogal memuat informasi", Snackbar.LENGTH_SHORT
                                ).show()
                            }
                        } else {

                            Snackbar.make(
                                parentView,
                                "Gogal memuat informasi", Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                    swipeRefresh.isRefreshing = false
                }

                override fun onFailure(call: Call<ProductModel>, t: Throwable) {
                    if (isAdded) {
                        Snackbar.make(
                            parentView,
                            t.message.toString(), Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    swipeRefresh.isRefreshing = false
                }

            })
    }

}