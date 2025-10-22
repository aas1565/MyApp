package com.example.hw_3.filter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Компонент для отображения бейджа (оранжевого кружка) рядом с иконкой
 */
@Composable
fun BadgeBox(
    showBadge: Boolean,
    content: @Composable () -> Unit
) {
    Box {
        content()
        
        if (showBadge) {
            Box(
                modifier = Modifier
                    .offset(x = 8.dp, y = (-8).dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF8800)) // Оранжевый цвет
            )
        }
    }
}