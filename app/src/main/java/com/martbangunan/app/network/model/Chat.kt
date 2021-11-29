package com.martbangunan.app.network.model

data class ChatModel(
    val message: String,
    val errors: Boolean,
    val chat: List<ChatDetailModel>
)

data class ChatDetailModel(
    val id:Int,
    val from_user_id:Int,
    val to_user_id:Int,
    val name:String,
    val message:String,
    val created_at:String,
    val image:String,
)