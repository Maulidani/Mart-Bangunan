package com.martbangunan.app.ui.activity.customer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.martbangunan.app.R
import com.martbangunan.app.adapter.ProductAdapter
import com.martbangunan.app.network.ApiClient
import com.martbangunan.app.network.model.ProductModel
import com.martbangunan.app.utils.Constant
import com.martbangunan.app.utils.PreferencesHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductActivity : AppCompatActivity() {
    private lateinit var sharedPref: PreferencesHelper
    private val parentView: ConstraintLayout by lazy { findViewById(R.id.parentProduct) }
    private val back: ImageView by lazy { findViewById(R.id.imgBack) }
    private val search: EditText by lazy { findViewById(R.id.etSearch) }

    private val swipeRefresh: SwipeRefreshLayout by lazy { findViewById(R.id.swapRefresh) }
    private val rv: RecyclerView by lazy { findViewById(R.id.rvProduct) }

    var category: String? = null
    var seller: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        sharedPref = PreferencesHelper(this)
        val token = sharedPref.getString(Constant.PREF_AUTH_TOKEN)

        category = intent.getStringExtra("category").toString()
        seller = intent.getStringExtra("seller").toString()

        if (seller!="null") {
            category = "seller"
        }

        back.setOnClickListener {
            finish()
        }

        search.addTextChangedListener {
            product(token, search.text.toString())
        }

        swipeRefresh.setOnRefreshListener {
            product(token, "")
        }

        CoroutineScope(Dispatchers.IO).launch {
            Log.e(this.toString(), "user: loading...")
            product(token, "")
        }
    }

    private fun product(token: String?, search: String) {
        swipeRefresh.isRefreshing = true

        ApiClient.instances.allProduct("Bearer $token", category!!, search, seller!!)
            .enqueue(object : Callback<ProductModel> {
                override fun onResponse(
                    call: Call<ProductModel>,
                    response: Response<ProductModel>
                ) {
                    val message = response.body()?.message
                    val error = response.body()?.errors
                    val product = response.body()?.product

                    if (response.isSuccessful) {

                        if (error == false) {
                            val adapter = product?.let { ProductAdapter(it, category!!) }
                            rv.layoutManager = GridLayoutManager(applicationContext, 2)
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

                override fun onFailure(call: Call<ProductModel>, t: Throwable) {

                    Snackbar.make(
                        parentView,
                        t.message.toString(), Snackbar.LENGTH_SHORT
                    ).show()
                    swipeRefresh.isRefreshing = false
                }

            })
    }

}