package com.example.snakegamefrontend.service

import com.example.snakegamefrontend.model.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
 * Service class for handling score-related API calls
 */
class ScoreService {
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }
    
    private val baseUrl = "https://vscode-internal-498-beta.beta01.cloud.kavia.ai:3001"
    
    // PUBLIC_INTERFACE
    /**
     * Submit a new score to the backend
     */
    suspend fun submitScore(playerName: String, score: Int, level: Int): Result<Score> {
        return try {
            val submission = ScoreSubmission(playerName, score, level)
            val response: ApiResponse<Score> = client.post("$baseUrl/api/scores") {
                contentType(ContentType.Application.Json)
                setBody(submission)
            }.body()
            
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Failed to submit score"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // PUBLIC_INTERFACE
    /**
     * Get the leaderboard from the backend
     */
    suspend fun getLeaderboard(limit: Int = 10): Result<LeaderboardResponse> {
        return try {
            val response: ApiResponse<LeaderboardResponse> = client.get("$baseUrl/api/scores/leaderboard") {
                parameter("limit", limit)
            }.body()
            
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Failed to get leaderboard"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // PUBLIC_INTERFACE
    /**
     * Get the highest score from the backend
     */
    suspend fun getHighScore(): Result<Score?> {
        return try {
            val response: ApiResponse<Score?> = client.get("$baseUrl/api/scores/high").body()
            
            if (response.success) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Failed to get high score"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // PUBLIC_INTERFACE
    /**
     * Get scores for a specific player
     */
    suspend fun getPlayerScores(playerName: String): Result<List<Score>> {
        return try {
            val response: ApiResponse<List<Score>> = client.get("$baseUrl/api/scores/player/$playerName").body()
            
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Failed to get player scores"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // PUBLIC_INTERFACE
    /**
     * Close the HTTP client
     */
    fun close() {
        client.close()
    }
}
