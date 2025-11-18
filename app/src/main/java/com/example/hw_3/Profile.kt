// Profile.kt
package com.example.hw_3

data class Profile(
    val fullName: String = "",
    val avatarUri: String = "",
    val resumeUrl: String = "",
    val position: String = "",
    val email: String = "",
    val favoritePairTime: String = "" // Формат HH:mm
)