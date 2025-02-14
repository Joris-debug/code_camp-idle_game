package com.example.idle_game.data.repositories;

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.idle_game.util.OPTION_THEME
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SettingsRepository(private val dataStore: DataStore<Preferences>) {
    private fun getOptionKey(num: Int) = booleanPreferencesKey("option_$num")

    fun getOption(num: Int): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[getOptionKey(num)] ?: true
        }
    }

    fun getOptions(count: Int): Flow<List<Boolean>> {
        return dataStore.data.map { preferences ->
            List(count) { index -> preferences[getOptionKey(index)] ?: true }
        }
    }

    suspend fun saveOption(option: Boolean, num: Int) {
        dataStore.edit { settings ->
            settings[getOptionKey(num)] = option
        }
    }

    // Theme - datastore

    private val THEME_KEY = booleanPreferencesKey("option_${OPTION_THEME}")

    private val _themeState = MutableStateFlow(true)
    val themeState: StateFlow<Boolean> = _themeState.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.data.map { preferences ->
                preferences[THEME_KEY] ?: true
            }.collect { isDark ->
                _themeState.value = isDark
            }
        }
    }

    suspend fun saveTheme(theme: Boolean) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme
        }
        _themeState.value = theme
    }

}