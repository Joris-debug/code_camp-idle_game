package com.example.idle_game.data.repositories;

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.example.idle_game.util.HIGH_CONTRAST
import com.example.idle_game.util.LOW_CONTRAST
import com.example.idle_game.util.OPTION_THEME
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Repo: Datastore for all option on the settings-page
 */
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


    // Theme & Contrast level

    private val THEME_KEY = booleanPreferencesKey("option_${OPTION_THEME}")
    private val CONTRAST_KEY = intPreferencesKey("option_contrast")

    private val _contrastState = MutableStateFlow(LOW_CONTRAST)  // Default contrast value is 0
    val contrastState: StateFlow<Int> = _contrastState.asStateFlow()
    private val _themeState = MutableStateFlow(true)
    val themeState: StateFlow<Boolean> = _themeState.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            combine(
                dataStore.data.map { preferences -> preferences[THEME_KEY] ?: true },
                dataStore.data.map { preferences -> preferences[CONTRAST_KEY] ?: LOW_CONTRAST }
            ) { isDark, contrast ->
                Pair(isDark, contrast)
            }.collect { (isDark, contrast) ->
                _themeState.value = isDark
                _contrastState.value = contrast
            }
        }
    }

    suspend fun saveTheme(theme: Boolean) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme
        }
        _themeState.value = theme
    }

    suspend fun saveContrast(contrast: Int) {
        if (contrast in LOW_CONTRAST..HIGH_CONTRAST) {  // Ensure contrast value is between 0 and 2
            dataStore.edit { preferences ->
                preferences[CONTRAST_KEY] = contrast
            }
            _contrastState.value = contrast
        }
    }

    fun getContrast(): Flow<Int> {
        return dataStore.data.map { preferences ->
            preferences[CONTRAST_KEY] ?: LOW_CONTRAST  // Default to 0 if not set
        }
    }
}