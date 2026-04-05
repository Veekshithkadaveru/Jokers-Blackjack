package app.krafted.jokersblackjack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.krafted.jokersblackjack.navigation.Screen
import app.krafted.jokersblackjack.ui.screens.GameScreen
import app.krafted.jokersblackjack.ui.screens.HomeScreen
import app.krafted.jokersblackjack.ui.screens.LeaderboardScreen
import app.krafted.jokersblackjack.ui.screens.SessionResultScreen
import app.krafted.jokersblackjack.ui.screens.SplashScreen
import app.krafted.jokersblackjack.ui.theme.JokersBlackjackTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JokersBlackjackTheme {
                var splashDone by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    delay(2500)
                    splashDone = true
                }

                if (!splashDone) {
                    SplashScreen(onTimeout = {})
                } else {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = Screen.HOME,
                        enterTransition = { fadeIn(tween(300)) },
                        exitTransition = { fadeOut(tween(300)) },
                        popEnterTransition = { fadeIn(tween(300)) },
                        popExitTransition = { fadeOut(tween(300)) }
                    ) {
                        composable(Screen.SPLASH) {
                            SplashScreen(onTimeout = { splashDone = true })
                        }
                        composable(Screen.HOME) {
                            HomeScreen(
                                onNavigateToGame = { difficulty ->
                                    navController.navigate(Screen.gameRoute(difficulty))
                                }
                            )
                        }
                        composable(Screen.LEADERBOARD) {
                            LeaderboardScreen()
                        }
                        composable(Screen.GAME) { backStackEntry ->
                            val difficulty =
                                backStackEntry.arguments?.getString("difficulty") ?: "EASY"
                            GameScreen(
                                difficulty = difficulty,
                                onNavigateToSessionResult = {
                                    navController.navigate(Screen.sessionResultRoute(difficulty))
                                }
                            )
                        }
                        composable(Screen.SESSION_RESULT) { backStackEntry ->
                            val difficulty =
                                backStackEntry.arguments?.getString("difficulty") ?: "EASY"
                            SessionResultScreen(
                                onNavigateHome = {
                                    navController.navigate(Screen.HOME) {
                                        popUpTo(Screen.HOME) { inclusive = false }
                                    }
                                },
                                onPlayAgain = {
                                    navController.navigate(Screen.gameRoute(difficulty)) {
                                        popUpTo(Screen.HOME)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
