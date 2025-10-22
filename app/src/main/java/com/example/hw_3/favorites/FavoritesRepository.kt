package com.example.hw_3.favorites

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesRepository(private val dao: FavoriteDao) {
    fun getFavorites(): Flow<List<FavoriteMatchEntity>> = dao.getFavorites()

    fun isFavorite(matchId: Int): Flow<Boolean> = dao.isFavoriteCount(matchId).map { it > 0 }

    suspend fun addFavorite(entity: FavoriteMatchEntity) = dao.insertFavorite(entity)

    suspend fun removeFavorite(matchId: Int) = dao.deleteFavorite(matchId)
}