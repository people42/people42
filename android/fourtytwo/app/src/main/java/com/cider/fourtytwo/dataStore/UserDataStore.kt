package com.cider.fourtytwo.dataStore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.cider.fourtytwo.signIn.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class UserDataStore (private val context : Context){
//    private val context = App.context()
    companion object {
        private val Context.dataStore : DataStore<Preferences> by preferencesDataStore("user_pref")
    }
//    private val mDataStore : DataStore<Preferences> = context.dataStore
//    private val Context.dataStore by preferencesDataStore(name = "dataStore")

    private var user_idx = intPreferencesKey("user_idx")
    private var email = stringPreferencesKey("email")
    private var emoji = stringPreferencesKey("emoji")
    private var color = stringPreferencesKey("color")
    private var refreshToken = stringPreferencesKey("refreshToken")
    private var nickname = stringPreferencesKey("nickname")
    private var accessToken = stringPreferencesKey("accessToken")
    private var idToken = stringPreferencesKey("idToken")
    private var webSocket = booleanPreferencesKey("webSocket")

    val mDataStore = context.dataStore
    val get_webSocket : Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map {preferences ->
            preferences[webSocket] ?: false
        }
    val get_userIdx : Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map {preferences ->
            preferences[user_idx].toString() ?: ""
        }
    val get_email : Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map {preferences ->
            preferences[email].toString() ?: ""
        }
    val get_idToken : Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map {preferences ->
            preferences[idToken].toString() ?: ""
        }
    val get_color : Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map {preferences ->
            preferences[color].toString() ?: ""
        }
    val get_emoji : Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map {preferences ->
            preferences[emoji].toString() ?: ""
        }
    val get_access_token : Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map {preferences ->
            preferences[accessToken].toString() ?: ""
        }
    val get_refresh_token : Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map {preferences ->
            preferences[refreshToken].toString() ?: ""
        }
    suspend fun setWebSocket(webSocketstate: Boolean){
        mDataStore.edit {preferences ->
            preferences[webSocket] = webSocketstate
        }
    }

    suspend fun setUserEmail(user_email: String): String {
        mDataStore.edit {preferences ->
            preferences[email] = user_email
        }
        return user_email
    }
    suspend fun setUserIdToken(user_idToken: String): String {
        mDataStore.edit { preferences ->
            preferences[idToken] = user_idToken
        }
        return user_idToken
    }
    suspend fun setUserEmoji(user_emoji: String){
        mDataStore.edit { preferences ->
            preferences[emoji] = user_emoji
        }
    }
    suspend fun setUserNickname(user_nickname: String){
        mDataStore.edit { preferences ->
            preferences[nickname] = user_nickname
        }
    }


    suspend fun setUserData(payload: UserInfo){
        mDataStore.edit { preferences ->
            preferences[user_idx] = payload.user_idx
            preferences[email] = payload.email
            preferences[emoji] = payload.emoji
            preferences[color] = payload.color
            preferences[refreshToken] = payload.refreshToken
            preferences[nickname] = payload.nickname
            preferences[accessToken] = payload.accessToken
        }
    }

    suspend fun setUserAccessToken(user_accessToken: String){
        mDataStore.edit { preferences ->
            preferences[accessToken] = user_accessToken
        }
    }
    suspend fun setUserRefreshToken(user_refreshToken: String){
        mDataStore.edit { preferences ->
            preferences[refreshToken] = user_refreshToken
        }
    }
    suspend fun getUserAccessToken(): String {
        var result = ""
        mDataStore.edit { preferences ->
            result = preferences[accessToken].toString()
        }
        return result
    }
    suspend fun getUserRefreshToken(): String? {
        var result = ""
        mDataStore.edit { preferences ->
            result = preferences[refreshToken].toString()
        }
        return result
    }
    suspend fun getUserEmail(): String {
        var result = ""
        mDataStore.edit { preferences ->
            result = preferences[email].toString()
        }
        return result
    }
}