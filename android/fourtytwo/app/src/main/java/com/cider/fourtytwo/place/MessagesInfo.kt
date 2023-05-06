package com.cider.fourtytwo.place

data class MessagesInfo(
    val messageIdx: Int,
    val content: String,
    val userIdx: Int,
    val nickname: String,
    val emoji: String,
    val color: String,
    val brushCnt: Int,
    val emotion: String,
    val isInappropriate : Boolean,
)
