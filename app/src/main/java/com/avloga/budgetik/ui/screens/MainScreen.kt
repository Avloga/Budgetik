package com.avloga.budgetik.ui.screens

import ExpensesViewModel
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.avloga.budgetik.R
import com.avloga.budgetik.data.firebase.FirebaseFirestoreManager
import com.avloga.budgetik.ui.components.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    userId: String,
    viewModel: ExpensesViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
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
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm") // або твій формат часу

    val allExpenses = expenses.sortedByDescending { expense ->
        try {
            val date = LocalDate.parse(expense.date, dateFormatter)
            val time = LocalTime.parse(expense.time, timeFormatter)
            LocalDateTime.of(date, time)
        } catch (e: Exception) {
            // Якщо парсинг не вдався — ставимо мінімальне значення, щоб ці елементи були внизу
            LocalDateTime.MIN
        }
    }
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
                    .padding(top = 90.dp)
            ) {
//                UserHeader(
//                    name = name,
//                    balance = balanceText,
//                    avatarRes = avatarRes
//                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("+ Додати", color = Color.White, style = MaterialTheme.typography.titleMedium)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Новий рядок із трьома кнопками
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { /* поки що без функціоналу */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Кнопка 1")
                    }
                    Button(
                        onClick = { /* поки що без функціоналу */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Кнопка 2")
                    }
                    Button(
                        onClick = { /* поки що без функціоналу */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Кнопка 3")
                    }
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
                        //HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
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

            // Верхній AppBar з аватаркою, ім'ям і кнопкою налаштувань
            CenterAlignedTopAppBar(
                title = {
                    // Залиш порожнім, або додай щось дрібне
                },
                navigationIcon = {
                    // Замість navigationIcon використовуємо весь вміст
                    Row(
                        modifier = Modifier
                            .padding(end = 60.dp) // ВАЖЛИВО
                            .fillMaxWidth()
                            .height(80.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Ось тут буде вже сам UserHeader з нуля або inline
                        UserHeader(
                            name = name,
                            balance = balanceText,
                            avatarRes = avatarRes,
                            modifier = Modifier
                                .padding(start = 0.dp)
                                .fillMaxWidth()
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        // TODO: Перехід у налаштування
                    }) {
                        Icon(Icons.Default.Settings, contentDescription = "Налаштування")
                    }
                }
            )


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
