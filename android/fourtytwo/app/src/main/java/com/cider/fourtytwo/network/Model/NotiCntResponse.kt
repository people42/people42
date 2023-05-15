package com.cider.fourtytwo.network.Model

data class NotiCntResponse(
    val message: String,
    val status: Int,
    val data: NotiCnt,
)
