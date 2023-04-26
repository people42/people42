package com.cider.fourtytwo.network.repository

import com.cider.fourtytwo.network.Api
import com.cider.fourtytwo.network.RetrofitInstance

class NetworkRepository {
    private val client = RetrofitInstance.getInstance().create(Api::class.java)
    fun getUser(params: HashMap<String, String>) = client.getUserGoogle(params)
}
