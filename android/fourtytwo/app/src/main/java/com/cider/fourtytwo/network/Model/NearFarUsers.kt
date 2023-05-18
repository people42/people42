package com.cider.fourtytwo.network.Model

data class NearFarUsers(
    val type : String,
    val userIdx : Int,
    val latitude : Double,
    val longitude : Double,
    val nickname : String,
    val message : String,
    val emoji : String,
    val status : String,
)
