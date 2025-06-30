package com.example.snakegamefrontend.model

import kotlinx.serialization.Serializable

/**
 * Represents a position on the game board
 */
data class Position(
    val x: Int,
    val y: Int
) {
    operator fun plus(other: Position) = Position(x + other.x, y + other.y)
    operator fun minus(other: Position) = Position(x - other.x, y - other.y)
}

/**
 * Represents the direction of movement
 */
enum class Direction(val delta: Position) {
    UP(Position(0, -1)),
    DOWN(Position(0, 1)),
    LEFT(Position(-1, 0)),
    RIGHT(Position(1, 0));
    
    fun opposite(): Direction = when (this) {
        UP -> DOWN
        DOWN -> UP
        LEFT -> RIGHT
        RIGHT -> LEFT
    }
}

/**
 * Represents the current state of the game
 */
enum class GameState {
    IDLE,
    PLAYING,
    PAUSED,
    GAME_OVER
}

/**
 * Represents a food item on the game board
 */
data class Food(
    val position: Position,
    val points: Int = 10
)

/**
 * Backend API models for score submission
 */
@Serializable
data class ScoreSubmission(
    val playerName: String,
    val score: Int,
    val level: Int = 1
)

@Serializable
data class Score(
    val id: String,
    val playerName: String,
    val score: Int,
    val level: Int = 1,
    val timestamp: Long
)

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null
)

@Serializable
data class LeaderboardResponse(
    val scores: List<Score>,
    val totalCount: Int
)
