package com.example.hw_3.favorites

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite_matches ORDER BY date DESC")
    fun getFavorites(): Flow<List<FavoriteMatchEntity>>

    @Query("SELECT COUNT(*) FROM favorite_matches WHERE matchId = :matchId")
    fun isFavoriteCount(matchId: Int): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(match: FavoriteMatchEntity)

    @Query("DELETE FROM favorite_matches WHERE matchId = :matchId")
    suspend fun deleteFavorite(matchId: Int)
}