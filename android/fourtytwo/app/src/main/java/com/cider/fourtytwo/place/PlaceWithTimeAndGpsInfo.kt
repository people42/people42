package com.cider.fourtytwo.place

data class PlaceWithTimeAndGpsInfo(
    val placeIdx: Int,
    val placeName: String,
    val time: String,
    val placeLatitude: Double,
    val placeLongitude: Double
)
