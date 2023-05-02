package com.cider.fourtytwo.network.Model

data class HistoryResponse(
    val message: String,
    val status: Int,
    val data: List<HistoryData>
)
