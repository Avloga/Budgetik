package com.avloga.budgetik.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.avloga.budgetik.ui.theme.BalanceGreen
import com.avloga.budgetik.ui.theme.LightMintGreen
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

@Composable
fun SideMenu(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val screenWidth = with(LocalDensity.current) { 360.dp.toPx() } // Приблизна ширина екрану
    val menuWidth = screenWidth * 0.6f // Меню займає 70% ширини екрану
    
    val slideAnimation by animateFloatAsState(
        targetValue = if (isVisible) 0f else -menuWidth,
        animationSpec = tween(durationMillis = 300),
        label = "slide"
    )
    
    val fadeAnimation by animateFloatAsState(
        targetValue = if (isVisible) 0.5f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "fade"
    )
    
    if (isVisible || slideAnimation > -menuWidth) {
        Box(
            modifier = modifier.fillMaxSize()
        ) {
            // Затемнений фон (не перекриває хедер)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 78.dp) // Відступ зверху, щоб не перекривати хедер
                    .background(Color.Black.copy(alpha = fadeAnimation))
                    .clickable { onDismiss() }
                    .zIndex(1f)
            )
            
            // Бокове меню (не перекриває хедер)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(with(LocalDensity.current) { menuWidth.toDp() })
                    .offset(x = with(LocalDensity.current) { slideAnimation.toDp() })
                    .padding(top = 78.dp) // Відступ зверху, щоб не перекривати хедер
                    .background(
                        color = Color.White,
                    )
                    .zIndex(2f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    
                    // Кнопка "Усі рахунки"
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BalanceGreen),
                        color = Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Усі рахунки",
                                    color = Color.Black,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "UAH",
                                    color = Color.Black.copy(alpha = 0.7f),
                                    fontSize = 12.sp
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Розгорнути",
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    
                    // Список періодів
                    val periods = listOf(
                        "День",
                        "Тиждень",
                        "Місяць",
                        "Рік",
                        "Усі",
                        "Інтервал",
                        "Вибір дати"
                    )
                    
                    periods.forEach { period ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { /* TODO: Обробка вибору періоду */ },
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BalanceGreen),
                            color = Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = period,
                                    color = Color.Black,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
} 