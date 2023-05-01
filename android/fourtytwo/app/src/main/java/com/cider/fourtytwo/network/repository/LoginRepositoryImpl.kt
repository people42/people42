//package com.cider.fourtytwo.network.repository
//
//import android.content.Context
//import androidx.datastore.preferences.core.booleanPreferencesKey
//import androidx.datastore.preferences.core.stringPreferencesKey
//
//class LoginRepositoryImpl @Inject constructor(private val context: Context
//) : LoginRepository {
//
//    override suspend fun getKakaoToken(kakaoOauthRequest: KakaoOauthRequest): JWT {
//        return loginApi.getKakaoToken(kakaoOauthRequest).toJWT()
//    }
//
//    override suspend fun getNaverToken(naverOauthRequest: NaverOauthRequest): JWT {
//        return loginApi.getNaverToken(naverOauthRequest).toJWT()
//    }
//
//    private object PreferenceKeys {
//        val ACCESS_TOKEN = stringPreferencesKey("access_token")
//        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
//        val LOGIN_CHECK = booleanPreferencesKey("login_check")
//    }
//
//    private val Context.tokenDataStore by preferencesDataStore(TOKEN_DATASTORE)
//    private val Context.loginCheckDataStore by preferencesDataStore(LOGIN_CHECK_DATASTORE)
//
//    override suspend fun saveToken(token: List<String>) {
//        context.tokenDataStore.edit { prefs ->
//            prefs[ACCESS_TOKEN] = token.first()
//            prefs[REFRESH_TOKEN] = token.last()
//        }
//        // AccessToken, RefreshToken 이 제대로 들어온 여부를 확인하는 boolean 값
//        context.loginCheckDataStore.edit { prefs ->
//            prefs[LOGIN_CHECK] = true
//        }
//    }
//
//    override suspend fun getToken(): Flow<List<String>> {
//        return context.tokenDataStore.data
//            .catch { exception ->
//                if (exception is IOException) {
//                    exception.printStackTrace()
//                    emit(emptyPreferences())
//                } else {
//                    throw exception
//                }
//            }
//            .map { prefs ->
//                prefs.asMap().values.toList().map {
//                    it.toString()
//                }
//            }
//    }
//
//    override suspend fun getIsLogin(): Flow<Boolean> {
//        return context.loginCheckDataStore.data
//            .map { prefs ->
//                prefs[LOGIN_CHECK] ?: false
//            }
//    }
//}