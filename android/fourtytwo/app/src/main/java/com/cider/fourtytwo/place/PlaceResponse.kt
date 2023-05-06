package com.cider.fourtytwo.place

import com.cider.fourtytwo.feed.RecentFeedData

data class PlaceResponse(
    val message: String,
    val status: Int,
    val data: PlacecData,
)
