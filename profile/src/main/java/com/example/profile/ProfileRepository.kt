package com.example.profile

import android.content.Context
import com.google.gson.Gson

class ProfileRepository(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("profile_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val PROFILE_KEY = "profile_data"

    // Кэшируем профиль в памяти
    private var cachedProfile: Profile? = null

    fun saveProfile(profile: Profile) {
        val profileJson = gson.toJson(profile)
        sharedPreferences.edit().putString(PROFILE_KEY, profileJson).apply()
        cachedProfile = profile // Обновляем кэш
    }

    fun getProfile(): Profile {
        // Возвращаем из кэша если есть
        return cachedProfile ?: let {
            val profileJson = sharedPreferences.getString(PROFILE_KEY, null)
            val profile = if (profileJson != null) {
                gson.fromJson(profileJson, Profile::class.java)
            } else {
                Profile()
            }
            cachedProfile = profile
            profile
        }
    }
}


