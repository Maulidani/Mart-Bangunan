package com.martbangunan.app.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.martbangunan.app.R
import com.martbangunan.app.network.model.ChatDetailModel
import com.martbangunan.app.ui.activity.ChatDetailActivity
import com.martbangunan.app.utils.Constant
import com.martbangunan.app.utils.PreferencesHelper

class ListChatAdapter(
    private val list: List<ChatDetailModel>
) :
    RecyclerView.Adapter<ListChatAdapter.ListViewlHoder>() {

    inner class ListViewlHoder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var sharedPref: PreferencesHelper

        val imgTo: ImageView by lazy { itemView.findViewById(R.id.img) }
        val nameTo: TextView by lazy { itemView.findViewById(R.id.tvName) }
        val item: CardView by lazy { itemView.findViewById(R.id.itemChat) }

        fun bindData(result: ChatDetailModel) {
            sharedPref = PreferencesHelper(itemView.context)
            val type = sharedPref.getString(Constant.PREF_TYPE)

            imgTo.load(Constant.URL_IMAGE_USER + result.image) {
                transformations(CircleCropTransformation())
            }
            nameTo.text = result.name

            item.setOnClickListener {
                if (type == "seller") {
                    itemView.context.startActivity(
                        Intent(
                            itemView.context,
                            ChatDetailActivity::class.java
                        ).putExtra("id", result.from_user_id).putExtra("name", result.name)
                    )
                } else {
                    itemView.context.startActivity(
                        Intent(
                            itemView.context,
                            ChatDetailActivity::class.java
                        ).putExtra("id", result.to_user_id).putExtra("name", result.name)
                    )
                }
            }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListChatAdapter.ListViewlHoder {
        return ListViewlHoder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_list_chat, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ListChatAdapter.ListViewlHoder, position: Int) {
        holder.bindData(list[position])
    }

    override fun getItemCount(): Int = list.size
}