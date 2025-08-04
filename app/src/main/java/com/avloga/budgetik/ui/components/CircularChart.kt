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
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// Структура для категорії з відсотком
data class CategoryPercentage(
    val name: String,
    val percentage: Float,
    val color: Color
)

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
                    cap = StrokeCap.Butt
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

@Composable
fun CategoryCircularChart(
    categories: List<CategoryPercentage>,
    incomeText: String = "0 грн",
    expenseText: String = "0 грн",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Круговий графік з категоріями
        Canvas(
            modifier = Modifier.size(220.dp)
        ) {
            val strokeWidth = 115f
            val radius = (size.minDimension - strokeWidth) / 2
            val center = androidx.compose.ui.geometry.Offset(size.width / 2, size.height / 2)
            
            // Фільтруємо категорії з відсотками > 0
            val activeCategories = categories.filter { it.percentage > 0 }
            
            if (activeCategories.isNotEmpty()) {
                var currentAngle = -90f // Починаємо з верхньої точки
                
                // Нормалізуємо відсотки, щоб вони в сумі давали 100%
                val totalPercentage = activeCategories.sumOf { it.percentage.toDouble() }
                val normalizedCategories = if (totalPercentage > 0) {
                    activeCategories.map { category ->
                        category.copy(percentage = (category.percentage / totalPercentage * 100).toFloat())
                    }
                } else {
                    activeCategories
                }
                
                // Якщо є тільки одна категорія, робимо повне коло
                if (normalizedCategories.size == 1) {
                    val category = normalizedCategories.first()
                    drawArc(
                        color = category.color,
                        startAngle = currentAngle,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(
                            width = strokeWidth,
                            cap = StrokeCap.Butt
                        ),
                        size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                        topLeft = androidx.compose.ui.geometry.Offset(
                            center.x - radius,
                            center.y - radius
                        )
                    )
                } else {
                    // Малюємо всі категорії
                    normalizedCategories.forEach { category ->
                        val sweepAngle = (category.percentage / 100f) * 360f
                        
                        // Малюємо дугу для категорії
                        drawArc(
                            color = category.color,
                            startAngle = currentAngle,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            style = Stroke(
                                width = strokeWidth,
                                cap = StrokeCap.Butt
                            ),
                            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                            topLeft = androidx.compose.ui.geometry.Offset(
                                center.x - radius,
                                center.y - radius
                            )
                        )
                        
                        currentAngle += sweepAngle
                    }
                }
            } else {
                // Якщо немає активних категорій, малюємо сіре кільце
                drawCircle(
                    color = BlueGray,
                    radius = radius,
                    style = Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Butt
                    )
                )
            }
        }
        
        // Текст всередині кільця
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = incomeText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = IncomeGreen
            )
            Text(
                text = expenseText,
                fontSize = 16.sp,
                color = ExpenseRed
            )
        }
    }
} 