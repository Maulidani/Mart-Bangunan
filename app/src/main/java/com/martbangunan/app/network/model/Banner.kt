package com.martbangunan.app.network.model

data class BannerModel(
    val message: String,
    val errors: Boolean,
    val banner: List<BannerImage>
)

data class BannerImage(
    val id:Int,
    val image: String
)