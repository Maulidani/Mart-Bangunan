package com.martbangunan.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.martbangunan.app.R
import com.martbangunan.app.network.model.CartDetailModel
import com.martbangunan.app.network.model.ChatDetailModel
import com.martbangunan.app.utils.Constant
import com.martbangunan.app.utils.PreferencesHelper

class ChatAdapter (private val list: List<ChatDetailModel>
) :
    RecyclerView.Adapter<ChatAdapter.ListViewlHoder>() {

    inner class ListViewlHoder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var sharedPref: PreferencesHelper

        val parentInbox: LinearLayout by lazy {itemView.findViewById(R.id.parentInbox)}
        val tvInboxMessage: TextView by lazy {itemView.findViewById(R.id.tvInboxMessage)}
        val tvInboxTime: TextView by lazy {itemView.findViewById(R.id.tvInboxTime)}

        val parentSend: LinearLayout by lazy {itemView.findViewById(R.id.parentSend)}
        val tvSendMessage: TextView by lazy {itemView.findViewById(R.id.tvSendMessage)}
        val tvSendTime: TextView by lazy {itemView.findViewById(R.id.tvSendTime)}

        fun bindData(result: ChatDetailModel) {
            sharedPref = PreferencesHelper(itemView.context)
            val id = sharedPref.getString(Constant.PREF_ID)

            if (id?.toInt() == result.from_user_id) {
                parentInbox.visibility = View.GONE
                parentSend.visibility = View.VISIBLE
                tvSendMessage.text = result.message
                tvSendTime.text = result.created_at
            } else {
                parentInbox.visibility = View.VISIBLE
                parentSend.visibility = View.GONE
                tvInboxMessage.text = result.message
                tvInboxTime.text = result.created_at
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAdapter.ListViewlHoder {
        return ListViewlHoder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ChatAdapter.ListViewlHoder, position: Int) {
        holder.bindData(list[position])
    }

    override fun getItemCount(): Int = list.size
}