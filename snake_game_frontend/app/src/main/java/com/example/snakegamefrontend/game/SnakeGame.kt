package com.example.snakegamefrontend.game

import com.example.snakegamefrontend.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

/**
 * Core game logic for the Snake Game
 */
class SnakeGame(
    private val boardWidth: Int = 20,
    private val boardHeight: Int = 20
) {
    private val _snake = MutableStateFlow(listOf(Position(10, 10)))
    val snake: StateFlow<List<Position>> = _snake.asStateFlow()
    
    private val _direction = MutableStateFlow(Direction.RIGHT)
    val direction: StateFlow<Direction> = _direction.asStateFlow()
    
    private val _food = MutableStateFlow(generateFood())
    val food: StateFlow<Food> = _food.asStateFlow()
    
    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score.asStateFlow()
    
    private val _gameState = MutableStateFlow(GameState.IDLE)
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()
    
    private val _level = MutableStateFlow(1)
    val level: StateFlow<Int> = _level.asStateFlow()
    
    private val _highScore = MutableStateFlow(0)
    val highScore: StateFlow<Int> = _highScore.asStateFlow()
    
    // PUBLIC_INTERFACE
    /**
     * Start a new game
     */
    fun startGame() {
        resetGame()
        _gameState.value = GameState.PLAYING
    }
    
    // PUBLIC_INTERFACE
    /**
     * Pause the current game
     */
    fun pauseGame() {
        if (_gameState.value == GameState.PLAYING) {
            _gameState.value = GameState.PAUSED
        }
    }
    
    // PUBLIC_INTERFACE
    /**
     * Resume the paused game
     */
    fun resumeGame() {
        if (_gameState.value == GameState.PAUSED) {
            _gameState.value = GameState.PLAYING
        }
    }
    
    // PUBLIC_INTERFACE
    /**
     * Change the snake's direction
     */
    fun changeDirection(newDirection: Direction) {
        if (_gameState.value == GameState.PLAYING && 
            newDirection != _direction.value.opposite()) {
            _direction.value = newDirection
        }
    }
    
    // PUBLIC_INTERFACE
    /**
     * Update the game state (called every frame)
     */
    fun update() {
        if (_gameState.value != GameState.PLAYING) return
        
        val currentSnake = _snake.value.toMutableList()
        val head = currentSnake.first()
        val newHead = head + _direction.value.delta
        
        // Check wall collision
        if (isOutOfBounds(newHead)) {
            gameOver()
            return
        }
        
        // Check self collision
        if (currentSnake.contains(newHead)) {
            gameOver()
            return
        }
        
        // Move snake
        currentSnake.add(0, newHead)
        
        // Check food collision
        if (newHead == _food.value.position) {
            eatFood()
        } else {
            // Remove tail if no food eaten
            currentSnake.removeAt(currentSnake.size - 1)
        }
        
        _snake.value = currentSnake
    }
    
    // PUBLIC_INTERFACE
    /**
     * Set the high score
     */
    fun setHighScore(score: Int) {
        if (score > _highScore.value) {
            _highScore.value = score
        }
    }
    
    // PUBLIC_INTERFACE
    /**
     * Get current game speed based on level
     */
    fun getGameSpeed(): Long {
        return (500 - (_level.value - 1) * 50).coerceAtLeast(100).toLong()
    }
    
    private fun resetGame() {
        _snake.value = listOf(Position(10, 10))
        _direction.value = Direction.RIGHT
        _food.value = generateFood()
        _score.value = 0
        _level.value = 1
    }
    
    private fun gameOver() {
        _gameState.value = GameState.GAME_OVER
        if (_score.value > _highScore.value) {
            _highScore.value = _score.value
        }
    }
    
    private fun eatFood() {
        val newScore = _score.value + _food.value.points
        _score.value = newScore
        
        // Increase level every 100 points
        val newLevel = (newScore / 100) + 1
        if (newLevel > _level.value) {
            _level.value = newLevel
        }
        
        _food.value = generateFood()
    }
    
    private fun generateFood(): Food {
        var foodPosition: Position
        do {
            foodPosition = Position(
                Random.nextInt(0, boardWidth),
                Random.nextInt(0, boardHeight)
            )
        } while (_snake.value.contains(foodPosition))
        
        return Food(foodPosition)
    }
    
    private fun isOutOfBounds(position: Position): Boolean {
        return position.x < 0 || position.x >= boardWidth ||
               position.y < 0 || position.y >= boardHeight
    }
}
