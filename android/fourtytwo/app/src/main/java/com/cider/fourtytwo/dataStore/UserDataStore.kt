package com.cider.fourtytwo.dataStore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.cider.fourtytwo.App

class UserDataStore {
    private var context = App.context()

    companion object {
        private val Context.datastore : DataStore<Preferences> by preferencesDataStore("user_pref")
    }

    private val mDataStore : DataStore<Preferences> = context.datastore
    private val FIRST_FLAG = booleanPreferencesKey("FIRST_FLAG")


    suspend fun setupFirstData(){
        mDataStore.edit { preferences ->
            preferences[FIRST_FLAG] = true
        }
    }
    suspend fun getFirstData() : Boolean {
        var currentValue = false

        mDataStore.edit { preferences ->
            currentValue = preferences[FIRST_FLAG] ?: false
        }
        return currentValue
    }
}