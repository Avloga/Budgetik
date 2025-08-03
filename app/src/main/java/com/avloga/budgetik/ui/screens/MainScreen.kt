package com.avloga.budgetik.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.avloga.budgetik.R
import com.avloga.budgetik.data.firebase.FirebaseFirestoreManager
import com.avloga.budgetik.data.model.Expense
import com.avloga.budgetik.ui.components.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MainScreen(
    navController: NavController,
    userId: String,
    expenses: List<Expense> = emptyList()  // Передавай сюди реальні дані
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Визначаємо дані користувача залежно від userId
    val (name, balance, avatarRes) = when (userId.lowercase()) {
        "pasha" -> Triple("Паша", "9 500 ₴", R.drawable.pasha_avatar)
        "tanya" -> Triple("Таня", "12 300 ₴", R.drawable.tanya_avatar)
        else -> Triple("Користувач", "0 ₴", R.drawable.default_avatar)
    }

    var showDialog by remember { mutableStateOf(false) }

    // Фільтруємо витрати по поточному користувачу
    val userExpenses = expenses.filter { it.userName == name }

    // Останні витрати — показуємо всі (income + outcome)
    val recentExpenses = userExpenses.sortedByDescending { it.date + it.time }.take(10)

    // Статистика за категоріями — тільки outcome
    val expensesOutcome = userExpenses.filter { it.type == "outcome" }
    val categoryStats = expensesOutcome.groupBy { it.category ?: "Інше" }
        .mapValues { entry -> entry.value.sumOf { it.amount } }

    // Загальна сума балансу — для прикладу можна порахувати
    val totalBalance = userExpenses.sumOf { if (it.type == "income") it.amount else -it.amount }
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
                    .then(if (showDialog) Modifier.blur(6.dp) else Modifier)
            ) {
                UserHeader(
                    name = name,
                    balance = balanceText,
                    avatarRes = avatarRes
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Кнопка "+ Додати"
                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("+ Додати", color = Color.White, style = MaterialTheme.typography.titleMedium)
                }

                Spacer(modifier = Modifier.height(24.dp))

                SectionTitle("Останні операції")
                Spacer(modifier = Modifier.height(8.dp))

                if (recentExpenses.isEmpty()) {
                    Text("Поки що немає операцій.", color = Color.Gray)
                } else {
                    recentExpenses.forEach { expense ->
                        ExpenseRow(
                            expense = ExpenseItem(
                                userName = expense.userName,
                                category = expense.category ?: "",
                                amount = (if (expense.type == "outcome") "-" else "+") + expense.amount.toInt().toString(),
                                date = expense.date,
                                avatarRes = if (expense.userName == "Паша") R.drawable.pasha_avatar else R.drawable.tanya_avatar
                            )
                        )
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

                // Цілі заощаджень — для прикладу можна замінити на реальні дані
                //SavingsGoalsSection()

                Spacer(modifier = Modifier.height(24.dp))

                // Графік витрат (плейсхолдер)
                //ExpensesChartSection()
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
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )
}
