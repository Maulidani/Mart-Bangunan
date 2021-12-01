package com.martbangunan.app.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.martbangunan.app.R
import com.martbangunan.app.adapter.ChatAdapter
import com.martbangunan.app.network.ApiClient
import com.martbangunan.app.network.model.ChatModel
import com.martbangunan.app.utils.Constant
import com.martbangunan.app.utils.PreferencesHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatDetailActivity : AppCompatActivity() {
    private lateinit var sharedPref: PreferencesHelper
    private val parentView: ConstraintLayout by lazy { findViewById(R.id.parentChatDetail) }
    private val back: ImageView by lazy {findViewById(R.id.imgBack)}
    private val title: TextView by lazy {findViewById(R.id.tvTitleBar)}
    private val rv: RecyclerView by lazy {findViewById(R.id.rvChat)}
    private val swipeRefresh: SwipeRefreshLayout by lazy { findViewById(R.id.swapRefresh) }
    private val message: EditText by lazy {findViewById(R.id.etMessage)}
    private val send: MaterialButton by lazy {findViewById(R.id.btnSend)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_detail)

        sharedPref = PreferencesHelper(this)
        val token = sharedPref.getString(Constant.PREF_AUTH_TOKEN)

        val id = intent.getIntExtra("id",0)
        val name = intent.getStringExtra("name")

        title.text = name

        back.setOnClickListener {
            finish()
        }

        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = true
            chat(token, id)
        }

        CoroutineScope(Dispatchers.Main).launch {
            Log.e(this.toString(), "user: loading...")
            while (true){
                chat(token, id)
                delay(1500)
            }
        }

        send.setOnClickListener {
            if (message.text.toString().isNotEmpty()) {

                send(id,message.text.toString(), token)
            }
        }
    }
    private fun chat(token: String?, id: Int) {
        ApiClient.instances.getChat("Bearer $token",id)
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
                            val adapter = chat?.let { ChatAdapter(it) }
                            rv.layoutManager = LinearLayoutManager(applicationContext).apply {
                                stackFromEnd = true
                                reverseLayout = false
                            }
                            rv.adapter = adapter

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

    private fun send(id: Int, msg: String, token: String?) {
        ApiClient.instances.sendChat("Bearer $token",id,msg)
            .enqueue(object : Callback<ChatModel> {
                override fun onResponse(
                    call: Call<ChatModel>,
                    response: Response<ChatModel>
                ) {
                    val error = response.body()?.errors

                    if (response.isSuccessful) {

                        if (error == false) {

//                            CoroutineScope(Dispatchers.Main).launch {
                                Log.e(this.toString(), "user: loading...")
                                chat(token, id)
                                message.setText("")
//                            }

                        } else {
                            Snackbar.make(
                                parentView,
                                "Gogal", Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    } else {

                        Snackbar.make(
                            parentView,
                            "Gogal", Snackbar.LENGTH_SHORT
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