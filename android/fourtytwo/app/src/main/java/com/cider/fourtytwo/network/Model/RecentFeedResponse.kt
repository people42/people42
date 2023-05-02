package com.cider.fourtytwo.network.Model

data class RecentFeedResponse(
    val message: String,
    val status: Int,
    val data: List<RecentFeedData>,
)
