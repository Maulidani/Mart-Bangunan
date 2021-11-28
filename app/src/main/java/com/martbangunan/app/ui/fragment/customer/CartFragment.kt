package com.martbangunan.app.ui.fragment.customer

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.martbangunan.app.R
import com.martbangunan.app.adapter.CartAdapter
import com.martbangunan.app.network.ApiClient
import com.martbangunan.app.network.model.*
import com.martbangunan.app.utils.Constant
import com.martbangunan.app.utils.PreferencesHelper
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.BillingAddress
import com.midtrans.sdk.corekit.models.CustomerDetails
import com.midtrans.sdk.corekit.models.ItemDetails
import com.midtrans.sdk.corekit.models.ShippingAddress
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

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
    private var productName: MutableList<CartProductNameModel> = mutableListOf()
    private var distinctProductName: MutableList<CartProductNameModel> = mutableListOf()

    override fun refreshView(dataResult: CartDetailModel, onUpdate: Boolean, qty: Int) {
        checkOut()

        val token = sharedPref.getString(Constant.PREF_AUTH_TOKEN)

        CoroutineScope(Dispatchers.Main).launch {

            if (onUpdate) {
                myCart(token)
            } else {

                var index = 0
                for (i in productId) {
                    if (i.product_id == dataResult.product_id) {
                        productId.set(
                            index,
                            CartTotalModel(price = qty, product_id = dataResult.product_id)
                        )
                        productName.set(
                            index,
                            CartProductNameModel(
                                price = qty,
                                name = dataResult.name,
                                product_id = dataResult.product_id
                            )
                        )
                    }
                    index += 1
                }
                productId.add(CartTotalModel(product_id = dataResult.product_id, price = qty))
                productName.add(
                    CartProductNameModel(
                        product_id = dataResult.product_id, name = dataResult.name, price = qty
                    )
                )

                val distinct = productId.toSet().toMutableList()
                distinctProductName = productName.toSet().toMutableList()

                Log.e("product : ", productId.toString())
                Log.e("product distinct: ", distinct.toString())

                val subTotal = distinct.map { it.price }.sum()
                tvTotal.setText(subTotal.toString())
            }
        }
    }

    private fun checkOut() {
        var orderId = ""
        var totalAmount = ""
        SdkUIFlowBuilder.init()
            .setClientKey("SB-Mid-client-8RcUVS5NNNMKC0b6")
            .setContext(requireContext())
            .setTransactionFinishedCallback {
//                if (it.status == "pending") {
                    Toast.makeText(
                        requireContext(),
                        "Transaksi ${it.status}, Selesaikan pembayaran",
                        Toast.LENGTH_SHORT
                    ).show()

//                } else {
//                    if (orderId != "" && totalAmount != "")
//                        order(orderId, totalAmount.toInt())
//                }
            } // set transaction finish callback (sdk callback)
            .setMerchantBaseUrl("http://192.168.37.5:8000/api/midtrans/")
            .enableLog(true) // enable sdk log (optional)
            .setLanguage("id")
            .setColorTheme(
                CustomColorTheme(
                    "#FFE51255",
                    "#B61548",
                    "#FFE51255"
                )
            ) // set theme. it will replace theme on snap theme on MAP ( optional)
            .buildSDK()

        btnCheckout.setOnClickListener {
            val amount = tvTotal.text.toString()
            totalAmount = amount

            if (amount.isNotEmpty()) {
                val token = sharedPref.getString(Constant.PREF_AUTH_TOKEN)

                var name = ""
                for (i in productName) {
                    name = "${i.name} dan lainnya "
                }
                val currentTimeHills = System.currentTimeMillis()
                val transactionRequest = TransactionRequest(
                    "mb-$currentTimeHills",
                    totalAmount.toDouble()
                )

                orderId = "bm-$currentTimeHills"

                val details = com.midtrans.sdk.corekit.models.ItemDetails(
                    orderId,
                    amount.toDouble(),
                    1,
                    name,
                )
                val itemDetails = ArrayList<ItemDetails>()
                itemDetails.add(details)

                transactionRequest.itemDetails = itemDetails

                // user
                ApiClient.instances.user("Bearer $token").enqueue(object : Callback<UserModel> {
                    override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                        val message = response.body()?.message
                        val error = response.body()?.errors
                        val user = response.body()?.user
                        if (isAdded) {
                            if (response.isSuccessful) {

                                if (error == false) {

                                    Log.e(this.toString(), "user: success")

                                    val customerDetails = CustomerDetails()
                                    customerDetails.customerIdentifier = user?.user_name
                                    customerDetails.phone = user?.phone.toString()
                                    customerDetails.firstName = user?.user_name
                                    customerDetails.lastName = user?.user_name
                                    customerDetails.email = user?.email

                                    val shippingAddress = ShippingAddress()
                                    shippingAddress.address = user?.address
                                    shippingAddress.city = user?.city
                                    shippingAddress.postalCode = "10220"
                                    customerDetails.shippingAddress = shippingAddress

                                    val billingAddress = BillingAddress()
                                    shippingAddress.address = user?.address
                                    shippingAddress.city = user?.city
                                    billingAddress.postalCode = "10220"
                                    customerDetails.billingAddress = billingAddress

                                    transactionRequest.customerDetails = customerDetails

                                    MidtransSDK.getInstance().transactionRequest =
                                        transactionRequest

                                    MidtransSDK.getInstance().startPaymentUiFlow(requireActivity())

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


                        }
                    }

                    override fun onFailure(call: Call<UserModel>, t: Throwable) {

                        if (isAdded) {
                            Snackbar.make(
                                parentView,
                                "Gogal memuat informasi", Snackbar.LENGTH_SHORT
                            ).show()

                            Log.e(this.toString(), "user: gagal")

                        }
                    }
                })
                //
            }
        }
    }

//    private fun order(orderId: String, totalAmoun: Int) {
//        val token = sharedPref.getString(Constant.PREF_AUTH_TOKEN)
//        val productId = distinctProductName.map { it.product_id }
//        ApiClient.instances.addOrder("Bearer $token", 0, "", orderId, totalAmoun,productId.toTypedArray()).enqueue(object : Callback<ProductModel> {
//            override fun onResponse(call: Call<ProductModel>, response: Response<ProductModel>) {
//                val message = response.body()?.message
//                val error = response.body()?.errors
//                if (isAdded) {
//                    if (response.isSuccessful) {
//
//                        if (error == false) {
//                            Snackbar.make(
//                                parentView,
//                                "Berhasil order", Snackbar.LENGTH_SHORT
//                            ).show()
//                        } else {
//                            Snackbar.make(
//                                parentView,
//                                "Gagal", Snackbar.LENGTH_SHORT
//                            ).show()
//                            Log.e(this.toString(), "user: gagal")
//
//                        }
//                    } else {
//
//                        Snackbar.make(
//                            parentView,
//                            "Gagal", Snackbar.LENGTH_SHORT
//                        ).show()
//
//                        Log.e(this.toString(), "user: gagal")
//
//                    }
//
//
//                }
//            }
//
//            override fun onFailure(call: Call<ProductModel>, t: Throwable) {
//
//                if (isAdded) {
//                    Snackbar.make(
//                        parentView,
//                        "Gogal", Snackbar.LENGTH_SHORT
//                    ).show()
//
//                    Log.e(this.toString(), "user: gagal")
//
//                }
//            }
//
//        })
//    }

}
