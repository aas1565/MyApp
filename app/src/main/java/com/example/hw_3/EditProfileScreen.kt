@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.hw_3


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
// Заменяем иконку часов на стандартную, чтобы избежать зависимости extended
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavHostController) {
    val context = LocalContext.current
    val profileRepository = remember { ProfileRepository(context) }

    // Используем derivedStateOf для оптимизации
    var profileState by remember {
        mutableStateOf(profileRepository.getProfile())
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            profileState = profileState.copy(avatarUri = it.toString())
        }
    }

    var showImageSourceDialog by remember { mutableStateOf(false) }

    val timeRegex = remember { Regex("^([01]\\d|2[0-3]):([0-5]\\d)$") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Редактирование профиля") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    val isTimeValid = remember(profileState.favoritePairTime) {
                        profileState.favoritePairTime.isNotBlank() && timeRegex.matches(profileState.favoritePairTime)
                    }
                    TextButton(
                        onClick = {
                            profileRepository.saveProfile(profileState)
                            // Планируем уведомление на выбранное время
                            // Если на Android 12+ нет права на точные будильники, предложим открыть настройки
                            val alarmManager = context.getSystemService(android.content.Context.ALARM_SERVICE) as android.app.AlarmManager
                            val canExact = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                                alarmManager.canScheduleExactAlarms()
                            } else true
                            if (!canExact) {
                                // Открыть настройки точных будильников
                                val intent = android.content.Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                                intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(intent)
                            } else {
                                scheduleFavoritePairAlarm(
                                    context = context,
                                    name = profileState.fullName,
                                    time = profileState.favoritePairTime
                                )
                            }
                            navController.popBackStack()
                        },
                        enabled = isTimeValid
                    ) {
                        Text("Готово")
                    }
                }
            )
        }
    ) { paddingValues ->
        EditProfileContent(
            profileState = profileState,
            onProfileChange = { profileState = it },
            onShowImageDialog = { showImageSourceDialog = true },
            paddingValues = paddingValues
        )

        if (showImageSourceDialog) {
            ImageSourceDialog(
                onDismiss = { showImageSourceDialog = false },
                onGallerySelected = {
                    showImageSourceDialog = false
                    galleryLauncher.launch("image/*")
                },
                onCameraSelected = { showImageSourceDialog = false }
            )
        }
    }
}

// Выносим контент в отдельную функцию
@Composable
private fun EditProfileContent(
    profileState: Profile,
    onProfileChange: (Profile) -> Unit,
    onShowImageDialog: () -> Unit,
    paddingValues: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally // Выравнивание для всего Column
    ) {
        EditableAvatar(
            avatarUri = profileState.avatarUri,
            onEditClick = onShowImageDialog
        )

        Spacer(modifier = Modifier.height(32.dp))

        ProfileForm(profileState, onProfileChange)
    }
}

@Composable
private fun EditableAvatar(avatarUri: String, onEditClick: () -> Unit) {
    // Убрали .align(Alignment.CenterHorizontally) так как Column уже имеет horizontalAlignment
    Box(modifier = Modifier.wrapContentSize()) {

        // Встроенная реализация AvatarImage вместо вызова приватной функции
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
                Text("Добавить фото")
            }
        }

        FloatingActionButton(
            onClick = onEditClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(36.dp)
        ) {
            Icon(Icons.Default.Edit, contentDescription = "Изменить фото")
        }
    }
}

@Composable
private fun ProfileForm(profileState: Profile, onProfileChange: (Profile) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        listOf(
            "ФИО" to profileState.fullName,
            "Должность" to profileState.position,
            "Email" to profileState.email,
            "Ссылка на резюме" to profileState.resumeUrl,
            "Время любимой пары" to profileState.favoritePairTime
        ).forEachIndexed { index, (label, value) ->
            if (index > 0) {
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (label == "Время любимой пары") {
                val timeRegex = remember { Regex("^([01]\\d|2[0-3]):([0-5]\\d)$") }
                val isError = value.isNotBlank() && !timeRegex.matches(value)
                var showTimePicker by remember { mutableStateOf(false) }
                OutlinedTextField(
                    value = value,
                    onValueChange = { newValue ->
                        onProfileChange(profileState.copy(favoritePairTime = newValue))
                    },
                    label = { Text(label) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = !value.isNotBlank() || isError,
                    trailingIcon = {
                        IconButton(onClick = { showTimePicker = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Выбрать время")
                        }
                    },
                    supportingText = {
                        if (!value.isNotBlank()) {
                            Text("Укажите время в формате HH:mm")
                        } else if (isError) {
                            Text("Неверный формат. Пример: 09:30")
                        }
                    }
                )

                if (showTimePicker) {
                    val initialHour = value.takeIf { timeRegex.matches(it) }?.substring(0,2)?.toIntOrNull() ?: 8
                    val initialMinute = value.takeIf { timeRegex.matches(it) }?.substring(3,5)?.toIntOrNull() ?: 0
                    val timePickerState = remember(initialHour, initialMinute) {
                        androidx.compose.material3.TimePickerState(
                            initialHour = initialHour,
                            initialMinute = initialMinute,
                            is24Hour = true
                        )
                    }
                    AlertDialog(
                        onDismissRequest = { showTimePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                val hh = timePickerState.hour.toString().padStart(2, '0')
                                val mm = timePickerState.minute.toString().padStart(2, '0')
                                onProfileChange(profileState.copy(favoritePairTime = "$hh:$mm"))
                                showTimePicker = false
                            }) { Text("Выбрать") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showTimePicker = false }) { Text("Отмена") }
                        },
                        text = {
                            androidx.compose.material3.TimePicker(state = timePickerState)
                        }
                    )
                }
            } else {
                OutlinedTextField(
                    value = value,
                    onValueChange = { newValue ->
                        val updatedProfile = when (label) {
                            "ФИО" -> profileState.copy(fullName = newValue)
                            "Должность" -> profileState.copy(position = newValue)
                            "Email" -> profileState.copy(email = newValue)
                            "Ссылка на резюме" -> profileState.copy(resumeUrl = newValue)
                            else -> profileState
                        }
                        onProfileChange(updatedProfile)
                    },
                    label = { Text(label) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = label != "Ссылка на резюме"
                )
            }
        }
    }
}

@Composable
private fun ImageSourceDialog(
    onDismiss: () -> Unit,
    onGallerySelected: () -> Unit,
    onCameraSelected: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Выберите источник") },
        text = { Text("Откуда хотите загрузить фото?") },
        confirmButton = {
            Button(onClick = onGallerySelected) {
                Text("Галерея")
            }
        },
        dismissButton = {
            TextButton(onClick = onCameraSelected) {
                Text("Камера")
            }
        }
    )
}