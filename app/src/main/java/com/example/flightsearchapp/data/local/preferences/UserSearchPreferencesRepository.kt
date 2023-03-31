package com.example.flightsearchapp.data.local.preferences

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class UserSearchPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val USER_SEARCH_KEY = stringPreferencesKey("user_search")
    private val USER_DARKMODE_KEY = booleanPreferencesKey("user_darkMode")

    //READ PREFS VALUE
    val userSearchPref : Flow<String> = dataStore.data
        .catch { error ->
            if (error is IOException){
               Log.d("prefError", "error in: ${error.message}")
               emit(emptyPreferences())
           } else {
               throw error
           }
        }
        .map { prefs ->
        prefs[USER_SEARCH_KEY] ?: ""
    }

    val userDarkPref : Flow<Boolean> = dataStore.data
        .catch {error ->
            if (error is IOException){
                Log.d("prefError", "error in: ${error.message}")
                emit(emptyPreferences())
            } else {
                throw error
            }
        }
        .map { pref ->
            pref[USER_DARKMODE_KEY] ?: false
        }

    //SET PREFS VALUE
    suspend fun setUserSearchPreferences(userSearch: String) {
        dataStore.edit { prefs ->
            prefs[USER_SEARCH_KEY] = userSearch
        }
    }

    suspend fun setUserDarkModePreferences(userDark: Boolean) {
        dataStore.edit { pref ->
            pref[USER_DARKMODE_KEY] = userDark
        }
    }
}