package com.example.hw_3.filter

import android.content.Context
import androidx.compose.runtime.compositionLocalOf
import com.example.hw_3.FilterPreferences

/**
 * Провайдер для внедрения зависимости FilterBadgeState
 */
object FilterBadgeProvider {
    // Создание экземпляра FilterBadgeState
    fun provideFilterBadgeState(): FilterBadgeState {
        return FilterBadgeState.getInstance()
    }
}

// CompositionLocal для доступа к FilterBadgeState из Composable функций
val LocalFilterBadgeState = compositionLocalOf { FilterBadgeState.getInstance() }