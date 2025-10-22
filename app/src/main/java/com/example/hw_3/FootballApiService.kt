package com.example.hw_3.api

import com.example.hw_3.data.MatchesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface FootballApiService {
    @GET("matches")
    @Headers("X-Auth-Token: 5f5e8b7e1e5f4e4b9b5e8b7e1e5f4e4b")
    suspend fun getMatches(): Response<MatchesResponse>
}