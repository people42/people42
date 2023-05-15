package com.cider.fourtytwo.person

data class PersonPlaceInfo(
    val messageIdx: Int,
    val content: String,
    val time: String,
    val emotion: String,
    val isInappropriate : Boolean,
)
