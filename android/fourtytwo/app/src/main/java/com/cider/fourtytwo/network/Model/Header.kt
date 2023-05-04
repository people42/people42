package com.cider.fourtytwo.network.Model

import com.google.gson.annotations.SerializedName

data class Header(
    @SerializedName("ACCESS-TOKEN")
    var accessToken : String
)
