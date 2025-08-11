package com.avloga.budgetik.ui.screens

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
import com.avloga.budgetik.util.MoneyUtils.formatMoneyTruncated
import com.avloga.budgetik.util.MoneyUtils.formatMoneyTruncatedWithSign
import com.avloga.budgetik.ui.components.CategoryPercentage
import com.avloga.budgetik.ui.components.SideMenu
import com.avloga.budgetik.util.AccountType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    userId: String,
    viewModel: ExpensesViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val expenses by viewModel.expensesFlow.collectAsState()

    // Перевіряємо поточний selectedAccount в ViewModel при поверненні на екран
    LaunchedEffect(Unit) {
        // Це забезпечить, що при поверненні на екран ми маємо актуальний стан
        viewModel.refreshBalances()
    }

    val filteredExpenses by viewModel.filteredExpensesFlow.collectAsState()
    val formattedDate by viewModel.formattedDateFlow.collectAsState()
    val selectedPeriod by viewModel.selectedPeriod.collectAsState()
    val selectedAccount by viewModel.selectedAccount.collectAsState()
    val allExpenses by viewModel.allExpensesFlow.collectAsState()
    val cashBalance by viewModel.cashBalance.collectAsState()
    val cardBalance by viewModel.cardBalance.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var lastPressedType by remember { mutableStateOf<String?>(null) } // "outcome" або "income"
    var showSideMenu by remember { mutableStateOf(false) }

    // Оптимізуємо обчислення, використовуючи remember
    val name = remember(userId) {
        when (userId.lowercase()) {
            "pasha" -> "Паша"
            "tanya" -> "Таня"
            else -> "Користувач"
        }
    }

    val avatarRes = remember(userId) {
        when (userId.lowercase()) {
            "pasha" -> R.drawable.pasha_avatar
            "tanya" -> R.drawable.tanya_avatar
            else -> R.drawable.default_avatar
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

    // Використовуємо фільтровані витрати замість всіх
    val allExpensesForDisplay = remember(filteredExpenses) {
        filteredExpenses.sortedByDescending { expense ->
            try {
                val date = LocalDate.parse(expense.date, dateFormatter)
                val time = parseTimeSafely(expense.time)
                LocalDateTime.of(date, time)
            } catch (e: Exception) {
                LocalDateTime.MIN
            }
        }
    }

    val totalBalance = remember(allExpensesForDisplay) {
        allExpensesForDisplay.sumOf { if (it.type == "income") it.amount else -it.amount }
    }
    val balanceText = remember(totalBalance) { "${formatMoneyTruncatedWithSign(totalBalance)} ₴" }

    // Розрахунок доходів та витрат для кругового графіка
    val totalIncome = remember(allExpensesForDisplay) {
        allExpensesForDisplay.filter { it.type == "income" }.sumOf { it.amount }
    }
    val totalExpense = remember(allExpensesForDisplay) {
        allExpensesForDisplay.filter { it.type == "outcome" }.sumOf { it.amount }
    }
    val incomeText = remember(totalIncome) { "${formatMoneyTruncated(totalIncome)} грн" }
    val expenseText = remember(totalExpense) { "${formatMoneyTruncated(totalExpense)} грн" }

    // Розрахунок відсотків для кожної категорії
    val categoryPercentages = remember(allExpensesForDisplay) {
        val totalExpenses = allExpensesForDisplay.filter { it.type == "outcome" }.sumOf { it.amount }
        if (totalExpenses > 0) {
            val categoryTotals = mutableMapOf<String, Double>()
            
            // Підраховуємо суму для кожної категорії
            allExpensesForDisplay.filter { it.type == "outcome" }.forEach { expense ->
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
    val categories = remember { Categories.toCategoryItems() }

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

    // Оновлюємо баланси при зміні рахунку
    LaunchedEffect(selectedAccount) {
        viewModel.refreshBalances()
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
                    showSideMenu = !showSideMenu
                },
                selectedAccount = selectedAccount,
                userId = userId
            )

            // Основний контент
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Відформатована дата залежно від періоду
                Text(
                    text = formattedDate,
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
                                navController.navigate("all_expenses/$userId/${selectedAccount.name}") {
                                    popUpTo("MainScreen/$userId") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = false
                                }
                            }
                    )
                    
                    // Панель балансу (кнопка навігації)
                    BalancePanel(
                        balance = balanceText,
                        onClick = {
                            navController.navigate("all_expenses/$userId/${selectedAccount.name}") {
                                popUpTo("MainScreen/$userId") { saveState = true }
                                launchSingleTop = true
                                restoreState = false
                            }
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
                                navController.navigate("all_expenses/$userId/${selectedAccount.name}") {
                                    popUpTo("MainScreen/$userId") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = false
                                }
                            }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Кнопки дій
                ActionButtons(
                    onExpenseClick = {
                        lastPressedType = "outcome"
                        showDialog = true
                    },
                    onIncomeClick = {
                        lastPressedType = "income"
                        showDialog = true
                    },
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
                        if (selectedAccount == AccountType.ALL) {
                            Toast.makeText(context, "Оберіть конкретний рахунок для додавання операції", Toast.LENGTH_LONG).show()
                            return@launch
                        }
                        
                        FirebaseFirestoreManager.addExpense(
                            expense,
                            selectedAccount, // Передаємо поточний рахунок
                            onSuccess = {
                                Toast.makeText(context, "Операцію додано", Toast.LENGTH_SHORT).show()
                                showDialog = false
                            },
                            onFailure = {
                                Toast.makeText(context, "Помилка збереження", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                },
                // Передаємо зафіксований тип у залежності від останньої натиснутої кнопки
                presetType = lastPressedType
            )
        }

        // Бокове меню
        SideMenu(
            isVisible = showSideMenu,
            onDismiss = { showSideMenu = false },
            selectedPeriod = selectedPeriod,
            selectedAccount = selectedAccount,
            cashBalance = cashBalance,
            cardBalance = cardBalance,
            onPeriodSelected = { periodName ->
                viewModel.setSelectedPeriodFromString(periodName)
            },
            onAccountSelected = { accountType ->
                // Безпосередньо встановлюємо вибраний рахунок
                viewModel.setSelectedAccount(accountType)
                showSideMenu = false
            }
        )


    }
}
