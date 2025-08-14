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
import com.avloga.budgetik.ui.screens.SavingsScreen
import com.avloga.budgetik.ui.animations.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.os.Build
import androidx.core.view.WindowCompat
import android.view.View
import android.view.WindowInsetsController
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowInsetsControllerCompat
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.viewmodel.compose.viewModel
import com.avloga.budgetik.ui.animations.fastSlideUpTransition
import com.avloga.budgetik.ui.components.ExpensesViewModel
import com.avloga.budgetik.ui.components.SavingsViewModel

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
                val sharedViewModel: ExpensesViewModel = viewModel()

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
                        MainScreen(navController = navController, userId = userId, viewModel = sharedViewModel)
                    }

                    // Екран перегляду всіх витрат з анімацією
                    composable(
                        route = "all_expenses/{userId}/{selectedAccount}",
                        arguments = listOf(
                            navArgument("userId") { type = NavType.StringType },
                            navArgument("selectedAccount") { type = NavType.StringType }
                        ),
                        enterTransition = fastSlideUpTransition(),
                        exitTransition = fastSlideDownTransition(),
                        popEnterTransition = fastSlideUpTransition(),
                        popExitTransition = fastSlideDownTransition()
                    ) { backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId") ?: "unknown"
                        val selectedAccount = backStackEntry.arguments?.getString("selectedAccount") ?: "CASH"

                        AllExpensesScreen(
                            navController = navController, 
                            userId = userId,
                            selectedAccountString = selectedAccount,
                            viewModel = sharedViewModel
                        )
                    }

                    // Екран накопичень
                    composable(
                        route = "savings/{userId}",
                        arguments = listOf(
                            navArgument("userId") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId") ?: "unknown"
                        val savingsViewModel: SavingsViewModel = viewModel()
                        SavingsScreen(
                            navController = navController, 
                            userId = userId,
                            viewModel = savingsViewModel
                        )
                    }

                    // Екран деталей банки з передачею всіх даних
                    composable(
                        route = "bank_details/{bankId}/{bankName}/{currentAmount}/{targetAmount}/{withdrawnAmount}",
                        arguments = listOf(
                            navArgument("bankId") { type = NavType.StringType },
                            navArgument("bankName") { type = NavType.StringType },
                            navArgument("currentAmount") { type = NavType.StringType },
                            navArgument("targetAmount") { type = NavType.StringType },
                            navArgument("withdrawnAmount") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val bankId = backStackEntry.arguments?.getString("bankId") ?: "unknown"
                        val bankName = backStackEntry.arguments?.getString("bankName") ?: "Банка"
                        val currentAmount = backStackEntry.arguments?.getString("currentAmount")?.toDoubleOrNull() ?: 0.0
                        val targetAmount = backStackEntry.arguments?.getString("targetAmount")?.toDoubleOrNull() ?: 0.0
                        val withdrawnAmount = backStackEntry.arguments?.getString("withdrawnAmount")?.toDoubleOrNull() ?: 0.0
                        
                        val bank = com.avloga.budgetik.data.model.SavingsBank(
                            id = bankId,
                            name = bankName,
                            targetAmount = targetAmount,
                            currentAmount = currentAmount,
                            withdrawnAmount = withdrawnAmount
                        )
                        
                        com.avloga.budgetik.ui.screens.BankDetailsScreen(
                            navController = navController,
                            bank = bank
                        )
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