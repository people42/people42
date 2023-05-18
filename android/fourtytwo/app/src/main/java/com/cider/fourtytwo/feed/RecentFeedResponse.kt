package com.cider.fourtytwo.feed

data class RecentFeedResponse(
    val message: String,
    val status: Int,
    val data: ArrayList<RecentFeedData>,
)
