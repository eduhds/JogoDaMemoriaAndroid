package com.eduhds.jogodamemoria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.eduhds.jogodamemoria.model.GameCollection
import com.eduhds.jogodamemoria.ui.screens.GameScreen
import com.eduhds.jogodamemoria.ui.screens.HomeScreen
import com.eduhds.jogodamemoria.ui.theme.JogoDaMemoriaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JogoDaMemoriaTheme {
                MemoryGameApp()
            }
        }
    }
}

@Composable
fun MemoryGameApp() {
    val navController = rememberNavController()
    var selectedGame by remember { mutableStateOf<GameCollection?>(null) }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(onStartGame = { game ->
                selectedGame = game
                navController.navigate("game")
            })
        }
        composable("game") {
            GameScreen(
                game = selectedGame,
                onNavigateBack = {
                    navController.popBackStack()
                })
        }
    }
}
