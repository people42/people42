package com.cider.fourtytwo.signIn

data class UserInfo(
    val user_idx: Int,
    val email: String,
    val emoji: String,
    val color: String,
    val refreshToken: String,
    val nickname: String,
    val accessToken: String,
)