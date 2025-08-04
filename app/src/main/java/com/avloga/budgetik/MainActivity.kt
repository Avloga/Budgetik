package com.avloga.budgetik

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.avloga.budgetik.data.model.Expense
import com.avloga.budgetik.ui.screens.MainScreen
import com.avloga.budgetik.ui.theme.BudgetikTheme
import com.avloga.budgetik.ui.screens.LoginScreen
import com.avloga.budgetik.ui.screens.AllExpensesScreen
import com.avloga.budgetik.ui.animations.slideUpTransition
import com.avloga.budgetik.ui.animations.slideDownTransition
import com.avloga.budgetik.ui.animations.slideUpPopTransition
import com.avloga.budgetik.ui.animations.slideDownPopTransition
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.os.Build
import androidx.core.view.WindowCompat
import android.view.View
import android.view.WindowInsetsController
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowInsetsControllerCompat
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Забезпечуємо відображення контенту від краю до краю
        enableEdgeToEdge()
        
        // Налаштовуємо світлий статус бар для всіх версій Android
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = true

        setContent {
            BudgetikTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController, 
                    startDestination = "Login"
                ) {
                    // Екран входу
                    composable("Login") {
                        LoginScreen(navController = navController) { userId ->
                            navController.navigate("MainScreen/$userId")
                        }
                    }

                    // Головний екран
                    composable(
                        route = "MainScreen/{userId}",
                        arguments = listOf(navArgument("userId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId") ?: "unknown"
                        MainScreen(navController = navController, userId = userId)
                    }

                    // Екран перегляду всіх витрат з анімацією
                    composable(
                        route = "all_expenses",
                        enterTransition = slideUpTransition(),
                        exitTransition = slideDownTransition(),
                        popEnterTransition = slideUpPopTransition(),
                        popExitTransition = slideDownPopTransition()
                    ) {
                        val expenses =
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.get<List<Expense>>("expenses") ?: emptyList()
                        val userId =
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.get<String>("userId") ?: "unknown"

                        AllExpensesScreen(navController = navController, userId = userId)
                    }

                }
            }
        }
    }
}
fun ensureUserLoggedIn(onLoggedIn: () -> Unit) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    if (currentUser == null) {
        auth.signInAnonymously().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onLoggedIn()
            } else {
                // Тут можна показати помилку
            }
        }
    } else {
        onLoggedIn()
    }
}