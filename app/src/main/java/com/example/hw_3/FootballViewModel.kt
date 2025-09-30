package com.example.hw_3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hw_3.api.RetrofitClient
import com.example.hw_3.data.Match
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FootballViewModel : ViewModel() {
    //приватный изменяемый поток для списка матчей
    private val _matches = MutableStateFlow<List<Match>>(emptyList())
    //преобразует MutableStateFlow в StateFlow, предотвращая изменение извне
    val matches: StateFlow<List<Match>> = _matches.asStateFlow()

    //флаг загрузки (true - идет загрузка, false - завершена)
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    //хранит сообщение об ошибке или null если ошибок нет
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    //публичная функция для инициации загрузки
    fun fetchMatches() {
        _isLoading.value = true
        _error.value = null

        // запускает корутину в scope ViewModel
        viewModelScope.launch {
            try {
                //содержит HTTP ответ от сервера
                val response = RetrofitClient.footballApiService.getMatches()
                if (response.isSuccessful) {
                    //извлекает список матчей из тела ответа
                    _matches.value = response.body()?.matches ?: emptyList()
                } else {
                    _error.value = "Ошибка: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Ошибка сети: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}