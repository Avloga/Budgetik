package com.avloga.budgetik.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.avloga.budgetik.R
import com.avloga.budgetik.ui.components.*

@Composable
fun MainScreen(navController: NavController) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(top = 24.dp)  // ось тут додано відступ зверху
        ) {
            UserHeader(
                name = "Паша",
                balance = "9 500 ₴",
                avatarRes = R.drawable.pasha_avatar
            )

            Spacer(modifier = Modifier.height(24.dp))

            SectionTitle("Останні витрати")
            Spacer(modifier = Modifier.height(8.dp))

            sampleExpenses.forEach { expense ->
                ExpenseRow(expense)
                Divider(color = Color.LightGray, thickness = 1.dp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            SectionTitle("Статистика за категоріями")
            Spacer(modifier = Modifier.height(8.dp))

            CategoryRow(name = "Їжа", amount = "3 200 ₴")
            CategoryRow(name = "Одяг", amount = "1 500 ₴")
            CategoryRow(name = "Транспорт", amount = "800 ₴")

            Spacer(modifier = Modifier.height(24.dp))

            // Нова секція: Цілі заощаджень
            SavingsGoalsSection()

            Spacer(modifier = Modifier.height(24.dp))

            // Нова секція: Графік витрат (плейсхолдер)
            ExpensesChartSection()
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )
}

@Composable
fun SavingsGoalsSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFE8F5E9))
            .padding(16.dp)
    ) {
        Text(
            "Цілі заощаджень",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E7D32)
        )

        Spacer(modifier = Modifier.height(12.dp))

        GoalProgress(name = "Відпустка", current = 4000f, goal = 20000f)
        GoalProgress(name = "Новий ноутбук", current = 8000f, goal = 15000f)
    }
}

@Composable
fun GoalProgress(name: String, current: Float, goal: Float) {
    val progress = (current / goal).coerceIn(0f, 1f)
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = name)
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp)),
            color = Color(0xFF388E3C),
            trackColor = Color(0xFFC8E6C9)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "${current.toInt()} ₴ / ${goal.toInt()} ₴", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun ExpensesChartSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFBBDEFB))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Графік витрат (placeholder)",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF0D47A1)
        )
        Spacer(modifier = Modifier.height(12.dp))
        // Можеш тут додати графік пізніше (наприклад, з MPAndroidChart або іншим)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color(0xFF90CAF9), RoundedCornerShape(8.dp))
        )
    }
}
