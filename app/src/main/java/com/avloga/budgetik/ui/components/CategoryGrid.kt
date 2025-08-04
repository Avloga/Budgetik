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
    val contentDescription: String
)

@Composable
fun CategoryGrid(
    categories: List<CategoryItem>,
    incomeText: String,
    expenseText: String,
    categoryPercentages: Map<String, String> = emptyMap(),
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Центральне коло
        CircularChart(
            income = incomeText,
            expense = expenseText,
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
                percentage = categoryPercentages[categories[0].contentDescription]
            )
            if (categories.size > 1) CategoryText(
                text = categories[1].text, 
                color = categories[1].color, 
                contentDescription = categories[1].contentDescription,
                percentage = categoryPercentages[categories[1].contentDescription]
            )
            if (categories.size > 2) CategoryText(
                text = categories[2].text, 
                color = categories[2].color, 
                contentDescription = categories[2].contentDescription,
                percentage = categoryPercentages[categories[2].contentDescription]
            )
            if (categories.size > 3) CategoryText(
                text = categories[3].text, 
                color = categories[3].color, 
                contentDescription = categories[3].contentDescription,
                percentage = categoryPercentages[categories[3].contentDescription]
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
                percentage = categoryPercentages[categories[4].contentDescription]
            )
            if (categories.size > 5) CategoryText(
                text = categories[5].text, 
                color = categories[5].color, 
                contentDescription = categories[5].contentDescription,
                percentage = categoryPercentages[categories[5].contentDescription]
            )
            if (categories.size > 6) CategoryText(
                text = categories[6].text, 
                color = categories[6].color, 
                contentDescription = categories[6].contentDescription,
                percentage = categoryPercentages[categories[6].contentDescription]
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
                percentage = categoryPercentages[categories[7].contentDescription]
            )
            if (categories.size > 8) CategoryText(
                text = categories[8].text, 
                color = categories[8].color, 
                contentDescription = categories[8].contentDescription,
                percentage = categoryPercentages[categories[8].contentDescription]
            )
            if (categories.size > 9) CategoryText(
                text = categories[9].text, 
                color = categories[9].color, 
                contentDescription = categories[9].contentDescription,
                percentage = categoryPercentages[categories[9].contentDescription]
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
                percentage = categoryPercentages[categories[10].contentDescription]
            )
            if (categories.size > 11) CategoryText(
                text = categories[11].text, 
                color = categories[11].color, 
                contentDescription = categories[11].contentDescription,
                percentage = categoryPercentages[categories[11].contentDescription]
            )
            if (categories.size > 12) CategoryText(
                text = categories[12].text, 
                color = categories[12].color, 
                contentDescription = categories[12].contentDescription,
                percentage = categoryPercentages[categories[12].contentDescription]
            )
            if (categories.size > 13) CategoryText(
                text = categories[13].text, 
                color = categories[13].color, 
                contentDescription = categories[13].contentDescription,
                percentage = categoryPercentages[categories[13].contentDescription]
            )
        }
    }
} 