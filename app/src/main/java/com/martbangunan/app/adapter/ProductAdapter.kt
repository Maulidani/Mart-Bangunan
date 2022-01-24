package com.martbangunan.app.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.martbangunan.app.R
import com.martbangunan.app.network.ApiClient
import com.martbangunan.app.network.model.ProductDetailModel
import com.martbangunan.app.network.model.ProductModel
import com.martbangunan.app.ui.activity.selller.UploadProductActivity
import com.martbangunan.app.ui.activity.ProductDetailActivity
import com.martbangunan.app.utils.Constant
import com.martbangunan.app.utils.PreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductAdapter(private val list: List<ProductDetailModel>, type: String) :
    RecyclerView.Adapter<ProductAdapter.ListViewlHoder>() {

    val _type = type

    inner class ListViewlHoder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView by lazy { itemView.findViewById(R.id.tvProductName) }
        val price: TextView by lazy { itemView.findViewById(R.id.tvProductPrice) }
        val imgView: ImageView by lazy { itemView.findViewById(R.id.imgProduct) }
        val item: CardView by lazy { itemView.findViewById(R.id.itemCardProduct) }
        val imgMore: ImageView by lazy { itemView.findViewById(R.id.imgMore) }
        private lateinit var sharedPref: PreferencesHelper

        fun bindData(result: ProductDetailModel) {

            sharedPref = PreferencesHelper(itemView.context)
            val type = sharedPref.getString(Constant.PREF_TYPE)
            val token = sharedPref.getString(Constant.PREF_AUTH_TOKEN)

            if (type == "seller") {
                imgMore.visibility = View.VISIBLE
            } else {
                imgMore.visibility = View.INVISIBLE
            }

            imgMore.setOnClickListener {
                optionAlert(itemView, result, token)
            }
            var linkImage = "${Constant.URL_IMAGE_PRODUCT}${result.image}"
            imgView.load(linkImage)
            linkImage = ""

            productName.text = result.name
            price.text = result.price.toString()

            item.setOnClickListener {
                itemView.context.startActivity(
                    Intent(itemView.context, ProductDetailActivity::class.java)
                        .putExtra("id", result.product_id.toString())
                        .putExtra("name", result.name)
                        .putExtra("seller_id", result.user_id.toString())
                        .putExtra("seller_name", result.seller_name)
                        .putExtra("price", result.price.toString())
                        .putExtra("phone", result.phone)
                        .putExtra("description", result.description)
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewlHoder {
        return ListViewlHoder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ListViewlHoder, position: Int) {
        holder.bindData(list[position])
    }

    override fun getItemCount(): Int {
        return if (_type == "home" && list.size >= 4) {
            4
        } else {
            list.size
        }
    }

    private fun optionAlert(itemView: View, result: ProductDetailModel, token: String?) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(itemView.context)
        builder.setTitle("Aksi")

        val options = arrayOf("Edit produk", "Hapus produk")
        builder.setItems(
            options
        ) { _, which ->
            when (which) {
                0 -> {
                    ContextCompat.startActivity(
                        itemView.context,
                        Intent(itemView.context, UploadProductActivity::class.java)
                            .putExtra("cek", true)
                            .putExtra("id", result.product_id)
                            .putExtra("name", result.name)
                            .putExtra("category", result.category)
                            .putExtra("quantity", result.quantity)
                            .putExtra("price", result.price)
                            .putExtra("description", result.description), null
                    )
                }
                1 -> deleteAlert(itemView, result.product_id, token)
            }
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun deleteAlert(itemView: View, id: Int, token: String?) {
        val builder = AlertDialog.Builder(itemView.context)
        builder.setTitle("Hapus")
        builder.setMessage("Hapus produk ini ?")

        builder.setPositiveButton("Ya") { _, _ ->
            delete(itemView, id, token)
        }

        builder.setNegativeButton("Tidak") { _, _ ->
            // cancel
        }
        builder.show()
    }

    private fun delete(itemView: View, id: Int, token: String?) {
        ApiClient.instances.deleteProduct("Bearer $token", id).enqueue(object :
            Callback<ProductModel> {
            override fun onResponse(call: Call<ProductModel>, response: Response<ProductModel>) {
                if (response.isSuccessful) {

                    notifyDataSetChanged()
                } else {
                    Toast.makeText(itemView.context, "gagal", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ProductModel>, t: Throwable) {
                Toast.makeText(itemView.context, t.message.toString(), Toast.LENGTH_SHORT).show()

            }

        })
    }
}