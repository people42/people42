package com.cider.fourtytwo.signup

data class SignupForm (
    val email: String,
    val nickname: String,
    val o_auth_token: String,
    val emoji: String,
)