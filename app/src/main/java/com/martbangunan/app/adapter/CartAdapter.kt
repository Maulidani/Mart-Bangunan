package com.martbangunan.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.martbangunan.app.R
import com.martbangunan.app.network.ApiClient
import com.martbangunan.app.network.model.CartDetailModel
import com.martbangunan.app.network.model.ProductModel
import com.martbangunan.app.utils.Constant
import com.martbangunan.app.utils.PreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CartAdapter(private val list: List<CartDetailModel>,
                  private val mListener: iUserRecycler) :
    RecyclerView.Adapter<CartAdapter.ListViewlHoder>() {

    inner class ListViewlHoder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var sharedPref: PreferencesHelper

        val checkBox: CheckBox by lazy {itemView.findViewById(R.id.cbCartProduct)}
        val imgCart: ImageView by lazy {itemView.findViewById(R.id.imgCartProduct)}
        val nameCart: TextView by lazy {itemView.findViewById(R.id.tvNameCartProduct)}
        val priceCart: TextView by lazy {itemView.findViewById(R.id.tvPriceCartProduct)}

        val parentQuantity: ConstraintLayout by lazy {itemView.findViewById(R.id.parentQuantity)}
        val quantityCart: EditText by lazy {itemView.findViewById(R.id.etQuantityCartProduct)}
        val imgPlus: ImageView by lazy {itemView.findViewById(R.id.imgPlusCartProduct)}
        val imgMinus: ImageView by lazy {itemView.findViewById(R.id.imgMinusCartProduct)}
        val imgDelete: ImageView by lazy {itemView.findViewById(R.id.imgDeleteCart)}

        fun bindData(result: CartDetailModel) {
            sharedPref = PreferencesHelper(itemView.context)
            val token = sharedPref.getString(Constant.PREF_AUTH_TOKEN)

            imgCart.load(Constant.URL_IMAGE_PRODUCT+result.image)
            nameCart.text = result.name
            priceCart.text = result.price.toString()

            imgDelete.setOnClickListener {
                deleteAlert(itemView, result.cart_id, token, result)
            }

            checkBox.setOnClickListener {
                parentQuantity.visibility = View.VISIBLE
                var qty = 0
                mListener.refreshView(result, false,qty)
                if (checkBox.isChecked) {
                    parentQuantity.visibility = View.VISIBLE
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
                        if (quantityCart.text.toString().isNotEmpty()){
                            qty = quantityCart.text.toString().toInt()

                            if (quantityCart.text.isNullOrEmpty()) {
                                qty = 0
                            }
                            val total = result.price * qty

                            mListener.refreshView(result, false,total)
                        }
                    }

                } else {
                    mListener.refreshView(result, false,0)
                    parentQuantity.visibility = View.INVISIBLE
                }
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewlHoder {
        return ListViewlHoder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ListViewlHoder, position: Int) {
        holder.bindData(list[position])
    }

    override fun getItemCount(): Int = list.size

    private fun deleteAlert(itemView: View, id: Int, token: String?, result: CartDetailModel) {
        val builder = AlertDialog.Builder(itemView.context)
        builder.setTitle("Hapus")
        builder.setMessage("Hapus produk ini dari gerobak?")

        builder.setPositiveButton("Ya") { _, _ ->
            delete(itemView, id, token, result)
        }

        builder.setNegativeButton("Tidak") { _, _ ->
            // cancel
        }
        builder.show()
    }

    private fun delete(itemView: View, id: Int, token: String?, result: CartDetailModel) {
        ApiClient.instances.deleteCart("Bearer $token", id).enqueue(object :
            Callback<ProductModel> {
            override fun onResponse(call: Call<ProductModel>, response: Response<ProductModel>) {
                if (response.isSuccessful) {
                    mListener.refreshView(result, true, 0)
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

    interface iUserRecycler {
        fun refreshView(dataResult: CartDetailModel, onUpdate: Boolean, qty: Int)
    }
}