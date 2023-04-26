package com.cider.fourtytwo.network.Model

data class UserInfo(
    val message: String,
    val status: Int,
    val user_idx: Int,
    val email: String,
    val nickname: String,
    val emoji: String,
    val color: String,
    val refreshToken: String,
    val accessToken: String,
)