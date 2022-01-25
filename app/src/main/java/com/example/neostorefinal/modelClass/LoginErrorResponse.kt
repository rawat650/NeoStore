package com.example.neostorefinal.modelClass

data class LoginErrorResponse(
    val message: String,
    val status: Int,
    val user_msg: String
)