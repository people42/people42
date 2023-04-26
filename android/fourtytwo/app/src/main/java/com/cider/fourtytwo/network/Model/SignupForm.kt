package com.cider.fourtytwo.network.Model

data class SignupForm (
    val email: String,
    val nickname: String,
    val o_auth_token: String,
    val color: String,
    val emoji: String,
)