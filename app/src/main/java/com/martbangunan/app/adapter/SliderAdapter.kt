package com.martbangunan.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import coil.load
import com.makeramen.roundedimageview.RoundedImageView
import com.martbangunan.app.R
import com.martbangunan.app.network.model.BannerImage
import com.martbangunan.app.utils.Constant

class SliderAdapter(
    private val bannerList: ArrayList<BannerImage>,
    private val viewPager2: ViewPager2
) : RecyclerView.Adapter<SliderAdapter.SliderViewHolder>() {

    private val runnable = Runnable {
        kotlin.run {
            bannerList.addAll(bannerList)
            notifyDataSetChanged()
        }
    }

    inner class SliderViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        private val imgView: RoundedImageView by lazy { itemView.findViewById(R.id.imgSlide) }

        fun setImg(sliderItem: BannerImage) {

            val linkImage = "${Constant.URL_IMAGE_BANNER}${sliderItem.image}"
            imgView.load(linkImage)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        return SliderViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_slide_container, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        holder.setImg(bannerList[position])

        if (position == bannerList.size - 2) {
            viewPager2.post(runnable)
        }
    }

    override fun getItemCount(): Int = bannerList.size

}