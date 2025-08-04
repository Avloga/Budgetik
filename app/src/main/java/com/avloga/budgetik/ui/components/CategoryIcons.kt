package com.avloga.budgetik.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avloga.budgetik.ui.theme.*

@Composable
fun CategoryIconsGrid(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Лівий стовпець категорій
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CategoryText(
                text = "🛒",
                color = CategoryPink,
                contentDescription = "Покупки"
            )
            CategoryText(
                text = "🏠",
                color = CategoryBlue,
                contentDescription = "Будинок"
            )
            CategoryText(
                text = "🍽️",
                color = LightGray,
                contentDescription = "Їжа"
            )
            CategoryText(
                text = "🧴",
                color = CategoryBlue,
                contentDescription = "Особиста гігієна"
            )
            CategoryText(
                text = "⚽",
                color = CategoryTeal,
                contentDescription = "Спорт"
            )
            CategoryText(
                text = "🚗",
                color = CategoryBlue,
                contentDescription = "Машина"
            )
        }
        
        // Правий стовпець категорій
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CategoryText(
                text = "🏥",
                color = CategoryRed,
                contentDescription = "Здоров'я"
            )
            CategoryText(
                text = "📞",
                color = LightGray,
                contentDescription = "Телефон"
            )
            CategoryText(
                text = "🐱",
                color = CategoryTeal,
                contentDescription = "Тварини"
            )
            CategoryText(
                text = "🎁",
                color = CategoryPurple,
                contentDescription = "Подарунки"
            )
            CategoryText(
                text = "👕",
                color = CategoryPurple,
                contentDescription = "Одяг"
            )
            CategoryText(
                text = "🍺",
                color = CategoryOrange,
                contentDescription = "Розваги"
            )
        }
    }
}

@Composable
fun CategoryText(
    text: String,
    color: Color,
    contentDescription: String,
    percentage: String? = null,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 24.sp,
                fontWeight = FontWeight.Normal
            )
        }
        
        // Відображення відсотків під категорією (тільки якщо відсоток не null)
        if (percentage != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = percentage,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = com.avloga.budgetik.ui.theme.DarkGray
            )
        }
    }
}

// Залишаю стару функцію для сумісності
@Composable
fun CategoryIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    CategoryText(
        text = "📊",
        color = color,
        contentDescription = contentDescription,
        modifier = modifier
    )
} 