package com.martbangunan.app.network.model

data class ProductModel(
    val message: String,
    val errors: Boolean,
    val product: List<ProductDetailModel>,
    val cart: List<CartDetailModel>,

    )

data class ProductDetailModel(
    val seller_name: String,
    val seller_image: String,
    val phone: String,
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

data class CartDetailModel(
    val id: Int,
    val cart_id: Int,
    val user_id: Int,
    val name: String,
    val product_category_id: Int,
    val price: Int,
    val quantity: Int,
    val discount: Int,
//    val price: String,
    val product_id: Int,
    val image: String,
    val description: String,

    )

data class CartTotalModel(
    val price: Int,
    val product_id: Int,
)

data class CartProductNameModel(
    val price: Int,
    val name: String,
    val product_id: Int,
)