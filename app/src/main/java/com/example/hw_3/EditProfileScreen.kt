// EditProfileScreen.kt
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
                    TextButton(onClick = {
                        profileRepository.saveProfile(profileState)
                        navController.popBackStack()
                    }) {
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
            "Ссылка на резюме" to profileState.resumeUrl
        ).forEachIndexed { index, (label, value) ->
            if (index > 0) {
                Spacer(modifier = Modifier.height(16.dp))
            }

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