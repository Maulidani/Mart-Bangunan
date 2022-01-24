package com.martbangunan.app.ui.activity

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.martbangunan.app.R
import com.martbangunan.app.adapter.ImageSlideAdapter
import com.martbangunan.app.network.ApiClient
import com.martbangunan.app.network.model.ProductModel
import com.martbangunan.app.network.model.SliderItem
import com.martbangunan.app.network.model.UserModel
import com.martbangunan.app.ui.activity.selller.UploadProductActivity
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
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class ProductDetailActivity : AppCompatActivity() {
    private lateinit var sharedPref: PreferencesHelper
    private val parentView: ConstraintLayout by lazy { findViewById(R.id.parentDetailProduct) }
    private val parentBtn: ConstraintLayout by lazy { findViewById(R.id.parentBtn) }
    private val parentQty: ConstraintLayout by lazy { findViewById(R.id.parentQuantity) }
    private val parentQuantity: ConstraintLayout by lazy { findViewById(R.id.parentQuantity) }
    private val quantityCart: EditText by lazy { findViewById(R.id.etQuantityCartProduct) }
    private val imgPlus: ImageView by lazy { findViewById(R.id.imgPlusCartProduct) }
    private val imgMinus: ImageView by lazy { findViewById(R.id.imgMinusCartProduct) }

    private val tvBarNameProduct: TextView by lazy { findViewById(R.id.tvBarNameProduct) }
    private val back: ImageView by lazy { findViewById(R.id.imgBack) }

    private val tvNameProduct: TextView by lazy { findViewById(R.id.tvProductName) }
    private val tvSeller: TextView by lazy { findViewById(R.id.tvSeller) }
    private val tvPrice: TextView by lazy { findViewById(R.id.tvProductPrice) }
    private val tvChat: TextView by lazy { findViewById(R.id.tvChatSeller) }
    private val tvDecription: TextView by lazy { findViewById(R.id.tvDeskripsi) }
    private val imgSLider: SliderView by lazy { findViewById(R.id.rvImageProduct) }

    private val btnCart: MaterialButton by lazy { findViewById(R.id.btnCart) }
    private val btnCheckout: MaterialButton by lazy { findViewById(R.id.btnCheckout) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        sharedPref = PreferencesHelper(this)
        val type = sharedPref.getString(Constant.PREF_TYPE)
        val token = sharedPref.getString(Constant.PREF_AUTH_TOKEN)

        val idProduct = intent.getStringExtra("id")
        val nameProduct = intent.getStringExtra("name")
        val sellerId = intent.getStringExtra("seller_id")
        val sellerProduct = intent.getStringExtra("seller_name")
        val priceProduct = intent.getStringExtra("price")
        val phoneProduct = intent.getStringExtra("phone")
        val descriptionProduct = intent.getStringExtra("description")

        if (type == "seller") {
            tvChat.visibility = View.INVISIBLE
            parentBtn.visibility = View.INVISIBLE
        } else {
            tvChat.setOnClickListener {
                startActivity(Intent(this,ChatDetailActivity::class.java)
                    .putExtra("id", sellerId?.toInt())
                    .putExtra("name", sellerProduct)
                )
            }
            parentBtn.visibility = View.VISIBLE
        }

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
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("Aksi")

            val options = arrayOf("Chat whatsapp", "Checkout")
            builder.setItems(
                options
            ) { _, which ->
                when (which) {
                    0 -> {
                       openWhatsApp(phoneProduct.toString())
                    }
                    1 -> {
                        parentQty.visibility = View.VISIBLE
                        var qty = 0
                        quantityCart.setText(qty.toString())
                        imgPlus.setOnClickListener {
                            qty += 1
                            quantityCart.setText(qty.toString())
                        }
                        imgMinus.setOnClickListener {
                            qty -= 1
                            quantityCart.setText(qty.toString())
                        }

                        quantityCart.addTextChangedListener {
                            if (quantityCart.text.toString().isNotEmpty()) {
                                qty = quantityCart.text.toString().toInt()

                                if (quantityCart.text.isNullOrEmpty()) {
                                    qty = 0
                                }
                                val total = tvPrice.text.toString().toInt() * qty
                                val name = tvNameProduct.text.toString()

                                if (qty != 0) {
                                    checkOut(total, name)
                                } else {
                                    Snackbar.make(
                                        parentView,
                                        "Isi jumlah produk", Snackbar.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                }
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()



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

        ApiClient.instances.addCart("Bearer $token", idProduct!!.toInt())
            .enqueue(object : Callback<ProductModel> {
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

    private fun checkOut(total: Int, name: String) {
        SdkUIFlowBuilder.init()
            .setClientKey("SB-Mid-client-8RcUVS5NNNMKC0b6")
            .setContext(applicationContext)
            .setTransactionFinishedCallback {
//                if (it.status == "pending") {
                Toast.makeText(
                    applicationContext,
                    "Transaksi ${it.status}, Selesaikan pembayaran",
                    Toast.LENGTH_SHORT
                ).show()

//                } else {
//                    if (orderId != "" && totalAmount != "")
//                        order(orderId, totalAmount.toInt())
//                }
            } // set transaction finish callback (sdk callback)
            .setMerchantBaseUrl(Constant.URL_MIDTRANS)
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

            val token = sharedPref.getString(Constant.PREF_AUTH_TOKEN)

            val currentTimeHills = System.currentTimeMillis()
            val transactionRequest = TransactionRequest(
                "mb-$currentTimeHills",
                total.toDouble()
            )

            val orderId = "bm-$currentTimeHills"

            val details = com.midtrans.sdk.corekit.models.ItemDetails(
                orderId,
                total.toDouble(),
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

                            MidtransSDK.getInstance().startPaymentUiFlow(this@ProductDetailActivity)

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

                override fun onFailure(call: Call<UserModel>, t: Throwable) {

                    Snackbar.make(
                        parentView,
                        "Gogal memuat informasi", Snackbar.LENGTH_SHORT
                    ).show()

                    Log.e(this.toString(), "user: gagal")

                }
            })
            //
        }
    }

    private fun openWhatsApp(number: String) {
        val url = "https://api.whatsapp.com/send?phone=+62$number"
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
    }
}
