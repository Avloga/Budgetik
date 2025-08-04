package com.avloga.budgetik.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avloga.budgetik.data.model.Expense
import com.avloga.budgetik.ui.theme.ExpenseRed
import com.avloga.budgetik.ui.theme.IncomeGreen
import com.avloga.budgetik.ui.theme.LightGray
import com.avloga.budgetik.ui.theme.LightMintGreen
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun DateTransactionList(
    expenses: List<Expense>,
    modifier: Modifier = Modifier
) {
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

    // Групування по датах (від нових до старих)
    val grouped = expenses
        .groupBy { it.date }
        .toList()
        .sortedByDescending { (date, _) ->
            try { LocalDate.parse(date, dateFormatter) } catch (e: Exception) { LocalDate.MIN }
        }
        .toMap()

    val sortedDates = grouped.keys.sortedByDescending { dateStr ->
        try { LocalDate.parse(dateStr, dateFormatter) } catch (e: Exception) { LocalDate.MIN }
    }

    Column(modifier = modifier) {
        sortedDates.forEach { date ->
            // Заголовок дати
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LightMintGreen)
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getFormattedDate(date),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF7FC99E)
                )
            }

            val dayExpenses = grouped[date] ?: emptyList()
            val sortedDay = dayExpenses.sortedByDescending {
                parseTimeSafely(it.time)
            }

            // Транзакції для цієї дати
            Column {
                sortedDay.forEach { expense ->
                    DateTransactionItem(expense = expense)
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

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
            "$dayOfWeek, $day $month"
        }
    }
}

// Функція для форматування часу для відображення (тільки години та хвилини)
fun formatTimeForDisplay(timeStr: String): String {
    return try {
        // Спочатку пробуємо новий формат з секундами
        val time = LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm:ss"))
        time.format(DateTimeFormatter.ofPattern("HH:mm"))
    } catch (e: Exception) {
        try {
            // Якщо не вдалося, пробуємо старий формат без секунд
            val time = LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"))
            time.format(DateTimeFormatter.ofPattern("HH:mm"))
        } catch (e: Exception) {
            // Якщо і це не вдалося, повертаємо оригінальний рядок
            timeStr
        }
    }
}

@Composable
fun DateTransactionItem(expense: Expense) {
    val avatarColor = remember(expense.userName) {
        when (expense.userName.lowercase()) {
            "паша" -> Color(0xFF4CAF50) // Зелений для Паші
            "таня" -> Color(0xFFE91E63) // Рожевий для Тані
            else -> Color(0xFF9C27B0) // Фіолетовий для інших
        }
    }
    val amountColor = remember(expense.type) {
        if (expense.type == "income") IncomeGreen else ExpenseRed
    }
    val displayTime = remember(expense.time) { formatTimeForDisplay(expense.time) }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Аватарка (колірний круг замість зображення)
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(avatarColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = expense.userName.firstOrNull()?.uppercase() ?: "?",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        // Ім'я та категорія
        Column(modifier = Modifier.width(80.dp)) {
            Text(
                text = expense.userName,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF333333)
            )
            Text(
                text = expense.category ?: "Інше",
                fontSize = 12.sp,
                color = LightGray
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        // Коментар
        Text(
            text = expense.comment,
            fontSize = 13.sp,
            color = Color(0xFF333333),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        // Сума та час
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = (if (expense.type == "income") "+" else "-") + "${expense.amount.toInt()} грн",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = amountColor
            )
            Text(
                text = displayTime,
                fontSize = 12.sp,
                color = LightGray
            )
        }
    }
} 