package com.avloga.budgetik

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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BudgetikTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "Login") {
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

                    // Екран перегляду всіх витрат
                    composable("all_expenses") {
                        val expenses =
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.get<List<Expense>>("expenses") ?: emptyList()

                        AllExpensesScreen(navController = navController)
                    }

                }
            }
        }
    }
}
