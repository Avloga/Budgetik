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
import com.avloga.budgetik.util.MoneyUtils.formatMoneyTruncated
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.withTimeoutOrNull
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.avloga.budgetik.R

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
fun DateTransactionItem(
    expense: Expense,
    showDelete: Boolean = false,
    onLongPress: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null
) {
    val avatarRes: Int? = remember(expense.userName) {
        when (expense.userName.lowercase()) {
            "паша", "pasha" -> R.drawable.pasha_avatar
            "таня", "tanya" -> R.drawable.tanya_avatar
            else -> null
        }
    }
    val amountColor = remember(expense.type) {
        if (expense.type == "income") IncomeGreen else ExpenseRed
    }
    val displayTime = remember(expense.time) { formatTimeForDisplay(expense.time) }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 16.dp)
            .pointerInput(onLongPress) {
                if (onLongPress != null) {
                    detectTapGestures(
                        onPress = {
                            // Кастомний лонгпрес >= 1 сек
                            val releasedWithinTimeout = withTimeoutOrNull(300) {
                                tryAwaitRelease()
                                true
                            } ?: false
                            if (!releasedWithinTimeout) {
                                onLongPress()
                                // Дочекаємося відпускання, щоб не обробляти повторно
                                tryAwaitRelease()
                            }
                        }
                    )
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Зона аватарки/іконки видалення (40dp)
        Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center) {
            // Аватарка
            if (!showDelete) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = true,
                    enter = slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(250)),
                    exit = slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(250))
                ) {
                    if (avatarRes != null) {
                        Image(
                            painter = painterResource(id = avatarRes),
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE0E0E0))
                        )
                    }
                }
            }

            // Іконка видалення (тимчасово ❌)
            androidx.compose.animation.AnimatedVisibility(
                visible = showDelete,
                enter = slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(250)),
                exit = slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(250))
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFEBEE))
                        .pointerInput(onDeleteClick) {
                            if (onDeleteClick != null) {
                                detectTapGestures(onTap = { onDeleteClick() })
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "❌", fontSize = 18.sp)
                }
            }
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
            val categoryName = expense.category ?: "Інше"
            val category = Categories.findByName(categoryName)
            Text(
                text = category?.name ?: categoryName,
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
                text = (if (expense.type == "income") "+" else "-") + "${formatMoneyTruncated(expense.amount)} грн",
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