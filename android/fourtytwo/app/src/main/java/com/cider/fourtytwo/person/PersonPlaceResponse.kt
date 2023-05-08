package com.cider.fourtytwo.person

data class PersonPlaceResponse(
    val message: String,
    val status: Int,
    val data: PersonPlaceData,
)
