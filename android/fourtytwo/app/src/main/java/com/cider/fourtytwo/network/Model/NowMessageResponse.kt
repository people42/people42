package com.cider.fourtytwo.network.Model

data class NowMessageResponse(
    val message : String,
    val status: Int,
    val data : NowMessageData
)
