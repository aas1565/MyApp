package com.example.hw_3.data.api

import com.example.hw_3.data.MatchesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface FootballApiService {


    @GET("matches")
    @Headers("Accept: application/json")
    suspend fun getMatches(
        @Query("status") status: String? = null,
        @Query("limit") limit: Int? = null,
        @Query("dateFrom") dateFrom: String? = null,
        @Query("dateTo") dateTo: String? = null
    ): Response<MatchesResponse>


    @GET("competitions/{compId}/matches")
    suspend fun getMatchesByCompetition(
        @Path("compId") compId: String,
        @Query("status") status: String? = null
    ): Response<MatchesResponse>
}
