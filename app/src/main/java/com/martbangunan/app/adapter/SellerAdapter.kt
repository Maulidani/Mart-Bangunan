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
import com.martbangunan.app.network.model.UserInfoModel
import com.martbangunan.app.ui.activity.ProductDetailActivity
import com.martbangunan.app.ui.activity.customer.ProductActivity
import com.martbangunan.app.ui.activity.selller.UploadProductActivity
import com.martbangunan.app.utils.Constant
import com.martbangunan.app.utils.PreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SellerAdapter(private val list: List<UserInfoModel>) :
    RecyclerView.Adapter<SellerAdapter.ListViewlHoder>() {

    inner class ListViewlHoder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sellerName: TextView by lazy { itemView.findViewById(R.id.tvSeller) }
        val imgSeller: ImageView by lazy { itemView.findViewById(R.id.imgSeller) }
        val item: CardView by lazy { itemView.findViewById(R.id.itemSeller) }
        private lateinit var sharedPref: PreferencesHelper

        fun bindData(result: UserInfoModel) {

            sharedPref = PreferencesHelper(itemView.context)
            val type = sharedPref.getString(Constant.PREF_TYPE)
            val token = sharedPref.getString(Constant.PREF_AUTH_TOKEN)

            var linkImage = "${Constant.URL_IMAGE_USER}${result.image}"
            imgSeller.load(linkImage)

            sellerName.text = result.user_name

            item.setOnClickListener {
                itemView.context.startActivity(
                    Intent(itemView.context, ProductActivity::class.java)
                        .putExtra("seller", result.id.toString())
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewlHoder {
        return ListViewlHoder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_seller, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ListViewlHoder, position: Int) {
        holder.bindData(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}