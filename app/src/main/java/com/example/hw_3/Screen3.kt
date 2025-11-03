// Screen3.kt
package com.example.hw_3

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest

@Composable
fun Screen3(navController: NavHostController) {
    val context = LocalContext.current
    val profileRepository = remember { ProfileRepository(context) }

    // Кэшируем получение профиля
    val profile by remember {
        derivedStateOf { profileRepository.getProfile() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header с кнопкой редактирования
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Профиль",
                style = MaterialTheme.typography.headlineMedium
            )
            IconButton(
                onClick = {
                    navController.navigate("editProfile")
                }
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Редактировать")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Оптимизированная загрузка аватара
        AvatarImage(avatarUri = profile.avatarUri)

        Spacer(modifier = Modifier.height(24.dp))

        // Статический список полей
        ProfileInfo(profile)

        Spacer(modifier = Modifier.height(32.dp))

        // Кнопка для резюме
        ResumeButton(profile.resumeUrl, context)
    }
}

// Выносим тяжелые компоненты в отдельные функции
@Composable
private fun AvatarImage(avatarUri: String) {
    if (avatarUri.isNotEmpty()) {
        Image(
            painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(avatarUri)
                    .crossfade(true)
                    .build()
            ),
            contentDescription = "Аватар",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text("Нет фото")
        }
    }
}

@Composable
private fun ProfileInfo(profile: Profile) {
    Column(modifier = Modifier.fillMaxWidth()) {
        ProfileField(label = "ФИО", value = profile.fullName)
        ProfileField(label = "Должность", value = profile.position)
        ProfileField(label = "Email", value = profile.email)
    }
}

// ДОБАВЬТЕ ЭТОТ КОМПОНЕНТ - он отсутствовал
@Composable
private fun ProfileField(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = if (value.isNotEmpty()) value else "Не указано",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun ResumeButton(resumeUrl: String, context: Context) {
    if (resumeUrl.isNotEmpty()) {
        Button(
            onClick = {
                downloadAndShowResume(context, resumeUrl)
            },
            enabled = resumeUrl.isNotEmpty()
        ) {
            Text("Скачать резюме")
        }
    }
}