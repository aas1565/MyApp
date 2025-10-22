package com.example.hw_3.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.hw_3.FilterPreferences
import com.example.hw_3.api.RetrofitClient
import com.example.hw_3.data.Match
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FootballViewModel(application: Application) : AndroidViewModel(application) {
    // Инициализация FilterPreferences
    private val filterPreferences = FilterPreferences(application)
    
    // Потоки для хранения настроек фильтрации
    private val _matchStatus = MutableStateFlow("ALL")
    val matchStatus: StateFlow<String> = _matchStatus.asStateFlow()
    
    private val _league = MutableStateFlow("ALL")
    val league: StateFlow<String> = _league.asStateFlow()
    
    private val _teamFilter = MutableStateFlow("")
    val teamFilter: StateFlow<String> = _teamFilter.asStateFlow()
    
    //приватный изменяемый поток для списка матчей
    private val _matches = MutableStateFlow<List<Match>>(emptyList())
    //преобразует MutableStateFlow в StateFlow, предотвращая изменение извне
    val matches: StateFlow<List<Match>> = _matches.asStateFlow()
    
    // Отфильтрованный список матчей
    private val _filteredMatches = MutableStateFlow<List<Match>>(emptyList())
    val filteredMatches: StateFlow<List<Match>> = _filteredMatches.asStateFlow()

    //флаг загрузки (true - идет загрузка, false - завершена)
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    //хранит сообщение об ошибке или null если ошибок нет
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        // Загружаем сохраненные настройки фильтрации
        loadFilterPreferences()
        // Загружаем матчи
        fetchMatches()
    }
    
    // Загрузка сохраненных настроек фильтрации
    private fun loadFilterPreferences() {
        viewModelScope.launch {
            filterPreferences.matchStatus.collectLatest { status ->
                _matchStatus.value = status
                applyFilters()
            }
        }
        
        viewModelScope.launch {
            filterPreferences.league.collectLatest { leagueValue ->
                _league.value = leagueValue
                applyFilters()
            }
        }
        
        viewModelScope.launch {
            filterPreferences.teamFilter.collectLatest { team ->
                _teamFilter.value = team
                applyFilters()
            }
        }
    }
    
    // Сохранение настроек фильтрации
    fun saveFilterPreferences(status: String, league: String, team: String) {
        viewModelScope.launch {
            filterPreferences.saveMatchStatus(status)
            filterPreferences.saveLeague(league)
            filterPreferences.saveTeamFilter(team)
        }
    }

    //публичная функция для инициации загрузки
    fun fetchMatches() {
        _isLoading.value = true
        _error.value = null
        
        println("Начинаем загрузку матчей")

        // запускает корутину в scope ViewModel
        viewModelScope.launch {
            try {
                println("Выполняем сетевой запрос")
                
                // Загружаем тестовые данные вместо реального API-запроса
                loadTestData()
                
                // Применяем фильтры к загруженным матчам
                applyFilters()
                
            } catch (e: Exception) {
                val errorMsg = "Ошибка сети: ${e.message}"
                println("Исключение при загрузке: $errorMsg")
                e.printStackTrace()
                _error.value = errorMsg
            } finally {
                _isLoading.value = false
                println("Загрузка завершена, isLoading = false")
            }
        }
    }
    
    // Загрузка тестовых данных для отображения
    private fun loadTestData() {
        println("Загружаем тестовые данные")
        
        val testMatches = listOf(
            com.example.hw_3.data.Match(
                id = 1,
                status = "FINISHED",
                date = "2023-05-20T14:00:00Z",
                homeTeam = com.example.hw_3.data.Team(id = 1, name = "Спартак", shortName = "СПМ", crest = null),
                awayTeam = com.example.hw_3.data.Team(id = 2, name = "ЦСКА", shortName = "ЦСКА", crest = null),
                score = com.example.hw_3.data.Score(fullTime = com.example.hw_3.data.FullTimeScore(home = 2, away = 1))
            ),
            com.example.hw_3.data.Match(
                id = 2,
                status = "SCHEDULED",
                date = "2023-05-25T17:30:00Z",
                homeTeam = com.example.hw_3.data.Team(id = 3, name = "Зенит", shortName = "ЗЕН", crest = null),
                awayTeam = com.example.hw_3.data.Team(id = 4, name = "Локомотив", shortName = "ЛОК", crest = null),
                score = null
            ),
            com.example.hw_3.data.Match(
                id = 3,
                status = "LIVE",
                date = "2023-05-21T19:00:00Z",
                homeTeam = com.example.hw_3.data.Team(id = 5, name = "Динамо", shortName = "ДИН", crest = null),
                awayTeam = com.example.hw_3.data.Team(id = 6, name = "Рубин", shortName = "РУБ", crest = null),
                score = com.example.hw_3.data.Score(fullTime = com.example.hw_3.data.FullTimeScore(home = 1, away = 1))
            )
        )
        
        println("Загружено тестовых матчей: ${testMatches.size}")
        _matches.value = testMatches
        println("Установлено значение _matches: ${_matches.value.size}")
    }
    
    // Применение фильтров к списку матчей
    private fun applyFilters() {
        viewModelScope.launch {
            println("Начинаем применение фильтров")
            
            // Выполняем фильтрацию в фоновом потоке
            val filtered = withContext(Dispatchers.Default) {
                val currentMatches = _matches.value
                println("Количество матчей для фильтрации: ${currentMatches.size}")
                
                val status = _matchStatus.value
                val leagueValue = _league.value
                val team = _teamFilter.value
                
                println("Применяем фильтры: статус=$status, лига=$leagueValue, команда=$team")
                
                val result = currentMatches.filter { match ->
                    // Фильтр по статусу матча
                    val statusMatch = if (status == "ALL") true else match.status == status
                    
                    // Фильтр по лиге (предполагаем, что у нас есть доступ к лиге через API)
                    // Так как в модели нет прямого поля для лиги, это условный фильтр
                    val leagueMatch = leagueValue == "ALL"
                    
                    // Фильтр по команде
                    val teamMatch = if (team.isEmpty()) true else 
                        match.homeTeam.name.contains(team, ignoreCase = true) || 
                        match.awayTeam.name.contains(team, ignoreCase = true)
                    
                    statusMatch && leagueMatch && teamMatch
                }
                
                println("После фильтрации осталось матчей: ${result.size}")
                result
            }
            
            println("Устанавливаем отфильтрованные матчи: ${filtered.size}")
            _filteredMatches.value = filtered
        }
    }
}