package com.example.snakegamefrontend.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snakegamefrontend.game.SnakeGame
import com.example.snakegamefrontend.model.Direction
import com.example.snakegamefrontend.model.GameState
import com.example.snakegamefrontend.model.Position

@Composable
// PUBLIC_INTERFACE
/**
 * Main game screen composable that displays the snake game
 */
fun GameScreen(
    game: com.example.snakegamefrontend.viewmodel.GameViewModel,
    modifier: Modifier = Modifier
) {
    val snake by game.snake.collectAsState()
    val food by game.food.collectAsState()
    val score by game.score.collectAsState()
    val gameState by game.gameState.collectAsState()
    val level by game.level.collectAsState()
    val highScore by game.highScore.collectAsState()
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF23272B))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Status bar
        StatusBar(
            score = score,
            highScore = highScore,
            level = level,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Game board
        GameBoard(
            snake = snake,
            food = food,
            modifier = Modifier
                .size(320.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(2.dp, Color(0xFF21C229), RoundedCornerShape(8.dp))
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Game state overlay
        when (gameState) {
            GameState.IDLE -> {
                StartGameButton(onStartClick = { game.startGame() })
            }
            GameState.PAUSED -> {
                PauseOverlay(onResumeClick = { game.resumeGame() })
            }
            GameState.GAME_OVER -> {
                GameOverOverlay(
                    score = score,
                    onRestartClick = { game.startGame() }
                )
            }
            GameState.PLAYING -> {
                // Show pause button during gameplay
                Button(
                    onClick = { game.pauseGame() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE4DE14)
                    )
                ) {
                    Text("Pause", color = Color.Black)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Direction controls
        if (gameState == GameState.PLAYING) {
            DirectionControls(
                onDirectionChange = { direction -> game.changeDirection(direction) }
            )
        }
    }
}

@Composable
private fun StatusBar(
    score: Int,
    highScore: Int,
    level: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF21C229).copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Score",
                    color = Color.White,
                    fontSize = 12.sp
                )
                Text(
                    text = score.toString(),
                    color = Color(0xFF21C229),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Column {
                Text(
                    text = "High Score",
                    color = Color.White,
                    fontSize = 12.sp
                )
                Text(
                    text = highScore.toString(),
                    color = Color(0xFFE4DE14),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Column {
                Text(
                    text = "Level",
                    color = Color.White,
                    fontSize = 12.sp
                )
                Text(
                    text = level.toString(),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun GameBoard(
    snake: List<Position>,
    food: com.example.snakegamefrontend.model.Food,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    
    Canvas(
        modifier = modifier.background(Color.Black)
    ) {
        val cellSize = size.width / 20f // 20x20 grid
        
        // Draw snake
        snake.forEachIndexed { index, position ->
            val color = if (index == 0) Color(0xFF21C229) else Color(0xFF21C229).copy(alpha = 0.7f)
            drawRect(
                color = color,
                topLeft = Offset(
                    position.x * cellSize,
                    position.y * cellSize
                ),
                size = Size(cellSize - 1, cellSize - 1)
            )
        }
        
        // Draw food
        drawRect(
            color = Color(0xFFE4DE14),
            topLeft = Offset(
                food.position.x * cellSize,
                food.position.y * cellSize
            ),
            size = Size(cellSize - 1, cellSize - 1)
        )
    }
}

@Composable
private fun StartGameButton(onStartClick: () -> Unit) {
    Button(
        onClick = onStartClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF21C229)
        ),
        modifier = Modifier.size(width = 200.dp, height = 50.dp)
    ) {
        Text(
            text = "Start Game",
            color = Color.Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun PauseOverlay(onResumeClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.8f)
        ),
        modifier = Modifier.padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Game Paused",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onResumeClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF21C229)
                )
            ) {
                Text("Resume", color = Color.Black)
            }
        }
    }
}

@Composable
private fun GameOverOverlay(
    score: Int,
    onRestartClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.8f)
        ),
        modifier = Modifier.padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Game Over",
                color = Color(0xFFE4DE14),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Final Score: $score",
                color = Color.White,
                fontSize = 18.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onRestartClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF21C229)
                )
            ) {
                Text("Play Again", color = Color.Black)
            }
        }
    }
}

@Composable
private fun DirectionControls(
    onDirectionChange: (Direction) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Up button
        Button(
            onClick = { onDirectionChange(Direction.UP) },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF21C229).copy(alpha = 0.7f)
            ),
            modifier = Modifier.size(60.dp)
        ) {
            Text("↑", fontSize = 24.sp, color = Color.Black)
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Left and Right buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { onDirectionChange(Direction.LEFT) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF21C229).copy(alpha = 0.7f)
                ),
                modifier = Modifier.size(60.dp)
            ) {
                Text("←", fontSize = 24.sp, color = Color.Black)
            }
            
            Button(
                onClick = { onDirectionChange(Direction.RIGHT) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF21C229).copy(alpha = 0.7f)
                ),
                modifier = Modifier.size(60.dp)
            ) {
                Text("→", fontSize = 24.sp, color = Color.Black)
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Down button
        Button(
            onClick = { onDirectionChange(Direction.DOWN) },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF21C229).copy(alpha = 0.7f)
            ),
            modifier = Modifier.size(60.dp)
        ) {
            Text("↓", fontSize = 24.sp, color = Color.Black)
        }
    }
}
