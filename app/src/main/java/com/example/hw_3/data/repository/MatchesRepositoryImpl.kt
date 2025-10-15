package com.example.hw_3.data.repository

import com.example.hw_3.data.Match
import com.example.hw_3.data.api.RetrofitClient
import com.example.hw_3.domain.repository.MatchesRepository
import com.example.hw_3.util.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MatchesRepositoryImpl @Inject constructor(
    private val retrofitClient: RetrofitClient
) : MatchesRepository {

    private val api = retrofitClient.apiService

    override suspend fun fetchMatches(
        status: String?, 
        limit: Int?, 
        dateFrom: String?, 
        dateTo: String?
    ): NetworkResult<List<Match>> {
        return withContext(Dispatchers.IO) {
            try {
                println("🌐 Запрос: https://api.football-data.org/v4/matches?status=${status ?: "ALL"}")
                val resp = api.getMatches(status = status, limit = limit, dateFrom = dateFrom, dateTo = dateTo)

                println("🔗 URL: ${resp.raw().request.url}")
                println("✅ Код: ${resp.code()} | Успешно: ${resp.isSuccessful}")

                if (resp.isSuccessful) {
                    val matches = resp.body()?.matches ?: emptyList()
                    println("📦 Тело ответа: ${resp.body()}")
                    println("✅ Получено матчей: ${matches.size}")

                    NetworkResult.Success(matches)
                } else {
                    val error = resp.errorBody()?.string()
                    println("⚠️ Ошибка тела ответа: $error")
                    NetworkResult.Error("HTTP ${resp.code()} ${resp.message()}")
                }
            } catch (e: IOException) {
                println("❌ Network error: ${e.message}")
                NetworkResult.Error("Network error: ${e.localizedMessage ?: e.message}")
            } catch (e: HttpException) {
                println("❌ HTTP exception: ${e.message()}")
                NetworkResult.Error("HTTP exception: ${e.code()} ${e.message()}")
            } catch (t: Throwable) {
                println("💥 Unexpected error: ${t.localizedMessage ?: t.message}")
                NetworkResult.Error("Unexpected error: ${t.localizedMessage ?: t.message}")
            }
        }
    }

    override suspend fun fetchMatchesByCompetition(
        compId: String,
        status: String?
    ): NetworkResult<List<Match>> {
        return withContext(Dispatchers.IO) {
            try {
                val resp = api.getMatchesByCompetition(compId = compId, status = status)
                if (resp.isSuccessful) {
                    NetworkResult.Success(resp.body()?.matches ?: emptyList())
                } else {
                    NetworkResult.Error("HTTP ${resp.code()} ${resp.message()}")
                }
            } catch (e: IOException) {
                NetworkResult.Error("Network error: ${e.localizedMessage ?: e.message}")
            } catch (e: HttpException) {
                NetworkResult.Error("HTTP exception: ${e.code()} ${e.message()}")
            } catch (t: Throwable) {
                NetworkResult.Error("Unexpected error: ${t.localizedMessage ?: t.message}")
            }
        }
    }
}
