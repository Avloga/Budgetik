package com.avloga.budgetik.ui.screens

import ExpensesViewModel
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.avloga.budgetik.R
import com.avloga.budgetik.data.firebase.FirebaseFirestoreManager
import com.avloga.budgetik.ui.components.*
import com.avloga.budgetik.ui.theme.LightMintGreen
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.clickable
import com.avloga.budgetik.ui.theme.DarkGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    userId: String,
    viewModel: ExpensesViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val expenses by viewModel.expensesFlow.collectAsState()

    var showDialog by remember { mutableStateOf(false) }

    val name = when (userId.lowercase()) {
        "pasha" -> "Паша"
        "tanya" -> "Таня"
        else -> "Користувач"
    }

    val avatarRes = when (userId.lowercase()) {
        "pasha" -> R.drawable.pasha_avatar
        "tanya" -> R.drawable.tanya_avatar
        else -> R.drawable.default_avatar
    }

    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

    // Функція для безпечного парсингу часу (підтримує як HH:mm, так і HH:mm:ss)
    fun parseTimeSafely(timeStr: String): LocalTime {
        return try {
            // Спочатку пробуємо новий формат з секундами
            LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm:ss"))
        } catch (e: Exception) {
            try {
                // Якщо не вдалося, пробуємо старий формат без секунд
                LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"))
            } catch (e: Exception) {
                // Якщо і це не вдалося, повертаємо мінімальний час
                LocalTime.MIN
            }
        }
    }

    val allExpenses = expenses.sortedByDescending { expense ->
        try {
            val date = LocalDate.parse(expense.date, dateFormatter)
            val time = parseTimeSafely(expense.time)
            LocalDateTime.of(date, time)
        } catch (e: Exception) {
            LocalDateTime.MIN
        }
    }

    val totalBalance = allExpenses.sumOf { if (it.type == "income") it.amount else -it.amount }
    val balanceText = "${totalBalance.toInt()} ₴"

    // Розрахунок доходів та витрат для кругового графіка
    val totalIncome = allExpenses.filter { it.type == "income" }.sumOf { it.amount }
    val totalExpense = allExpenses.filter { it.type == "outcome" }.sumOf { it.amount }
    val incomeText = "${totalIncome.toInt()} грн"
    val expenseText = "${totalExpense.toInt()} грн"

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = LightMintGreen
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Верхня панель
            CustomTopBar(
                modifier = Modifier.fillMaxWidth(),
                onMenuClick = {
                    // TODO: Відкрити меню (поки що без функціоналу)
                }
            )

            // Основний контент
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Сітка категорій та круговий графік
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Лівий стовпець категорій
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CategoryText(
                            text = "🛒",
                            color = com.avloga.budgetik.ui.theme.CategoryPink,
                            contentDescription = "Покупки"
                        )
                        CategoryText(
                            text = "🏠",
                            color = com.avloga.budgetik.ui.theme.CategoryBlue,
                            contentDescription = "Будинок"
                        )
                        CategoryText(
                            text = "🍽️",
                            color = com.avloga.budgetik.ui.theme.LightGray,
                            contentDescription = "Їжа"
                        )
                        CategoryText(
                            text = "🧴",
                            color = com.avloga.budgetik.ui.theme.CategoryBlue,
                            contentDescription = "Особиста гігієна"
                        )
                        CategoryText(
                            text = "⚽",
                            color = com.avloga.budgetik.ui.theme.CategoryTeal,
                            contentDescription = "Спорт"
                        )
                        CategoryText(
                            text = "🚗",
                            color = com.avloga.budgetik.ui.theme.CategoryBlue,
                            contentDescription = "Машина"
                        )
                    }

                    // Круговий графік по центру
                    CircularChart(
                        income = incomeText,
                        expense = expenseText
                    )

                    // Правий стовпець категорій
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CategoryText(
                            text = "🏥",
                            color = com.avloga.budgetik.ui.theme.CategoryRed,
                            contentDescription = "Здоров'я"
                        )
                        CategoryText(
                            text = "📞",
                            color = com.avloga.budgetik.ui.theme.LightGray,
                            contentDescription = "Телефон"
                        )
                        CategoryText(
                            text = "🐱",
                            color = com.avloga.budgetik.ui.theme.CategoryTeal,
                            contentDescription = "Тварини"
                        )
                        CategoryText(
                            text = "🎁",
                            color = com.avloga.budgetik.ui.theme.CategoryPurple,
                            contentDescription = "Подарунки"
                        )
                        CategoryText(
                            text = "👕",
                            color = com.avloga.budgetik.ui.theme.CategoryPurple,
                            contentDescription = "Одяг"
                        )
                        CategoryText(
                            text = "🍺",
                            color = com.avloga.budgetik.ui.theme.CategoryOrange,
                            contentDescription = "Розваги"
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Панель балансу
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Ліва іконка (кнопка навігації)
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Перейти до всіх операцій",
                        tint = DarkGray,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    "expenses",
                                    allExpenses
                                )
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    "userId",
                                    userId
                                )
                                navController.navigate("all_expenses")
                            }
                    )
                    
                    // Панель балансу (кнопка навігації)
                    BalancePanel(
                        balance = balanceText,
                        onClick = {
                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                "expenses",
                                allExpenses
                            )
                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                "userId",
                                userId
                            )
                            navController.navigate("all_expenses")
                        },
                        modifier = Modifier.weight(1f)
                    )
                    
                    // Права іконка (кнопка навігації)
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Перейти до всіх операцій",
                        tint = DarkGray,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    "expenses",
                                    allExpenses
                                )
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    "userId",
                                    userId
                                )
                                navController.navigate("all_expenses")
                            }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Кнопки дій
                ActionButtons(
                    onExpenseClick = { showDialog = true },
                    onIncomeClick = { showDialog = true },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        // Діалог додавання витрат/доходів
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
