package com.avloga.budgetik.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.avloga.budgetik.ui.components.*
import com.avloga.budgetik.ui.theme.*
import com.avloga.budgetik.data.model.Expense
import java.time.LocalDate
import java.time.LocalTime
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.Color
import java.util.Locale
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import com.avloga.budgetik.data.firebase.FirebaseFirestoreManager
import com.avloga.budgetik.util.AccountType

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

// Функція для форматування дати
fun getFormattedDate(dateStr: String): String {
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val locale = Locale("uk")
    
    val parsed = try { 
        LocalDate.parse(dateStr, dateFormatter) 
    } catch (e: Exception) { 
        return "Невідома дата" 
    }
    
    val today = LocalDate.now()
    val yesterday = today.minusDays(1)
    
    return when (parsed) {
        today -> "Сьогодні"
        yesterday -> "Вчора"
        else -> {
            val dayOfWeek = parsed.dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL, locale)
                .replaceFirstChar { it.uppercase(locale) }
            val day = parsed.dayOfMonth.toString()
            val month = parsed.month.getDisplayName(java.time.format.TextStyle.FULL, locale)
            
            // Додаємо рік, якщо операція була в іншому році
            if (parsed.year != today.year) {
                "$dayOfWeek, $day $month ${parsed.year}"
            } else {
                "$dayOfWeek, $day $month"
            }
        }
    }
}

// Кеш для форматування дат
private val dateCache = mutableMapOf<String, String>()

@Composable
fun getFormattedDateCached(dateStr: String): String {
    return remember(dateStr) {
        dateCache.getOrPut(dateStr) { getFormattedDate(dateStr) }
    }
}

// Функція для створення групування транзакцій по датах
fun createGroupedExpenses(expenses: List<Expense>): Map<String, List<Expense>> {
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    return expenses
        .groupBy { it.date }
        .mapValues { (_, dayExpenses) ->
            dayExpenses.sortedByDescending {
                parseTimeSafely(it.time)
            }
        }
}

@Composable
fun ExpenseListWithStickyHeaders(
    groupedExpenses: Map<String, List<Expense>>, // дата -> список витрат
    modifier: Modifier = Modifier
) {
    val sortedDates = groupedExpenses.keys.sortedByDescending { dateStr ->
        // Сортуємо дати від новіших до старіших
        try {
            LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        } catch (e: Exception) {
            LocalDate.MIN
        }
    }

    LazyColumn(modifier = modifier.fillMaxSize()) {
        sortedDates.forEach { date ->
            stickyHeader {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LightMintGreen)
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = getFormattedDate(date), // Наприклад: "Сьогодні", "Вчора", або "1 липня"
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7FC99E)
                    )
                }
            }

            val expenses = groupedExpenses[date] ?: emptyList()

            items(expenses) { expense ->
                DateTransactionItem(expense = expense)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllExpensesScreen(
    navController: NavController,
    userId: String,
    selectedAccountString: String = "CASH",
    viewModel: ExpensesViewModel
) {
    val expenses by viewModel.expensesFlow.collectAsState() // Використовуємо операції з поточного рахунку
    val selectedAccount by viewModel.selectedAccount.collectAsState()
    
    // Встановлюємо правильний рахунок на основі переданого параметра
    LaunchedEffect(selectedAccountString) {
        if (selectedAccountString.isNotEmpty()) {
            viewModel.setSelectedAccountFromString(selectedAccountString)
        }
    }
    
    // Оновлюємо баланси при зміні рахунку
    LaunchedEffect(selectedAccount) {
        viewModel.refreshBalances()
    }
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Оптимізуємо обчислення, використовуючи remember
    val name = remember(userId) {
        when (userId.lowercase()) {
            "pasha" -> "Паша"
            "tanya" -> "Таня"
            else -> "Користувач"
        }
    }

    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy") }
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm:ss") }

    // Функція для безпечного парсингу часу (підтримує як HH:mm, так і HH:mm:ss)
    val parseTimeSafely = remember {
        { timeStr: String ->
            try {
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
    }

    val sortedExpenses = remember(expenses) {
        expenses.sortedByDescending { expense ->
            try {
                val date = LocalDate.parse(expense.date, dateFormatter)
                val time = parseTimeSafely(expense.time)
                LocalDateTime.of(date, time)
            } catch (e: Exception) {
                LocalDateTime.MIN
            }
        }
    }

    val totalBalance = remember(sortedExpenses) {
        sortedExpenses.sumOf { if (it.type == "income") it.amount else -it.amount }
    }
    val balanceText = remember(totalBalance) { "${totalBalance.toInt()} грн" }

    val groupedExpenses = remember(sortedExpenses) {
        createGroupedExpenses(sortedExpenses)
    }

    val sortedDates = remember(groupedExpenses) {
        groupedExpenses.keys.sortedByDescending { dateStr ->
            try {
                LocalDate.parse(dateStr, dateFormatter)
            } catch (e: Exception) {
                LocalDate.MIN
            }
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
                selectedAccount = selectedAccount
            )

            // Основний контент з LazyColumn
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    // Панель балансу з іконками
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        fun goBackWithSelectedAccount() {
                            // Оновлюємо selectedAccount в ViewModel перед поверненням
                            viewModel.setSelectedAccount(selectedAccount)
                            // Використовуємо scope для асинхронного закриття
                            scope.launch {
                                navController.popBackStack()
                            }
                        }
                
                        // Ліва іконка (кнопка назад)
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Повернутися назад",
                            tint = DarkGray,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { goBackWithSelectedAccount() }
                        )
                
                        // Панель балансу (кнопка назад)
                        BalancePanel(
                            balance = balanceText,
                            onClick = { goBackWithSelectedAccount() },
                            modifier = Modifier.weight(1f)
                        )
                
                        // Права іконка (кнопка назад)
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Повернутися назад",
                            tint = DarkGray,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { goBackWithSelectedAccount() }
                        )
                    }
                }
                

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Sticky headers та транзакції
                sortedDates.forEach { date ->
                    stickyHeader {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(LightMintGreen)
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = getFormattedDateCached(date),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF7FC99E)
                            )
                        }
                    }

                    val expenses = groupedExpenses[date] ?: emptyList()
                    items(expenses) { expense ->
                        DateTransactionItem(expense = expense)
                    }
                }
            }

            // Кнопки дій (завжди внизу екрану)
            ActionButtons(
                onExpenseClick = { showDialog = true },
                onIncomeClick = { showDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))
        }

        // Діалог додавання витрат/доходів
        if (showDialog) {
            AddExpenseDialog(
                userId = name,
                onDismiss = { 
                    showDialog = false
                },
                onSubmit = { expense ->
                    scope.launch {
                        // Використовуємо поточний вибраний рахунок
                        val currentSelectedAccount = viewModel.selectedAccount.value
                        
                        if (currentSelectedAccount == AccountType.ALL) {
                            Toast.makeText(context, "Оберіть конкретний рахунок для додавання операції", Toast.LENGTH_LONG).show()
                            return@launch
                        }
                        
                        FirebaseFirestoreManager.addExpense(
                            expense,
                            currentSelectedAccount,
                            onSuccess = {
                                Toast.makeText(context, "Операцію додано", Toast.LENGTH_SHORT).show()
                                showDialog = false
                            },
                            onFailure = {
                                Toast.makeText(context, "Помилка збереження", Toast.LENGTH_SHORT).show()
                                // Не закриваємо діалог при помилці, щоб користувач міг спробувати ще раз
                            }
                        )
                    }
                }
            )
        }
    }
}

