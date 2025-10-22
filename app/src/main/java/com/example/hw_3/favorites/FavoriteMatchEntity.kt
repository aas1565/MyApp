package com.example.hw_3.favorites

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_matches")
data class FavoriteMatchEntity(
    @PrimaryKey val matchId: Int,
    val homeTeamName: String,
    val awayTeamName: String,
    val date: String,
    val status: String,
    val homeScore: Int?,
    val awayScore: Int?
)