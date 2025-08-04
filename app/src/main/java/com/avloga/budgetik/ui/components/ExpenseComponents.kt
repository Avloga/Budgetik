package com.avloga.budgetik.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow
import com.avloga.budgetik.R
import com.avloga.budgetik.data.model.Expense
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.ui.unit.sp

@Composable
fun ExpenseRow(expense: Expense) {
    val amountColor = if (expense.type == "income") com.avloga.budgetik.ui.theme.IncomeGreen else com.avloga.budgetik.ui.theme.ExpenseRed
    var showDialog by remember { mutableStateOf(false) }
    val amountText = (if (expense.type == "outcome") "-" else "+") + expense.amount.toInt().toString()

    val avatarRes = when (expense.userName) {
        "Паша" -> R.drawable.pasha_avatar
        "Таня" -> R.drawable.tanya_avatar
        else -> R.drawable.default_avatar
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Закрити")
                }
            },
            title = {
                Text("Коментар")
            },
            text = {
                Text(expense.comment)
            }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 8.dp)
            .background(
                color = androidx.compose.ui.graphics.Color.White,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
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
        Spacer(Modifier.width(8.dp))

        Column(modifier = Modifier.width(80.dp)) {
            Text(
                text = expense.userName, 
                fontWeight = FontWeight.Bold,
                color = com.avloga.budgetik.ui.theme.DarkGray
            )
            expense.category?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = com.avloga.budgetik.ui.theme.LightGray
                )
            }
        }

        Text(
            text = expense.comment ?: "",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
                .clickable { if (expense.comment?.isNotEmpty() == true) showDialog = true },
            color = com.avloga.budgetik.ui.theme.DarkGray
        )

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = amountText,
                fontSize = 16.sp,
                color = amountColor,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = expense.time,
                style = MaterialTheme.typography.bodySmall,
                color = com.avloga.budgetik.ui.theme.LightGray
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

    val grouped = expenses.groupBy { expense ->
        try {
            LocalDate.parse(expense.date, dateFormatter)
        } catch (e: Exception) {
            null
        }
    }.toSortedMap(compareByDescending { it ?: LocalDate.MIN })

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

            val sortedDailyExpenses = dailyExpenses.sortedByDescending { expense ->
                try {
                    LocalTime.parse(expense.time, timeFormatter)
                } catch (e: Exception) {
                    LocalTime.MIN
                }
            }

            stickyHeader {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(com.avloga.budgetik.ui.theme.LightMintGreen)
                        .padding(vertical = 12.dp, horizontal = 16.dp)
                ) {
                    Text(
                        text = dateText,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        color = com.avloga.budgetik.ui.theme.DarkGray,
                        textAlign = TextAlign.Center
                    )
                }
            }

            items(sortedDailyExpenses) { expense ->
                if (!showFull && shownCount >= 5) return@items
                ExpenseRow(expense = expense)
                shownCount++
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
                    color = com.avloga.budgetik.ui.theme.LightGray
                )
            }
        }
    }
}
