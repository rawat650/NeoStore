package com.example.neostorefinal.modelClass

data class DefaultResponse(
    val `data`: Data,
    val message: String,
    val status: Int,
    val user_msg: String
)