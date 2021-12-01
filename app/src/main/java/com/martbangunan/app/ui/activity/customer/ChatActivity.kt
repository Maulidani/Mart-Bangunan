package com.martbangunan.app.ui.activity.customer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.martbangunan.app.R
import com.martbangunan.app.adapter.ChatAdapter
import com.martbangunan.app.adapter.ListChatAdapter
import com.martbangunan.app.adapter.ProductAdapter
import com.martbangunan.app.network.ApiClient
import com.martbangunan.app.network.model.ChatModel
import com.martbangunan.app.network.model.ProductModel
import com.martbangunan.app.utils.Constant
import com.martbangunan.app.utils.PreferencesHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatActivity : AppCompatActivity() {
    private lateinit var sharedPref: PreferencesHelper
    private val parentView: ConstraintLayout by lazy { findViewById(R.id.parentChat) }
    private val back:ImageView by lazy {findViewById(R.id.imgBack)}
    private val rv:RecyclerView by lazy {findViewById(R.id.rvChat)}
    private val swipeRefresh: SwipeRefreshLayout by lazy { findViewById(R.id.swapRefresh) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        sharedPref = PreferencesHelper(this)
        val token = sharedPref.getString(Constant.PREF_AUTH_TOKEN)

        back.setOnClickListener {
            finish()
        }

        swipeRefresh.setOnRefreshListener {
            chat(token)
        }

        CoroutineScope(Dispatchers.IO).launch {
            Log.e(this.toString(), "user: loading...")
            chat(token)
        }
    }

    private fun chat(token: String?) {
        ApiClient.instances.listChat("Bearer $token")
            .enqueue(object : Callback<ChatModel> {
                override fun onResponse(
                    call: Call<ChatModel>,
                    response: Response<ChatModel>
                ) {
                    val message = response.body()?.message
                    val error = response.body()?.errors
                    val chat = response.body()?.chat

                    if (response.isSuccessful) {

                        if (error == false) {
                            val adapter = chat?.let { ListChatAdapter(it) }
                            rv.layoutManager = LinearLayoutManager(applicationContext)
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

                override fun onFailure(call: Call<ChatModel>, t: Throwable) {

                    Snackbar.make(
                        parentView,
                        t.message.toString(), Snackbar.LENGTH_SHORT
                    ).show()
                    swipeRefresh.isRefreshing = false
                }

            })
    }
}