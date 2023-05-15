package com.cider.fourtytwo.network.Model

data class NowMessageData(
    val emoji: String,
    val message: String,
    val messageCnt : Int,
    val fire : Int,
    val heart : Int,
    val tear : Int,
    val thumbsUp : Int,
)
