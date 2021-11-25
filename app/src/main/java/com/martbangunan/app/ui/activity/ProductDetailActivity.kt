package com.martbangunan.app.ui.activity

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.martbangunan.app.R
import com.martbangunan.app.adapter.ImageSlideAdapter
import com.martbangunan.app.network.ApiClient
import com.martbangunan.app.network.model.ProductModel
import com.martbangunan.app.network.model.RegisterModel
import com.martbangunan.app.network.model.SliderItem
import com.martbangunan.app.utils.Constant
import com.martbangunan.app.utils.PreferencesHelper
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductDetailActivity : AppCompatActivity() {
    private lateinit var sharedPref: PreferencesHelper
    private val parentView: ConstraintLayout by lazy { findViewById(R.id.parentDetailProduct) }
    private val parentBtn: ConstraintLayout by lazy { findViewById(R.id.parentBtn) }

    val tvBarNameProduct: TextView by lazy { findViewById(R.id.tvBarNameProduct) }
    val back: ImageView by lazy { findViewById(R.id.imgBack) }

    val tvNameProduct: TextView by lazy { findViewById(R.id.tvProductName) }
    val tvSeller: TextView by lazy { findViewById(R.id.tvSeller) }
    val tvPrice: TextView by lazy { findViewById(R.id.tvProductPrice) }
    val tvDecription: TextView by lazy { findViewById(R.id.tvDeskripsi) }
    val imgSLider: SliderView by lazy { findViewById(R.id.rvImageProduct) }

    val btnCart: MaterialButton by lazy { findViewById(R.id.btnCart) }
    val btnCheckout: MaterialButton by lazy { findViewById(R.id.btnCheckout) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        sharedPref = PreferencesHelper(this)
        val type = sharedPref.getString(Constant.PREF_TYPE)
        val token = sharedPref.getString(Constant.PREF_AUTH_TOKEN)
        if (type == "seller") {
            parentBtn.visibility = View.INVISIBLE
        } else {
            parentBtn.visibility = View.VISIBLE        }

        val idProduct = intent.getStringExtra("id")
        val nameProduct = intent.getStringExtra("name")
        val sellerId = intent.getStringExtra("seller_id")
        val sellerProduct = intent.getStringExtra("seller_name")
        val priceProduct = intent.getStringExtra("price")
        val descriptionProduct = intent.getStringExtra("description")

        tvBarNameProduct.text = nameProduct.toString()
        tvNameProduct.text = nameProduct.toString()
        tvSeller.text = sellerProduct.toString()
        tvPrice.text = priceProduct.toString()
        tvDecription.text = descriptionProduct.toString()

        imgSLider.setIndicatorAnimation(IndicatorAnimationType.WORM);
        imgSLider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        imgSLider.setIndicatorSelectedColor(Color.WHITE);
        imgSLider.setIndicatorUnselectedColor(Color.GRAY);

        CoroutineScope(Dispatchers.Main).launch {
            getImage(token!!, idProduct.toString())
        }

        back.setOnClickListener {
            finish()
        }

        btnCart.setOnClickListener {
            addCart(token, idProduct)
        }
        btnCheckout.setOnClickListener {

        }

    }

    private fun getImage(token: String, id: String) {
        ApiClient.instances.imageProduct("Bearer $token", id)
            .enqueue(object : Callback<SliderItem> {
                override fun onResponse(call: Call<SliderItem>, response: Response<SliderItem>) {
                    val image = response.body()?.image

                    if (response.isSuccessful) {
                        if (image?.isNotEmpty() == true) {
                            imgSLider.setSliderAdapter(ImageSlideAdapter(image))
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

                override fun onFailure(call: Call<SliderItem>, t: Throwable) {
                    Snackbar.make(
                        parentView,
                        "Gogal memuat informasi", Snackbar.LENGTH_SHORT
                    ).show()
                }

            })

    }

    private fun addCart(token: String?, idProduct: String?) {

        ApiClient.instances.addCart("Bearer $token", idProduct!!.toInt()).enqueue(object : Callback<ProductModel> {
            override fun onResponse(
                call: Call<ProductModel>,
                response: Response<ProductModel>
            ) {
                val message = response.body()?.message
                if (response.isSuccessful) {
                    if (message == "Success") {
                        Snackbar.make(
                            parentView,
                            "Berhasil tambah ke gerobak", Snackbar.LENGTH_SHORT
                        ).show()
                    } else {
                        Snackbar.make(
                            parentView,
                            "Gagal", Snackbar.LENGTH_SHORT
                        ).show()
                    }

                } else {
                    Snackbar.make(
                        parentView,
                        "Gagal", Snackbar.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ProductModel>, t: Throwable) {
                Snackbar.make(
                    parentView,
                    "Gagal", Snackbar.LENGTH_SHORT
                ).show()

                Log.e(this.toString(), "onFailure: " + t.message.toString())
            }
        })
    }
}