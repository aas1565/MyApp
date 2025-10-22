package com.example.hw_3.favorites

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.hw_3.data.Match
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: FavoritesRepository

    private val _favorites = MutableStateFlow<List<FavoriteMatchEntity>>(emptyList())
    val favorites: StateFlow<List<FavoriteMatchEntity>> = _favorites.asStateFlow()

    init {
        val db = FavoritesDatabase.getInstance(application)
        repository = FavoritesRepository(db.favoriteDao())

        viewModelScope.launch {
            repository.getFavorites().collectLatest { list ->
                _favorites.value = list
            }
        }
    }

    fun addToFavorites(match: Match) {
        viewModelScope.launch {
            val entity = FavoriteMatchEntity(
                matchId = match.id,
                homeTeamName = match.homeTeam.name,
                awayTeamName = match.awayTeam.name,
                date = match.date,
                status = match.status,
                homeScore = match.score?.fullTime?.home,
                awayScore = match.score?.fullTime?.away
            )
            repository.addFavorite(entity)
        }
    }

    fun removeFromFavorites(matchId: Int) {
        viewModelScope.launch {
            repository.removeFavorite(matchId)
        }
    }

    fun isFavoriteFlow(matchId: Int): StateFlow<Boolean> {
        val state = MutableStateFlow(false)
        viewModelScope.launch {
            repository.isFavorite(matchId).collectLatest { state.value = it }
        }
        return state
    }
}