package com.cider.fourtytwo.myHistory

import com.cider.fourtytwo.myHistory.HistoryData

data class HistoryResponse(
    val message: String,
    val status: Int,
    val data: List<HistoryData>
)
