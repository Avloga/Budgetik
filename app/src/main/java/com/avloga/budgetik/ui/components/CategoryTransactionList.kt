package com.avloga.budgetik.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avloga.budgetik.data.model.Expense
import com.avloga.budgetik.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CategoryTransactionList(
    expenses: List<Expense>,
    modifier: Modifier = Modifier
) {
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    
    // Групуємо витрати за категоріями
    val categoryStats = expenses.groupBy { it.category ?: "Інше" }
        .mapValues { (category, categoryExpenses) ->
            CategoryData(
                name = category,
                totalAmount = categoryExpenses.sumOf { it.amount },
                transactionCount = categoryExpenses.size,
                transactions = categoryExpenses.sortedByDescending { expense ->
                    try {
                        LocalDate.parse(expense.date, dateFormatter)
                    } catch (e: Exception) {
                        LocalDate.MIN
                    }
                }
            )
        }
        .values
        .sortedByDescending { it.totalAmount }
        .toList()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categoryStats.forEach { categoryData ->
            CategoryTransactionItem(categoryData = categoryData)
        }
    }
}

data class CategoryData(
    val name: String,
    val totalAmount: Double,
    val transactionCount: Int,
    val transactions: List<Expense>
)

@Composable
fun CategoryTransactionItem(
    categoryData: CategoryData,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    
    val categoryColor = when (categoryData.name.lowercase()) {
        "зарплата", "дохід" -> IncomeGreen
        "машина", "транспорт" -> CategoryBlue
        "їжа", "продукти" -> CategoryPink
        "гігієна", "особиста гігієна" -> CategoryBlue
        "спорт", "фітнес" -> CategoryTeal
        "здоров'я", "медицина" -> CategoryRed
        "телефон", "комунікації" -> LightGray
        "тварини", "петс" -> CategoryTeal
        "подарунки" -> CategoryPurple
        "одяг" -> CategoryPurple
        "розваги", "розваги" -> CategoryOrange
        else -> LightGray
    }
    
    val isIncome = categoryData.name.lowercase() in listOf("зарплата", "дохід")
    val amountColor = if (isIncome) IncomeGreen else ExpenseRed
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        // Основна категорія
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded },
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Стрілка розгортання
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (isExpanded) "Згорнути" else "Розгорнути",
                tint = DarkGray,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Іконка категорії
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(categoryColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getCategoryEmoji(categoryData.name),
                    fontSize = 20.sp
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Кількість транзакцій
            if (categoryData.transactionCount > 1) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(categoryColor.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = categoryData.transactionCount.toString(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = categoryColor
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            } else {
                // Якщо транзакція одна, показуємо одиничку
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(categoryColor.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "1",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = categoryColor
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            
            // Назва категорії
            Text(
                text = categoryData.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = DarkGray,
                modifier = Modifier.weight(1f)
            )
            
            // Сума
            Text(
                text = "${categoryData.totalAmount.toInt()} грн",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = amountColor
            )
        }
        
        // Деталі транзакцій (якщо розгорнуто)
        if (isExpanded && categoryData.transactions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            
            Column {
                categoryData.transactions.forEachIndexed { index, expense ->
                    if (index > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    
                    TransactionDetailItem(
                        expense = expense,
                        isLast = index == categoryData.transactions.size - 1
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionDetailItem(
    expense: Expense,
    isLast: Boolean,
    modifier: Modifier = Modifier
) {
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val amountColor = if (expense.type == "income") IncomeGreen else ExpenseRed
    
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Червона крапка та лінія
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(ExpenseRed)
            )
            
            if (!isLast) {
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(16.dp)
                        .background(LightGray.copy(alpha = 0.5f))
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Сума
        Text(
            text = "${expense.amount.toInt()} грн",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = amountColor,
            modifier = Modifier.weight(1f)
        )
        
        // Дата
        val dateText = try {
            val date = LocalDate.parse(expense.date, dateFormatter)
            val day = date.dayOfMonth
            val month = date.month.getDisplayName(TextStyle.SHORT, Locale("uk"))
            "$day $month"
        } catch (e: Exception) {
            expense.date
        }
        
        Text(
            text = dateText,
            fontSize = 12.sp,
            color = LightGray
        )
    }
}

fun getCategoryEmoji(category: String): String {
    return when (category.lowercase()) {
        "зарплата", "дохід" -> "💰"
        "машина", "транспорт" -> "🚗"
        "їжа", "продукти" -> "🛒"
        "гігієна", "особиста гігієна" -> "🧴"
        "спорт", "фітнес" -> "⚽"
        "здоров'я", "медицина" -> "🏥"
        "телефон", "комунікації" -> "📞"
        "тварини", "петс" -> "🐱"
        "подарунки" -> "🎁"
        "одяг" -> "👕"
        "розваги" -> "🍺"
        else -> "📊"
    }
} 