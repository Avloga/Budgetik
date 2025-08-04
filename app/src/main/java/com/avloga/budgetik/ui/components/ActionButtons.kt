package com.avloga.budgetik.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avloga.budgetik.ui.theme.ExpenseRed
import com.avloga.budgetik.ui.theme.IncomeGreen

@Composable
fun ActionButtons(
    onExpenseClick: () -> Unit = {},
    onIncomeClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Кнопка "ВИТРАТА"
        Box(
            modifier = Modifier
                .weight(1f)
                .height(90.dp)
                .clip(CircleShape)
                .background(androidx.compose.ui.graphics.Color.White)
                .border(
                    width = 3.dp,
                    color = ExpenseRed,
                    shape = CircleShape
                )
                .clickable { onExpenseClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "ВИТРАТА",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = ExpenseRed
            )
        }
        
        // Кнопка "ДОХІД"
        Box(
            modifier = Modifier
                .weight(1f)
                .height(90.dp)
                .clip(CircleShape)
                .background(androidx.compose.ui.graphics.Color.White)
                .border(
                    width = 3.dp,
                    color = IncomeGreen,
                    shape = CircleShape
                )
                .clickable { onIncomeClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "ДОХІД",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = IncomeGreen
            )
        }
    }
} 