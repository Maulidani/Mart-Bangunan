package com.martbangunan.app.ui.fragment.customer

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.material.snackbar.Snackbar
import com.martbangunan.app.R
import com.martbangunan.app.adapter.ProductAdapter
import com.martbangunan.app.adapter.SellerAdapter
import com.martbangunan.app.adapter.SliderAdapter
import com.martbangunan.app.network.ApiClient
import com.martbangunan.app.network.model.BannerImage
import com.martbangunan.app.network.model.BannerModel
import com.martbangunan.app.network.model.ProductModel
import com.martbangunan.app.network.model.SellerModel
import com.martbangunan.app.ui.activity.customer.ChatActivity
import com.martbangunan.app.ui.activity.customer.ProductActivity
import com.martbangunan.app.utils.Constant
import com.martbangunan.app.utils.PreferencesHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {
    private lateinit var sharedPref: PreferencesHelper
    private val parentView: ConstraintLayout by lazy { requireActivity().findViewById(R.id.parentMainHome) }
    private val chat: ImageView by lazy { requireActivity().findViewById(R.id.imgChat) }
    private val swipeRefresh: SwipeRefreshLayout by lazy { requireActivity().findViewById(R.id.swapRefresh) }

    private val search: TextView by lazy { requireActivity().findViewById(R.id.tvSearch) }
    private val rv: RecyclerView by lazy { requireActivity().findViewById(R.id.rvSeller) }

    private val viewPager2: ViewPager2 by lazy { requireActivity().findViewById(R.id.vpBanner) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = PreferencesHelper(requireContext())
        val token = sharedPref.getString(Constant.PREF_AUTH_TOKEN)

        setBanner()

        swipeRefresh.setOnRefreshListener {
            seller(token)
        }

        chat.setOnClickListener {
            startActivity(Intent(requireContext(), ChatActivity::class.java))
        }
        search.setOnClickListener {
            startActivity(Intent(this.context, ProductActivity::class.java))
        }

        CoroutineScope(Dispatchers.Main).launch {
            Log.e(this.toString(), "user: loading...")
            getBanner()
            seller(token)
        }
    }

    private fun seller(token: String?) {
        swipeRefresh.isRefreshing = true

        ApiClient.instances.seller("Bearer $token")
            .enqueue(object : Callback<SellerModel> {
                override fun onResponse(
                    call: Call<SellerModel>,
                    response: Response<SellerModel>
                ) {
                    val message = response.body()?.message
                    val error = response.body()?.errors
                    val seller = response.body()?.user

                    if (isAdded) {
                        if (response.isSuccessful) {

                            if (error == false) {
                                val adapter = seller?.let { SellerAdapter(it) }
                                rv.layoutManager = GridLayoutManager(requireContext(), 2)
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
                    }
                    swipeRefresh.isRefreshing = false
                }

                override fun onFailure(call: Call<SellerModel>, t: Throwable) {
                    if (isAdded) {
                        Snackbar.make(
                            parentView,
                            t.message.toString(), Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    swipeRefresh.isRefreshing = false
                }

            })
    }
    private fun getBanner() {
        ApiClient.instances.banner().enqueue(object : Callback<BannerModel> {
            override fun onResponse(call: Call<BannerModel>, response: Response<BannerModel>) {

                viewPager2.adapter = response.body()?.banner?.let { SliderAdapter(it as ArrayList<BannerImage>, viewPager2) }

            }

            override fun onFailure(call: Call<BannerModel>, t: Throwable) {
                Log.e("onFailure ", t.message.toString())
            }

        })
    }
    private val handler = Handler()
    val sliderRunnable = Runnable {
        kotlin.run {
            viewPager2.currentItem = viewPager2.currentItem + 1
        }
    }
    private fun setBanner() {
        viewPager2.clipToPadding = false
        viewPager2.clipChildren = false
        viewPager2.offscreenPageLimit = 3
        viewPager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(40))

        viewPager2.setPageTransformer(compositePageTransformer)

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                handler.removeCallbacks(sliderRunnable)
                handler.postDelayed(sliderRunnable, 3000)
            }
        })
    }
    override fun onResume() {
        super.onResume()
        viewPager2.removeCallbacks(sliderRunnable)

    }

    override fun onPause() {
        super.onPause()
        viewPager2.removeCallbacks(sliderRunnable)
    }
}