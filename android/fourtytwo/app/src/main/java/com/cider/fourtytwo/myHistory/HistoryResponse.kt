package com.cider.fourtytwo.myHistory

data class HistoryResponse(
    val message: String,
    val status: Int,
    val data: ArrayList<HistoryData>
)
