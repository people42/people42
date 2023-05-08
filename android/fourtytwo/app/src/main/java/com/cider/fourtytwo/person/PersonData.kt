package com.cider.fourtytwo.person

data class PersonData(
    val brushCnt:Int,
    val userIdx:Int,
    val nickname:String,
    val emoji:String,
    val placeResDtos : ArrayList<PlaceResDto>
)
