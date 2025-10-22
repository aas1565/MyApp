package com.example.hw_3

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Расширение для Context, создающее единственный экземпляр DataStore
private val Context.filterDataStore by preferencesDataStore(name = "filter_preferences")

// Класс для работы с настройками фильтрации
class FilterPreferences(private val context: Context) {
    
    companion object {
        // Ключи для хранения настроек
        val MATCH_STATUS = stringPreferencesKey("match_status")
        val LEAGUE = stringPreferencesKey("league")
        val TEAM_FILTER = stringPreferencesKey("team_filter")
        
        // Значения по умолчанию
        const val DEFAULT_STATUS = "ALL"
        const val DEFAULT_LEAGUE = "ALL"
        const val DEFAULT_TEAM = ""
    }
    
    // Класс для хранения всех настроек фильтрации
    data class FilterPreference(
        val matchStatus: String,
        val league: String,
        val teamFilter: String
    )
    
    // Получение всех настроек фильтрации
    fun getFilterPreferences(): Flow<FilterPreference> {
        return context.filterDataStore.data.map { preferences ->
            FilterPreference(
                matchStatus = preferences[MATCH_STATUS] ?: DEFAULT_STATUS,
                league = preferences[LEAGUE] ?: DEFAULT_LEAGUE,
                teamFilter = preferences[TEAM_FILTER] ?: DEFAULT_TEAM
            )
        }
    }
    
    // Получение сохраненного статуса матча
    val matchStatus: Flow<String> = context.filterDataStore.data
        .map { preferences -> preferences[MATCH_STATUS] ?: DEFAULT_STATUS }
    
    // Получение сохраненной лиги
    val league: Flow<String> = context.filterDataStore.data
        .map { preferences -> preferences[LEAGUE] ?: DEFAULT_LEAGUE }
    
    // Получение сохраненного фильтра по команде
    val teamFilter: Flow<String> = context.filterDataStore.data
        .map { preferences -> preferences[TEAM_FILTER] ?: DEFAULT_TEAM }
    
    // Сохранение статуса матча
    suspend fun saveMatchStatus(status: String) {
        context.filterDataStore.edit { prefs ->
            prefs[MATCH_STATUS] = status
        }
    }
    
    // Сохранение лиги
    suspend fun saveLeague(leagueValue: String) {
        context.filterDataStore.edit { prefs ->
            prefs[LEAGUE] = leagueValue
        }
    }
    
    // Сохранение фильтра по команде
    suspend fun saveTeamFilter(team: String) {
        context.filterDataStore.edit { prefs ->
            prefs[TEAM_FILTER] = team
        }
    }
    
    // Сохранение всех настроек фильтрации
    suspend fun saveAllFilters(status: String, leagueValue: String, team: String) {
        context.filterDataStore.edit { prefs ->
            prefs[MATCH_STATUS] = status
            prefs[LEAGUE] = leagueValue
            prefs[TEAM_FILTER] = team
        }
    }
}