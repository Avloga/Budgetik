package com.avloga.budgetik

import AllExpensesScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.avloga.budgetik.data.model.Expense
import com.avloga.budgetik.ui.screens.MainScreen
import com.avloga.budgetik.ui.theme.BudgetikTheme
import com.budgetik.ui.screens.LoginScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BudgetikTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "Login") {
                    composable("Login") {
                        LoginScreen(navController = navController) { userId ->
                            navController.navigate("MainScreen/$userId")
                        }
                    }
                    composable("MainScreen/{userId}") { backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId") ?: "unknown"
                        MainScreen(navController = navController, userId = userId)
                    }
                    composable("all_expenses") {
                        val expenses = navController.previousBackStackEntry?.savedStateHandle?.get<List<Expense>>("expenses") ?: emptyList()
                        AllExpensesScreen(navController = navController, expenses = expenses)
                    }
                }

                // додай інші екрани, якщо потрібно

            }
        }
    }
}
