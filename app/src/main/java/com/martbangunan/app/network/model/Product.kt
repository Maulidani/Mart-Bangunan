package com.martbangunan.app.network.model

data class ProductModel(
    val message: String,
    val errors: Boolean,
    val product: List<ProductDetailModel>
)

data class ProductDetailModel(
    val seller_name: String,
    val seller_image: String,
    val id: Int,
    val user_id: Int,
    val name: String,
    val product_category_id: Int,
    val quantity: Int,
    val price: Int,
//    val price: String,
    val product_id: Int,
    val image: String,
    val category: String,
    val description: String,

    )