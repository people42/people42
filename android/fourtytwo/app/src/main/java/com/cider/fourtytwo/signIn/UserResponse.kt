package com.cider.fourtytwo.signIn

data class UserResponse(
    val message : String,
    val status: Int,
    val data : UserInfo
)
