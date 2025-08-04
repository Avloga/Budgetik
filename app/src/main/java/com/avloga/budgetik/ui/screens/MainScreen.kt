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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.avloga.budgetik.ui.theme.BalanceGreen
import java.util.Locale
import com.avloga.budgetik.ui.components.CategoryPercentage

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

    // Отримуємо поточну дату в потрібному форматі (наприклад: "Понеділок, 4 серпня")
    val currentDate = remember {
        val now = LocalDate.now()
        val locale = Locale("uk")
        val dayOfWeek = now.dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL, locale)
            .replaceFirstChar { it.uppercase(locale) }
        val day = now.dayOfMonth.toString()
        val month = now.month.getDisplayName(java.time.format.TextStyle.FULL, locale)
            .lowercase(locale)
            .replaceFirstChar { it.uppercase(locale) }
        "$dayOfWeek, $day $month"
    }

    // Розрахунок відсотків для кожної категорії
    val categoryPercentages = remember(allExpenses) {
        val totalExpenses = allExpenses.filter { it.type == "outcome" }.sumOf { it.amount }
        if (totalExpenses > 0) {
            val categoryTotals = mutableMapOf<String, Double>()
            
            // Підраховуємо суму для кожної категорії
            allExpenses.filter { it.type == "outcome" }.forEach { expense ->
                val category = expense.category ?: "Інше"
                categoryTotals[category] = categoryTotals.getOrDefault(category, 0.0) + expense.amount
            }
            
            // Розраховуємо відсотки
            categoryTotals.mapValues { (_, amount) ->
                val percentage = (amount / totalExpenses * 100)
                when {
                    percentage >= 1 -> "${percentage.toInt()}%"
                    percentage > 0 -> "<1%"
                    else -> "0%"
                }
            }
        } else {
            emptyMap()
        }
    }

    // Список категорій для легкого переміщення
    // Щоб перемістити категорію, просто змініть її позицію в списку
    // Наприклад, щоб перемістити "Зв'язок" з першої позиції в останню:
    // - Перемістіть CategoryItem("📞", ...) в кінець списку
    val categories = remember {
        listOf(
            CategoryItem("📞", com.avloga.budgetik.ui.theme.CategoryPink, "Зв'язок"),           // позиція 0
            CategoryItem("🍽️", com.avloga.budgetik.ui.theme.CategoryGreen, "Їжа"),             // позиція 1
            CategoryItem("☕", com.avloga.budgetik.ui.theme.CategoryOrange, "Кафе"),                // позиція 2
            CategoryItem("🚌", com.avloga.budgetik.ui.theme.CategoryBlue, "Транспорт"),        // позиція 3
            CategoryItem("🚕", com.avloga.budgetik.ui.theme.CategoryYellow, "Таксі"),            // позиція 4
            CategoryItem("🧴", com.avloga.budgetik.ui.theme.CategoryCyan, "Гігієна"),          // позиція 5
            CategoryItem("🐱", com.avloga.budgetik.ui.theme.CategoryTeal, "Улюбленці"),        // позиція 6
            CategoryItem("👕", com.avloga.budgetik.ui.theme.CategoryPurple, "Одяг"),           // позиція 7
            CategoryItem("🎁", com.avloga.budgetik.ui.theme.CategoryRed, "Подарунки"),      // позиція 8
            CategoryItem("⚽", com.avloga.budgetik.ui.theme.CategoryLime, "Спорт"),            // позиція 9
            CategoryItem("🏥", com.avloga.budgetik.ui.theme.CategoryDeepOrange, "Здоров'я"),          // позиція 10
            CategoryItem("🎮", com.avloga.budgetik.ui.theme.CategoryIndigo, "Ігри"),           // позиція 11
            CategoryItem("🍺", com.avloga.budgetik.ui.theme.CategoryAmber, "Розваги"),        // позиція 12
            CategoryItem("🏠", com.avloga.budgetik.ui.theme.CategoryBrown, "Житло")             // позиція 13
        )
    }

    // Створення списку категорій з відсотками для кругової діаграми
    val categoryPercentagesForChart = remember(categoryPercentages, categories) {
        categories.map { category ->
            val percentageText = categoryPercentages[category.contentDescription] ?: "0%"
            val percentage = when {
                percentageText == "0%" -> 0f
                percentageText == "<1%" -> 0.5f // Показуємо як 0.5% для візуалізації
                else -> percentageText.removeSuffix("%").toFloatOrNull() ?: 0f
            }
            CategoryPercentage(
                name = category.contentDescription,
                percentage = percentage,
                color = category.color
            )
        }
    }

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

                // Поточна дата
                Text(
                    text = currentDate,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = BalanceGreen
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Сітка категорій з центральною діаграмою
                CategoryGrid(
                    categories = categories,
                    incomeText = incomeText,
                    expenseText = expenseText,
                    categoryPercentages = categoryPercentagesForChart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(450.dp)
                )






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
