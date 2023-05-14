package com.cider.fourtytwo.network.Model

data class InfoData(
    val emoji : String,
    val nearUsers: ArrayList<NearFarUsers>,
    val farUsers : ArrayList<NearFarUsers>,
    val latitude : Double,
    val userIdx : Int,
    val nickname : String,
    val type : String,
    val message : String,
    val longitude : Double,
    val status : String,
)
