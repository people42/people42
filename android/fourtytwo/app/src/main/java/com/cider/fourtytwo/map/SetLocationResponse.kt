package com.cider.fourtytwo.map

data class SetLocationResponse(
    val message: String,
    val status: Int,
    val data: SetLocationData
)
