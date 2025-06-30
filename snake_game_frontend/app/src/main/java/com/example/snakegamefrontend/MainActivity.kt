package com.example.snakegamefrontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.snakegamefrontend.ui.GameScreen
import com.example.snakegamefrontend.ui.theme.SnakeGameTheme
import com.example.snakegamefrontend.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SnakeGameTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SnakeGameApp()
                }
            }
        }
    }
}

@Composable
// PUBLIC_INTERFACE
/**
 * Main composable for the Snake Game application
 */
fun SnakeGameApp() {
    val gameViewModel: GameViewModel = viewModel()
    
    GameScreen(
        game = gameViewModel,
        modifier = Modifier.fillMaxSize()
    )
}
