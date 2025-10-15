package com.example.hw_3.domain.usecase

import com.example.hw_3.data.Match
import com.example.hw_3.domain.repository.MatchesRepository
import com.example.hw_3.util.NetworkResult
import javax.inject.Inject

class GetMatchesUseCase @Inject constructor(
    private val repository: MatchesRepository
) {
    suspend operator fun invoke(
        status: String? = null, 
        limit: Int? = null, 
        dateFrom: String? = null, 
        dateTo: String? = null
    ): NetworkResult<List<Match>> {
        return repository.fetchMatches(status, limit, dateFrom, dateTo)
    }
}
