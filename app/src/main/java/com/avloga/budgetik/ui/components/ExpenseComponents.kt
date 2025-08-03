package com.avloga.budgetik.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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

data class ExpenseItem(
    val userName: String,
    val category: String,
    val amount: String,
    val date: String,
    val avatarRes: Int,
    val type: String
)

val sampleExpenses = listOf(
    ExpenseItem("Таня", "Одяг", "1 500", "сьогодні", R.drawable.tanya_avatar, "outcome"),
    ExpenseItem("Паша", "Їжа", "800", "сьогодні", R.drawable.pasha_avatar, "outcome"),
    ExpenseItem("Таня", "Транспорт", "300", "вчора", R.drawable.tanya_avatar, "outcome"),
)

@Composable
fun ExpenseRow(expense: ExpenseItem) {
    val amountColor = if (expense.type == "income") Color(0xFF2E7D32) else Color(0xFFC62828) // зелений/червоний

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = expense.avatarRes),
            contentDescription = "Avatar",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = expense.userName, fontWeight = FontWeight.Bold)
            Text(text = expense.category, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = expense.amount,
                color = amountColor,
                fontWeight = FontWeight.Bold
            )
            Text(text = expense.date, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}
