package com.avloga.budgetik.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import com.avloga.budgetik.R
import com.avloga.budgetik.data.model.Expense

// Одна витрата
@Composable
fun ExpenseRow(expense: Expense) {
    val amountColor = if (expense.type == "income") Color(0xFF2E7D32) else Color(0xFFC62828)

    // Конвертуємо amount Double в форматований рядок з +/-
    val amountText = (if (expense.type == "outcome") "-" else "+") + expense.amount.toInt().toString()

    // Визначаємо аватар за іменем користувача
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
                text = expense.date,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

// Список витрат з опцією згортання/розгортання
@Composable
fun ExpenseList(
    expenses: List<Expense>,
    showFull: Boolean,
    onToggleShowFull: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        val toShow = if (showFull) expenses else expenses.take(5)

        toShow.forEach { expense ->
            ExpenseRow(expense = expense)
            Divider(color = Color.LightGray, thickness = 1.dp)
        }

        if (!showFull && expenses.size > 5) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "...",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}
