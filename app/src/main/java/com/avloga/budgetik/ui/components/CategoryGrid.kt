package com.avloga.budgetik.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.IntSize

// Структура категорії для легкого переміщення
data class CategoryItem(
    val text: String,
    val color: Color,
    val contentDescription: String,
    val drawableName: String? = null
)

// Допоміжна функція для форматування відсотків
private fun formatPercentage(categoryPercentages: List<CategoryPercentage>, categoryName: String): String? {
    return categoryPercentages.find { it.name == categoryName }?.let { 
        when {
            it.percentage == 0f -> null // Не показуємо відсоток якщо він 0
            it.percentage < 1f -> "<1%"
            else -> "${it.percentage.toInt()}%"
        }
    }
}

@Composable
fun CategoryGrid(
    categories: List<CategoryItem>,
    incomeText: String,
    expenseText: String,
    categoryPercentages: List<CategoryPercentage> = emptyList(),
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Центральне коло з категоріями
        CategoryCircularChart(
            categories = categoryPercentages,
            incomeText = incomeText,
            expenseText = expenseText,
            modifier = Modifier.size(280.dp)
        )
        
        // Категорії розташовані навколо кола
        // Верхній ряд (позиції 0-3)
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(40.dp)
        ) {
            if (categories.size > 0) CategoryText(
                text = categories[0].text, 
                color = categories[0].color, 
                contentDescription = categories[0].contentDescription,
                percentage = formatPercentage(categoryPercentages, categories[0].contentDescription),
                drawableName = categories[0].drawableName,
                percentageColor = categories[0].color
            )
            if (categories.size > 1) CategoryText(
                text = categories[1].text, 
                color = categories[1].color, 
                contentDescription = categories[1].contentDescription,
                percentage = formatPercentage(categoryPercentages, categories[1].contentDescription),
                drawableName = categories[1].drawableName,
                percentageColor = categories[1].color
            )
            if (categories.size > 2) CategoryText(
                text = categories[2].text, 
                color = categories[2].color, 
                contentDescription = categories[2].contentDescription,
                percentage = formatPercentage(categoryPercentages, categories[2].contentDescription),
                drawableName = categories[2].drawableName,
                percentageColor = categories[2].color
            )
            if (categories.size > 3) CategoryText(
                text = categories[3].text, 
                color = categories[3].color, 
                contentDescription = categories[3].contentDescription,
                percentage = formatPercentage(categoryPercentages, categories[3].contentDescription),
                drawableName = categories[3].drawableName,
                percentageColor = categories[3].color
            )
        }
        
        // Лівий стовпець (позиції 4-6)
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 8.dp),
            verticalArrangement = Arrangement.spacedBy(40.dp)
        ) {
            if (categories.size > 4) CategoryText(
                text = categories[4].text, 
                color = categories[4].color, 
                contentDescription = categories[4].contentDescription,
                percentage = formatPercentage(categoryPercentages, categories[4].contentDescription),
                drawableName = categories[4].drawableName,
                percentageColor = categories[4].color
            )
            if (categories.size > 5) CategoryText(
                text = categories[5].text, 
                color = categories[5].color, 
                contentDescription = categories[5].contentDescription,
                percentage = formatPercentage(categoryPercentages, categories[5].contentDescription),
                drawableName = categories[5].drawableName,
                percentageColor = categories[5].color
            )
            if (categories.size > 6) CategoryText(
                text = categories[6].text, 
                color = categories[6].color, 
                contentDescription = categories[6].contentDescription,
                percentage = formatPercentage(categoryPercentages, categories[6].contentDescription),
                drawableName = categories[6].drawableName,
                percentageColor = categories[6].color
            )
        }
        
        // Правий стовпець (позиції 7-9)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp),
            verticalArrangement = Arrangement.spacedBy(40.dp)
        ) {
            if (categories.size > 7) CategoryText(
                text = categories[7].text, 
                color = categories[7].color, 
                contentDescription = categories[7].contentDescription,
                percentage = formatPercentage(categoryPercentages, categories[7].contentDescription),
                drawableName = categories[7].drawableName,
                percentageColor = categories[7].color
            )
            if (categories.size > 8) CategoryText(
                text = categories[8].text, 
                color = categories[8].color, 
                contentDescription = categories[8].contentDescription,
                percentage = formatPercentage(categoryPercentages, categories[8].contentDescription),
                drawableName = categories[8].drawableName,
                percentageColor = categories[8].color
            )
            if (categories.size > 9) CategoryText(
                text = categories[9].text, 
                color = categories[9].color, 
                contentDescription = categories[9].contentDescription,
                percentage = formatPercentage(categoryPercentages, categories[9].contentDescription),
                drawableName = categories[9].drawableName,
                percentageColor = categories[9].color
            )
        }
        
        // Нижній ряд (позиції 10-13)
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(40.dp)
        ) {
            if (categories.size > 10) CategoryText(
                text = categories[10].text, 
                color = categories[10].color, 
                contentDescription = categories[10].contentDescription,
                percentage = formatPercentage(categoryPercentages, categories[10].contentDescription),
                drawableName = categories[10].drawableName,
                percentageColor = categories[10].color
            )
            if (categories.size > 11) CategoryText(
                text = categories[11].text, 
                color = categories[11].color, 
                contentDescription = categories[11].contentDescription,
                percentage = formatPercentage(categoryPercentages, categories[11].contentDescription),
                drawableName = categories[11].drawableName,
                percentageColor = categories[11].color
            )
            if (categories.size > 12) CategoryText(
                text = categories[12].text, 
                color = categories[12].color, 
                contentDescription = categories[12].contentDescription,
                percentage = formatPercentage(categoryPercentages, categories[12].contentDescription),
                drawableName = categories[12].drawableName,
                percentageColor = categories[12].color
            )
            if (categories.size > 13) CategoryText(
                text = categories[13].text, 
                color = categories[13].color, 
                contentDescription = categories[13].contentDescription,
                percentage = formatPercentage(categoryPercentages, categories[13].contentDescription),
                drawableName = categories[13].drawableName,
                percentageColor = categories[13].color
            )
        }
    }
} 