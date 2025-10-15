package com.example.hw_3.domain.repository

import com.example.hw_3.data.Match
import com.example.hw_3.util.NetworkResult

interface MatchesRepository {
    suspend fun fetchMatches(
        status: String? = null, 
        limit: Int? = null, 
        dateFrom: String? = null, 
        dateTo: String? = null
    ): NetworkResult<List<Match>>
    
    suspend fun fetchMatchesByCompetition(
        compId: String,
        status: String? = null
    ): NetworkResult<List<Match>>
}
