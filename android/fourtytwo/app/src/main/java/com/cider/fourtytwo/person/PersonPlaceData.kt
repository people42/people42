package com.cider.fourtytwo.person

import com.cider.fourtytwo.place.MessagesInfo

data class PersonPlaceData(
    val messagesInfo : ArrayList<PersonPlaceInfo>,
    val brushCnt : Int,
)
