package com.cider.fourtytwo.network.Model

data class UserResponse(
    val message : String,
    val status: Int,
    val data : UserInfo
)
