package com.cider.fourtytwo

import android.app.Application
import com.cider.fourtytwo.dataStore.UserDataStore

class App : Application() {

        private lateinit var dataStore : UserDataStore

    companion object {
        private lateinit var App: App
        fun getInstance() : App = App
    }

    override fun onCreate() {
        super.onCreate()
        App = this
        dataStore = UserDataStore(this)
    }

    fun getDataStore() : UserDataStore = dataStore
}
//    init {
//        instance = this
//    }
//
//    companion object {
//        private var instance: App? = null
//        fun context(): Context {
//            return instance!!.applicationContext
//        }
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//    }
//}
