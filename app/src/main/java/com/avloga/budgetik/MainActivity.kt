package com.avloga.budgetik

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.avloga.budgetik.ui.screens.LoginScreen
import com.avloga.budgetik.ui.screens.RegisterScreen
import com.avloga.budgetik.ui.screens.MainScreen
import com.avloga.budgetik.ui.theme.BudgetikTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BudgetikTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "Login") {
                    composable("Login") {
                        LoginScreen(navController = navController)
                    }
                    composable("Register") {
                        RegisterScreen(navController)
                    }
                    composable("MainScreen") {
                        MainScreen(navController = navController)
                    }
                    // додай інші екрани, якщо потрібно
                }
            }
        }
    }
}
