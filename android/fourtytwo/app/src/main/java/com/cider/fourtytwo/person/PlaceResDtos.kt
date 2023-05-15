package com.cider.fourtytwo.person

data class PlaceResDto(
    val placeIdx:Int,
    val placeName:String,
    val placeLatitude:Double,
    val placeLongitude:Double,
    val brushCnt:Int,
)
