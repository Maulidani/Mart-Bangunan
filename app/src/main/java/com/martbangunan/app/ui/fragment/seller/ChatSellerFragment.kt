package com.martbangunan.app.ui.fragment.seller

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.martbangunan.app.R
import com.martbangunan.app.adapter.ListChatAdapter
import com.martbangunan.app.network.ApiClient
import com.martbangunan.app.network.model.ChatModel
import com.martbangunan.app.utils.Constant
import com.martbangunan.app.utils.PreferencesHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatSellerFragment : Fragment() {
    private lateinit var sharedPref: PreferencesHelper
    private val rv: RecyclerView by lazy { requireActivity().findViewById(R.id.rvChat) }
    private val parentView: ConstraintLayout by lazy { requireActivity().findViewById(R.id.parentChatSeller) }
    private val swipeRefresh: SwipeRefreshLayout by lazy { requireActivity().findViewById(R.id.swapRefresh) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_seller, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = PreferencesHelper(requireContext())
        val token = sharedPref.getString(Constant.PREF_AUTH_TOKEN)
        swipeRefresh.setOnRefreshListener {
            chat(token)
        }
        CoroutineScope(Dispatchers.Main).launch {
            Log.e(this.toString(), "user: loading...")
            chat(token)
        }
    }

    private fun chat(token: String?) {
        swipeRefresh.isRefreshing = true

        ApiClient.instances.listChat("Bearer $token")
            .enqueue(object : Callback<ChatModel> {
                override fun onResponse(
                    call: Call<ChatModel>,
                    response: Response<ChatModel>
                ) {
                    val message = response.body()?.message
                    val error = response.body()?.errors
                    val chat = response.body()?.chat
                    if (isAdded) {

                        if (response.isSuccessful) {

                            if (error == false) {
                                val adapter = chat?.let { ListChatAdapter(it) }
                                rv.layoutManager = LinearLayoutManager(requireContext())
                                rv.adapter = adapter

                            } else {
                                Snackbar.make(
                                    parentView,
                                    "Gagal memuat informasi", Snackbar.LENGTH_SHORT
                                ).show()
                            }
                        } else {

                            Snackbar.make(
                                parentView,
                                "Gagal memuat informasi", Snackbar.LENGTH_SHORT
                            ).show()
                        }
                        swipeRefresh.isRefreshing = false
                    }
                }

                override fun onFailure(call: Call<ChatModel>, t: Throwable) {
                    if (isAdded) {

                        Snackbar.make(
                            parentView,
                            t.message.toString(), Snackbar.LENGTH_SHORT
                        ).show()

                        swipeRefresh.isRefreshing = false

                    }
                }

            })
    }

}