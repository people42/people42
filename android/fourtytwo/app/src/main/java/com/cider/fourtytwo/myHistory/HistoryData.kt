package com.cider.fourtytwo.myHistory

data class HistoryData(
    val content: String,
    val createdAt: String,
    val messageIdx: Int,
    val heart: Int,
    val fire: Int,
    val tear: Int,
    val thumbsUp: Int,
)
