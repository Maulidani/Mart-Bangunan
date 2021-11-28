package com.martbangunan.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import coil.load
import com.martbangunan.app.network.model.ImageProductModel
import com.martbangunan.app.utils.Constant
import com.smarteist.autoimageslider.SliderViewAdapter


class ImageSlideAdapter(private val listSlide: List<ImageProductModel>) :
    SliderViewAdapter<ImageSlideAdapter.SliderAdapterVH>() {

    inner class SliderAdapterVH(itemView: View) : ViewHolder(itemView) {
        val imageViewBackground : ImageView by lazy { itemView.findViewById(com.martbangunan.app.R.id.iv_auto_image_slider) }
        val imageGifContainer  : ImageView by lazy { itemView.findViewById(com.martbangunan.app.R.id.iv_gif_container) }
        val textViewDescription   : ImageView by lazy { itemView.findViewById(com.martbangunan.app.R.id.tv_auto_image_slider) }

        fun bindData(result: ImageProductModel) {

            var linkImage = "${Constant.URL_IMAGE_PRODUCT}${result.image}"
            imageViewBackground .load(linkImage)

        }
    }

    override fun getCount(): Int = listSlide.size

    override fun onCreateViewHolder(parent: ViewGroup?): SliderAdapterVH {
        val inflate: View =
            LayoutInflater.from(parent!!.context)
                .inflate(com.martbangunan.app.R.layout.item_image_slide, null)
        return SliderAdapterVH(inflate)

    }

    override fun onBindViewHolder(viewHolder: SliderAdapterVH?, position: Int) {
        viewHolder?.bindData(listSlide[position])
    }
}
