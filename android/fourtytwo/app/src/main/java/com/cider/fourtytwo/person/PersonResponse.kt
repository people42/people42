package com.cider.fourtytwo.person

data class PersonResponse(
    val message: String,
    val status: Int,
    val data: PersonData,
)
