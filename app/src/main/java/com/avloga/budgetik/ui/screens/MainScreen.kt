package com.avloga.budgetik.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.avloga.budgetik.R
import com.avloga.budgetik.data.firebase.FirebaseFirestoreManager
import com.avloga.budgetik.data.model.Expense
import com.avloga.budgetik.ui.components.*
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    navController: NavController,
    userId: String
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val expenses by FirebaseFirestoreManager.getExpensesFlow().collectAsState(initial = emptyList())

    var showDialog by remember { mutableStateOf(false) }

    val (name, _, avatarRes) = when (userId.lowercase()) {
        "pasha" -> Triple("Паша", "9 500 ₴", R.drawable.pasha_avatar)
        "tanya" -> Triple("Таня", "12 300 ₴", R.drawable.tanya_avatar)
        else -> Triple("Користувач", "0 ₴", R.drawable.default_avatar)
    }

    val allExpenses = expenses.sortedByDescending { it.date + it.time }
    val recentExpenses = allExpenses.take(5)

    val expensesOutcome = allExpenses.filter { it.type == "outcome" }
    val categoryStats = expensesOutcome.groupBy { it.category ?: "Інше" }
        .mapValues { entry -> entry.value.sumOf { it.amount } }

    val totalBalance = allExpenses.sumOf { if (it.type == "income") it.amount else -it.amount }
    val balanceText = "${totalBalance.toInt()} ₴"

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(top = 24.dp)
            ) {
                UserHeader(
                    name = name,
                    balance = balanceText,
                    avatarRes = avatarRes
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("+ Додати", color = Color.White, style = MaterialTheme.typography.titleMedium)
                }

                Spacer(modifier = Modifier.height(24.dp))

                SectionTitleWithAction(
                    title = "Останні операції",
                    actionText = "Усі >",
                    onActionClick = {
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            "expenses",
                            allExpenses
                        )
                        navController.navigate("all_expenses")
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column {
                    recentExpenses.forEach { expense ->
                        ExpenseRow(expense = expense)
                        Divider(color = Color.LightGray, thickness = 1.dp)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                SectionTitle("Статистика за категоріями")
                Spacer(modifier = Modifier.height(8.dp))

                if (categoryStats.isEmpty()) {
                    Text("Немає витрат для статистики.", color = Color.Gray)
                } else {
                    categoryStats.forEach { (category, amount) ->
                        CategoryRow(
                            name = category,
                            amount = "${amount.toInt()} ₴"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            if (showDialog) {
                AddExpenseDialog(
                    userId = name,
                    onDismiss = { showDialog = false },
                    onSubmit = { expense ->
                        scope.launch {
                            FirebaseFirestoreManager.addExpense(
                                expense,
                                onSuccess = {
                                    Toast.makeText(context, "Операцію додано", Toast.LENGTH_SHORT).show()
                                    showDialog = false
                                },
                                onFailure = {
                                    Toast.makeText(context, "Помилка збереження", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SectionTitleWithAction(title: String, actionText: String, onActionClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Text(
            text = actionText,
            style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF2E7D32)),
            modifier = Modifier.clickable { onActionClick() }
        )
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )
}
