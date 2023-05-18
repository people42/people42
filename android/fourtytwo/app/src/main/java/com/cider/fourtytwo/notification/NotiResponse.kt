package com.cider.fourtytwo.notification

data class NotiResponse(
    val message: String,
    val status: Int,
    val data: ArrayList<NotiItem>,
)
