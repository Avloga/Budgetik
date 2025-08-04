package com.avloga.budgetik.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avloga.budgetik.ui.theme.DarkGray
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun DatePanel(
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val dayOfWeek = today.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("uk"))
    val day = today.dayOfMonth
    val month = today.month.getDisplayName(TextStyle.FULL, Locale("uk"))
    
    val dateText = "$dayOfWeek, $day $month"
    
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = dateText,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = DarkGray
        )
    }
} 