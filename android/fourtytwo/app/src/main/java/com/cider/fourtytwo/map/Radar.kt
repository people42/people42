package com.cider.fourtytwo.map

import com.google.android.gms.maps.model.LatLng

data class Radar(
    val nickname: String,
    val emoji: String,
    val latLng: LatLng,
    val address: LatLng,

    )
