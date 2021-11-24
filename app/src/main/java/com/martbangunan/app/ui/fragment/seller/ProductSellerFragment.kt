package com.martbangunan.app.ui.fragment.seller

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.martbangunan.app.R
import com.martbangunan.app.adapter.ProductAdapter
import com.martbangunan.app.network.ApiClient
import com.martbangunan.app.network.model.ProductModel
import com.martbangunan.app.ui.activity.selller.UploadProductActivity
import com.martbangunan.app.utils.Constant
import com.martbangunan.app.utils.PreferencesHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductSellerFragment : Fragment() {
    private lateinit var sharedPref: PreferencesHelper
    val fabAdd: FloatingActionButton by lazy { requireActivity().findViewById(R.id.fabAddProduct) }
    private val parentView: ConstraintLayout by lazy { requireActivity().findViewById(R.id.parentMyProduct) }
    private val swipeRefresh: SwipeRefreshLayout by lazy { requireActivity().findViewById(R.id.swapRefresh) }

    private val search: TextView by lazy { requireActivity().findViewById(R.id.etSearch) }
    private val rv: RecyclerView by lazy { requireActivity().findViewById(R.id.rvProduct) }

    var token = ""
    var id = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_seller, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = PreferencesHelper(requireContext())
        token = sharedPref.getString(Constant.PREF_AUTH_TOKEN)!!
        id = sharedPref.getString(Constant.PREF_ID)!!

        swipeRefresh.setOnRefreshListener {
            product(token, id, "")
        }
        search.addTextChangedListener {
            product(token, id, search.text.toString())
        }

        fabAdd.setOnClickListener {
            startActivity(Intent(requireContext(), UploadProductActivity::class.java))
        }

        CoroutineScope(Dispatchers.Main).launch {
            Log.e(this.toString(), "user: loading...")
            product(token, id, "")
        }
    }

    private fun product(token: String?, id: String?, s: String) {
        swipeRefresh.isRefreshing = true

        ApiClient.instances.sellerProduct("Bearer $token", id.toString(), s)
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
                                val adapter = product?.let { ProductAdapter(it, "") }
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

                        swipeRefresh.isRefreshing = false

                    }
                }

                override fun onFailure(call: Call<ProductModel>, t: Throwable) {
                    if (isAdded) {
                        Snackbar.make(
                            parentView,
                            t.message.toString(), Snackbar.LENGTH_SHORT
                        ).show()
                        swipeRefresh.isRefreshing = false
                    }
                }

            })
    }

    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.Main).launch {
            Log.e(this.toString(), "user: loading...")
            product(token, id, "")
        }
    }

}