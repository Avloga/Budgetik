package com.avloga.budgetik.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avloga.budgetik.ui.theme.BlueGray
import com.avloga.budgetik.ui.theme.DarkGray
import com.avloga.budgetik.ui.theme.LightGray
import com.avloga.budgetik.ui.theme.IncomeGreen
import com.avloga.budgetik.ui.theme.ExpenseRed

@Composable
fun CircularChart(
    modifier: Modifier = Modifier,
    income: String = "0,00 грн",
    expense: String = "0,00 грн"
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Круговий графік
        Canvas(
            modifier = Modifier.size(220.dp)
        ) {
            val strokeWidth = 115f
            val radius = (size.minDimension - strokeWidth) / 2
            
            // Малюємо сіре кільце
            drawCircle(
                color = BlueGray,
                radius = radius,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round
                )
            )
        }
        
        // Текст всередині кільця
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = income,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = IncomeGreen
            )
            Text(
                text = expense,
                fontSize = 16.sp,
                color = ExpenseRed
            )
        }
    }
} 