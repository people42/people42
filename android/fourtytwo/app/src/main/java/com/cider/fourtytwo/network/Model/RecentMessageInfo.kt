package com.cider.fourtytwo.network.Model

data class RecentMessageInfo(
    val messageIdx: Int,
    val content: String,
    val userIdx : Int,
    val nickname : String,
    val emoji : String,
    val color : String,
    val brushCnt : Int,
    val emotion : String
)
