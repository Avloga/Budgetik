package com.avloga.budgetik.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.avloga.budgetik.data.model.SavingsBank
import com.avloga.budgetik.ui.theme.BalanceGreen
import com.avloga.budgetik.util.MoneyUtils.formatMoneyTruncated

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankDetailsScreen(
    navController: NavController,
    bank: SavingsBank
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Зелена полоса зверху
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .background(BalanceGreen)
        ) {
            // Стрілка назад
            IconButton(
                onClick = { navController.navigateUp() },
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp)
                    .size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Назад",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        
        // Основний контент
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Зелене коло на стику кольорів (на першому плані)
            Box(
                modifier = Modifier
                    .offset(y = (-30).dp)
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(BalanceGreen.copy(alpha = 0.8f))
                    .zIndex(1f)
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Назва банки
            Text(
                text = bank.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Сіра горизонтальна лінія
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .height(1.dp)
                    .background(Color.Gray.copy(alpha = 0.3f))
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Сума накопичень
            Text(
                text = "${formatMoneyTruncated(bank.currentAmount)} ₴",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Текст про зняття
            Text(
                text = "Знято ${formatMoneyTruncated(bank.withdrawnAmount)} ₴",
                fontSize = 16.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Зображення банки (тимчасово зелений круг)
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(BalanceGreen.copy(alpha = 0.6f))
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Рядок з кнопками внизу
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    ActionButton(
                        text = "Поповнити\nбанку",
                        onClick = { /* TODO */ }
                    )
                }
                item {
                    ActionButton(
                        text = "Переглянути\nвиписку",
                        onClick = { /* TODO */ }
                    )
                }
                item {
                    ActionButton(
                        text = "Налаштування\nбанки",
                        onClick = { /* TODO */ }
                    )
                }
                item {
                    ActionButton(
                        text = "Зняття з\nбанки",
                        onClick = { /* TODO */ }
                    )
                }
                item {
                    ActionButton(
                        text = "Розбити\nбанку",
                        onClick = { /* TODO */ }
                    )
                }
            }

        }
    }
}

@Composable
private fun ActionButton(
    text: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        // Кругла кнопка
        Button(
            onClick = onClick,
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape),
            colors = ButtonDefaults.buttonColors(
                containerColor = BalanceGreen
            )
        ) {
            // Тут можна додати іконку замість тексту
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Текст під кнопкою
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
