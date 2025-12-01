package com.example.profile

import android.content.Context

/**
 * Интерфейс для предоставления Activity класса без прямой зависимости от app модуля
 */
interface ProfileActivityProvider {
    fun getMainActivityClass(): Class<*>
}


