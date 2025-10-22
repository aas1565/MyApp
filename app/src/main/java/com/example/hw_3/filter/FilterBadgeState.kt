package com.example.hw_3.filter

import androidx.compose.runtime.mutableStateOf
import com.example.hw_3.FilterPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Класс-кэш для хранения состояния бейджа фильтров.
 * Используется как общий класс для экрана списка и настроек фильтров.
 */
class FilterBadgeState {
    // Состояние бейджа - показывать или нет
    private val _showBadge = mutableStateOf(false)
    val showBadge get() = _showBadge.value
    
    // Обновление состояния бейджа на основе текущих фильтров
    fun updateBadgeState(
        status: String = FilterPreferences.DEFAULT_STATUS,
        league: String = FilterPreferences.DEFAULT_LEAGUE,
        teamFilter: String = FilterPreferences.DEFAULT_TEAM
    ) {
        // Показываем бейдж, если хотя бы один фильтр не в дефолтном состоянии
        _showBadge.value = status != FilterPreferences.DEFAULT_STATUS ||
                league != FilterPreferences.DEFAULT_LEAGUE ||
                teamFilter.isNotEmpty()
    }
    
    // Сброс состояния бейджа
    fun resetBadgeState() {
        _showBadge.value = false
    }
    
    companion object {
        // Синглтон для доступа к состоянию бейджа
        @Volatile
        private var INSTANCE: FilterBadgeState? = null
        
        fun getInstance(): FilterBadgeState {
            return INSTANCE ?: synchronized(this) {
                val instance = FilterBadgeState()
                INSTANCE = instance
                instance
            }
        }
    }
}