package com.martbangunan.app.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.martbangunan.app.R
import com.martbangunan.app.network.model.ChatDetailModel
import com.martbangunan.app.utils.Constant
import com.martbangunan.app.utils.PreferencesHelper
import java.text.SimpleDateFormat
import java.util.*

class ChatAdapter(
    private val list: List<ChatDetailModel>
) :
    RecyclerView.Adapter<ChatAdapter.ListViewlHoder>() {

    inner class ListViewlHoder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var sharedPref: PreferencesHelper

        val parentInbox: ConstraintLayout by lazy {itemView.findViewById(R.id.parentInbox)}
        val tvInboxMessage: TextView by lazy {itemView.findViewById(R.id.tvInboxMessage)}
        val tvInboxTime: TextView by lazy {itemView.findViewById(R.id.tvInboxTime)}

        val parentSend: ConstraintLayout by lazy {itemView.findViewById(R.id.parentSend)}
        val tvSendMessage: TextView by lazy {itemView.findViewById(R.id.tvSendMessage)}
        val tvSendTime: TextView by lazy {itemView.findViewById(R.id.tvSendTime)}

        @SuppressLint("SimpleDateFormat")
        fun bindData(result: ChatDetailModel) {
            sharedPref = PreferencesHelper(itemView.context)
            val id = sharedPref.getString(Constant.PREF_ID)
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            val outputFormat = SimpleDateFormat("dd-MM-yyyy HH:mm")
            val date: Date = inputFormat.parse(result.created_at)!!
            val formattedDate: String = outputFormat.format(date)


            if (id?.toInt() == result.from_user_id) {
                parentSend.visibility = View.VISIBLE
                parentInbox.visibility = View.GONE
                tvSendMessage.text = result.message
                tvSendTime.text = formattedDate
            } else {
                parentInbox.visibility = View.VISIBLE
                parentSend.visibility = View.GONE
                tvInboxMessage.text = result.message
                tvInboxTime.text = formattedDate
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAdapter.ListViewlHoder {
        return ListViewlHoder(
            LayoutInflater.from(parent.context).inflate(R.layout.chat, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ChatAdapter.ListViewlHoder, position: Int) {
        holder.bindData(list[position])
    }

    override fun getItemCount(): Int = list.size
}