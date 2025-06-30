package com.example.snakegamefrontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.snakegamefrontend.game.SnakeGame
import com.example.snakegamefrontend.model.GameState
import com.example.snakegamefrontend.model.Score
import com.example.snakegamefrontend.service.ScoreService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * ViewModel for managing the snake game state and backend interactions
 */
class GameViewModel : ViewModel() {
    private val game = SnakeGame()
    private val scoreService = ScoreService()
    
    private var gameLoopJob: Job? = null
    private val playerName = "Player" // In a real app, this would come from user input
    
    private val _leaderboard = MutableStateFlow<List<Score>>(emptyList())
    val leaderboard: StateFlow<List<Score>> = _leaderboard.asStateFlow()
    
    private val _isLoadingScores = MutableStateFlow(false)
    val isLoadingScores: StateFlow<Boolean> = _isLoadingScores.asStateFlow()
    
    // Expose game state
    val snake = game.snake
    val food = game.food
    val score = game.score
    val gameState = game.gameState
    val level = game.level
    val highScore = game.highScore
    
    init {
        // Load initial high score from backend
        loadHighScore()
        
        // Start game loop when game state changes to playing
        viewModelScope.launch {
            gameState.collect { state ->
                when (state) {
                    GameState.PLAYING -> startGameLoop()
                    GameState.GAME_OVER -> {
                        stopGameLoop()
                        submitScore()
                    }
                    else -> stopGameLoop()
                }
            }
        }
    }
    
    // PUBLIC_INTERFACE
    /**
     * Start a new game
     */
    fun startGame() {
        game.startGame()
    }
    
    // PUBLIC_INTERFACE
    /**
     * Pause the current game
     */
    fun pauseGame() {
        game.pauseGame()
    }
    
    // PUBLIC_INTERFACE
    /**
     * Resume the paused game
     */
    fun resumeGame() {
        game.resumeGame()
    }
    
    // PUBLIC_INTERFACE
    /**
     * Change snake direction
     */
    fun changeDirection(direction: com.example.snakegamefrontend.model.Direction) {
        game.changeDirection(direction)
    }
    
    // PUBLIC_INTERFACE
    /**
     * Load leaderboard from backend
     */
    fun loadLeaderboard() {
        viewModelScope.launch {
            _isLoadingScores.value = true
            try {
                val result = scoreService.getLeaderboard(10)
                result.onSuccess { leaderboardResponse ->
                    _leaderboard.value = leaderboardResponse.scores
                }.onFailure { error ->
                    // Handle error silently for now
                    println("Failed to load leaderboard: ${error.message}")
                }
            } finally {
                _isLoadingScores.value = false
            }
        }
    }
    
    private fun startGameLoop() {
        stopGameLoop()
        gameLoopJob = viewModelScope.launch {
            while (gameState.value == GameState.PLAYING) {
                game.update()
                delay(game.getGameSpeed())
            }
        }
    }
    
    private fun stopGameLoop() {
        gameLoopJob?.cancel()
        gameLoopJob = null
    }
    
    private fun loadHighScore() {
        viewModelScope.launch {
            try {
                val result = scoreService.getHighScore()
                result.onSuccess { highScoreData ->
                    highScoreData?.let { 
                        game.setHighScore(it.score)
                    }
                }
            } catch (e: Exception) {
                // Handle error silently
                println("Failed to load high score: ${e.message}")
            }
        }
    }
    
    private fun submitScore() {
        viewModelScope.launch {
            try {
                val currentScore = score.value
                val currentLevel = level.value
                
                if (currentScore > 0) {
                    val result = scoreService.submitScore(playerName, currentScore, currentLevel)
                    result.onSuccess {
                        // Score submitted successfully
                        loadLeaderboard() // Refresh leaderboard
                    }.onFailure { error ->
                        // Handle error silently
                        println("Failed to submit score: ${error.message}")
                    }
                }
            } catch (e: Exception) {
                println("Error submitting score: ${e.message}")
            }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        stopGameLoop()
        scoreService.close()
    }
}
