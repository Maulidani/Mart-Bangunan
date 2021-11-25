package com.martbangunan.app.ui.fragment.customer

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.martbangunan.app.R
import com.martbangunan.app.adapter.CartAdapter
import com.martbangunan.app.network.ApiClient
import com.martbangunan.app.network.model.CartDetailModel
import com.martbangunan.app.network.model.CartTotalModel
import com.martbangunan.app.network.model.ProductModel
import com.martbangunan.app.utils.Constant
import com.martbangunan.app.utils.PreferencesHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartFragment : Fragment(), CartAdapter.iUserRecycler {
    private lateinit var sharedPref: PreferencesHelper
    private val parentView: ConstraintLayout by lazy { requireActivity().findViewById(R.id.parentCart) }
    private val swipeRefresh: SwipeRefreshLayout by lazy { requireActivity().findViewById(R.id.swapRefresh) }
    private val rv: RecyclerView by lazy { requireActivity().findViewById(R.id.rvCart) }
    private val btnCheckout: MaterialButton by lazy { requireActivity().findViewById(R.id.btnCheckout) }
    private val tvTotal: TextView by lazy { requireActivity().findViewById(R.id.tvSubTotal) }

    lateinit var result: List<CartDetailModel>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = PreferencesHelper(requireContext())
        val token = sharedPref.getString(Constant.PREF_AUTH_TOKEN)

        swipeRefresh.setOnRefreshListener {
            myCart(token)
        }

        CoroutineScope(Dispatchers.Main).launch {
            Log.e(this.toString(), "user: loading...")
            myCart(token)
        }
    }

    private fun myCart(token: String?) {
        swipeRefresh.isRefreshing = true

        ApiClient.instances.myCart("Bearer $token")
            .enqueue(object : Callback<ProductModel> {
                override fun onResponse(
                    call: Call<ProductModel>,
                    response: Response<ProductModel>
                ) {
                    val error = response.body()?.errors
                    val product = response.body()?.cart

                    if (isAdded) {
                        if (response.isSuccessful) {

                            if (error == false) {
                                if (product != null) {
                                    result = product
                                    val adapter =
                                        product?.let { CartAdapter(it, this@CartFragment) }
                                    rv.layoutManager = LinearLayoutManager(requireContext())
                                    rv.adapter = adapter
                                }

                            } else {
                                Snackbar.make(
                                    parentView,
                                    "Gagal memuat informasi", Snackbar.LENGTH_SHORT
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

    private var productId: MutableList<CartTotalModel> = mutableListOf()
    var total = 0
    override fun refreshView(dataResult: CartDetailModel, onUpdate: Boolean, qty: Int) {
        val token = sharedPref.getString(Constant.PREF_AUTH_TOKEN)
        CoroutineScope(Dispatchers.Main).launch {

            if (onUpdate) {
                myCart(token)
            } else {


                var index = 0
                for (i in productId) {
                    if (i.product_id == dataResult.product_id) {
                        productId.set(index, CartTotalModel(price = qty,product_id = dataResult.product_id))
                    }
                    index += 1
                }
                productId.add(CartTotalModel(product_id = dataResult.product_id, price = qty))

                val distinct = productId.toSet().toMutableList()
                Log.e("product : ", productId.toString())
                Log.e("product distinct: ", distinct.toString())

                val subTotal = distinct.map { it.price }.sum()
                tvTotal.setText(subTotal.toString())
            }
        }
    }
}
