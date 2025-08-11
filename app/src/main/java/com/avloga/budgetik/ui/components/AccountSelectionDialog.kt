package com.avloga.budgetik.ui.components

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.avloga.budgetik.ui.theme.BalanceGreen
import com.avloga.budgetik.util.AccountType
import com.avloga.budgetik.util.MoneyUtils.formatMoneyTruncated

@Composable
fun AccountSelectionDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onAccountSelected: (AccountType) -> Unit,
    cashBalance: Double = 0.0,
    cardBalance: Double = 0.0
) {
    if (isVisible) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Заголовок
                    Text(
                        text = "Оберіть рахунок",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Опція "Готівка"
                    AccountOption(
                        emoji = "💵",
                        title = "Готівка",
                        balance = cashBalance,
                        onClick = {
                            onAccountSelected(AccountType.CASH)
                            onDismiss()
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Опція "Платіжна картка"
                    AccountOption(
                        emoji = "💳",
                        title = "Платіжна картка",
                        balance = cardBalance,
                        onClick = {
                            onAccountSelected(AccountType.CARD)
                            onDismiss()
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Опція "Усі рахунки"
                    AccountOption(
                        emoji = "🏦",
                        title = "Усі рахунки",
                        balance = cashBalance + cardBalance,
                        onClick = {
                            onAccountSelected(AccountType.ALL)
                            onDismiss()
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Кнопка "Скасувати"
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Скасувати",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AccountOption(
    emoji: String,
    title: String,
    balance: Double,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BalanceGreen.copy(alpha = 0.3f)),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Смайлик
            Text(
                text = emoji,
                fontSize = 32.sp
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Текст та баланс
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Text(
                    text = "${formatMoneyTruncated(balance)} грн",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            // Стрілка
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Вибрати",
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
