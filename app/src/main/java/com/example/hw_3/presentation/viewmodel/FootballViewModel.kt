package com.example.hw_3.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hw_3.data.Match
import com.example.hw_3.domain.usecase.GetMatchesUseCase
import com.example.hw_3.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FootballViewModel @Inject constructor(
    private val getMatchesUseCase: GetMatchesUseCase
) : ViewModel() {

    private val _matches = MutableStateFlow<List<Match>>(emptyList())
    val matches: StateFlow<List<Match>> = _matches.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadMatches()
    }

    fun loadMatches(status: String? = null, limit: Int? = null, dateFrom: String? = null, dateTo: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            _errorMessage.value = null
            when (val result = getMatchesUseCase(status, limit, dateFrom, dateTo)) {
                is NetworkResult.Success -> _matches.value = result.data ?: emptyList()
                is NetworkResult.Error -> {
                    _errorMessage.value = result.message
                    _matches.value = emptyList()
                }
                else -> {
                    _errorMessage.value = "Неизвестная ошибка"
                    _matches.value = emptyList()
                }
            }
            _isLoading.value = false
        }
    }

    fun refresh() {
        val today = java.time.LocalDate.now()
        val tenDaysAgo = today.minusDays(10)
        loadMatches(
            dateFrom = tenDaysAgo.toString(),
            dateTo = today.toString()
        )
    }
}
