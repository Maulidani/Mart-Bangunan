package com.martbangunan.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.martbangunan.app.R
import com.martbangunan.app.network.model.ChatDetailModel
import com.martbangunan.app.utils.Constant
import com.martbangunan.app.utils.PreferencesHelper

class ListChatAdapter(
    private val list: List<ChatDetailModel>
) :
    RecyclerView.Adapter<ListChatAdapter.ListViewlHoder>() {

    inner class ListViewlHoder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imgTo: ImageView by lazy {itemView.findViewById(R.id.img)}
        val nameTo: TextView by lazy {itemView.findViewById(R.id.tvName)}

        fun bindData(result: ChatDetailModel) {

            imgTo.load(Constant.URL_IMAGE_USER+result.image) {
                transformations(CircleCropTransformation())
            }
            nameTo.text = result.name
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