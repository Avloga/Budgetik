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
import androidx.compose.foundation.Image
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource

// CategoryIconsGrid was a legacy static preview and is not used anymore; removed to avoid duplicated/obsolete categories.

@Composable
fun CategoryText(
    text: String,
    color: Color,
    contentDescription: String,
    percentage: String? = null,
    modifier: Modifier = Modifier,
    drawableName: String? = null,
    percentageColor: Color? = null
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    val resId = remember(drawableName) {
        if (drawableName != null) {
            val id = context.resources.getIdentifier(drawableName, "drawable", context.packageName)
            if (id != 0) id else null
        } else null
    }

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
            if (resId != null) {
                Image(
                    painter = painterResource(id = resId as Int),
                    contentDescription = contentDescription,
                    modifier = Modifier.size(28.dp)
                )
            } else {
                Text(text = text, fontSize = 24.sp, fontWeight = FontWeight.Normal)
            }
        }

        if (percentage != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = percentage,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = percentageColor ?: color
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
    // Показати емодзі з центрального реєстру, якщо доступне
    val emoji = Categories.findByName(contentDescription)?.emoji ?: "📊"
    CategoryText(
        text = emoji,
        color = color,
        contentDescription = contentDescription,
        percentageColor = color,
        modifier = modifier
    )
} 