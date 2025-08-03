package com.avloga.budgetik.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.avloga.budgetik.R
import com.avloga.budgetik.data.model.Expense
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun ExpenseRow(expense: Expense) {
    val amountColor = if (expense.type == "income") Color(0xFF2E7D32) else Color(0xFFC62828)

    val amountText = (if (expense.type == "outcome") "-" else "+") + expense.amount.toInt().toString()

    val avatarRes = when (expense.userName) {
        "Паша" -> R.drawable.pasha_avatar
        "Таня" -> R.drawable.tanya_avatar
        else -> R.drawable.default_avatar
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = avatarRes),
            contentDescription = "Avatar",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = expense.userName, fontWeight = FontWeight.Bold)
            expense.category?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = amountText,
                color = amountColor,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = expense.date + " " + expense.time,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun ExpenseList(
    expenses: List<Expense>,
    showFull: Boolean,
    onToggleShowFull: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val today = LocalDate.now()
    val yesterday = today.minusDays(1)

    // Групуємо витрати за датою
    val grouped = expenses.groupBy { expense ->
        try {
            LocalDate.parse(expense.date, dateFormatter)
        } catch (e: Exception) {
            null
        }
    }.toSortedMap(compareByDescending { it ?: LocalDate.MIN }) // Сортуємо дати від нових до старих

    LazyColumn(modifier = modifier) {
        var shownCount = 0

        grouped.forEach { (date, dailyExpenses) ->
            if (!showFull && shownCount >= 5) return@forEach

            val dateText = when {
                date == today -> "Сьогодні"
                date == yesterday -> "Вчора"
                date != null -> {
                    val day = date.dayOfMonth
                    val month = date.month.getDisplayName(TextStyle.FULL, Locale("uk"))
                    if (date.year == today.year) "$day $month" else "$day $month ${date.year}"
                }
                else -> "Невідома дата"
            }

            // Сортуємо витрати в дні по часу (від раннього до пізнього)
            val sortedDailyExpenses = dailyExpenses.sortedByDescending  { expense ->
                try {
                    LocalTime.parse(expense.time, timeFormatter)
                } catch (e: Exception) {
                    LocalTime.MIN
                }
            }

            item {
                Text(
                    text = dateText,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center
                )

                sortedDailyExpenses.forEach { expense ->
                    if (!showFull && shownCount >= 5) return@forEach
                    ExpenseRow(expense = expense)
                    //Divider(color = Color.LightGray, thickness = 1.dp)
                    shownCount++
                }
            }
        }

        if (!showFull && expenses.size > 5) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "...",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}
