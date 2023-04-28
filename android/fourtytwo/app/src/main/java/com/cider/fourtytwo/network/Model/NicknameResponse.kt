package com.cider.fourtytwo.network.Model

data class NicknameResponse(
    val message : String,
    val status: Int,
    val data : NicknameData
)
