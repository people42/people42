package com.cider.fourtytwo.feed

data class RecentMessageInfo(
    val nickname : String,
    val userCnt: Int,
    val firstTimeUserEmojis: ArrayList<String>,
    val repeatUserEmojis : ArrayList<String>,
)
