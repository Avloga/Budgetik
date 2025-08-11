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
import com.avloga.budgetik.util.Period
import com.avloga.budgetik.util.AccountType
import com.avloga.budgetik.util.MoneyUtils.formatMoneyTruncated

// Клас для представлення рахунку
data class AccountItem(
    val emoji: String,
    val name: String,
    val type: AccountType,
    val balance: Double
)

@Composable
fun SideMenu(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    selectedPeriod: Period = Period.DAY,
    selectedAccount: AccountType = AccountType.CASH,
    cashBalance: Double = 0.0,
    cardBalance: Double = 0.0,
    onPeriodSelected: (String) -> Unit = {},
    onAccountSelected: (AccountType) -> Unit = {},
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
    
    // Стан для спадного списку рахунків
    var isAccountDropdownExpanded by remember { mutableStateOf(false) }
    
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
                    
                    // Спадний список для вибору рахунку
                    Box {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isAccountDropdownExpanded = !isAccountDropdownExpanded },
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
                                        text = when (selectedAccount) {
                                            AccountType.CASH -> "Готівка"
                                            AccountType.CARD -> "Платіжна картка"
                                            AccountType.ALL -> "Усі рахунки"
                                        },
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
                                    imageVector = if (isAccountDropdownExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = if (isAccountDropdownExpanded) "Згорнути" else "Розгорнути",
                                    tint = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        
                        // Спадний список
                        DropdownMenu(
                            expanded = isAccountDropdownExpanded,
                            onDismissRequest = { isAccountDropdownExpanded = false },
                            modifier = Modifier
                                .background(Color.White)
                                .width(with(LocalDensity.current) { (menuWidth * 1.2f).toDp() }) // Трохи довше за меню
                        ) {
                            val accounts = listOf(
                                AccountItem("💰", "Готівка", AccountType.CASH, cashBalance),
                                AccountItem("💳", "Платіжна картка", AccountType.CARD, cardBalance),
                                AccountItem("🏦", "Усі рахунки", AccountType.ALL, cashBalance + cardBalance)
                            )
                            
                            accounts.forEachIndexed { index, account ->
                                DropdownMenuItem(
                                    text = { 
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text(
                                                    text = account.emoji,
                                                    fontSize = 16.sp,
                                                    modifier = Modifier.padding(end = 8.dp)
                                                )
                                                Text(
                                                    text = account.name,
                                                    color = if (selectedAccount == account.type) BalanceGreen else Color.Black,
                                                    fontWeight = if (selectedAccount == account.type) FontWeight.Medium else FontWeight.Normal,
                                                    fontSize = 14.sp
                                                )
                                            }
                                            Text(
                                                text = "${formatMoneyTruncated(account.balance)} ₴",
                                                color = if (selectedAccount == account.type) BalanceGreen else Color.Black.copy(alpha = 0.7f),
                                                fontWeight = if (selectedAccount == account.type) FontWeight.Medium else FontWeight.Normal,
                                                fontSize = 12.sp
                                            )
                                        }
                                    },
                                    onClick = {
                                        onAccountSelected(account.type)
                                        isAccountDropdownExpanded = false
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                                
                                // Додаємо розділювач між елементами (крім останнього)
                                if (index < accounts.size - 1) {
                                    Divider(
                                        color = Color.LightGray.copy(alpha = 0.5f),
                                        thickness = 1.dp,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                }
                            }
                        }
                    }
                    
                    // Список періодів
                    val periods = listOf(
                        "День",
                        "Тиждень",
                        "Місяць",
                        "Рік",
                        "Усі"
                    )
                    
                    periods.forEach { period ->
                        val isSelected = when (period) {
                            "День" -> selectedPeriod == Period.DAY
                            "Тиждень" -> selectedPeriod == Period.WEEK
                            "Місяць" -> selectedPeriod == Period.MONTH
                            "Рік" -> selectedPeriod == Period.YEAR
                            "Усі" -> selectedPeriod == Period.ALL
                            else -> false
                        }
                        
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { 
                                    onPeriodSelected(period)
                                    onDismiss()
                                },
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BalanceGreen),
                            color = if (isSelected) BalanceGreen.copy(alpha = 0.1f) else Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = period,
                                    color = if (isSelected) BalanceGreen else Color.Black,
                                    fontSize = 16.sp,
                                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                                )
                                
                                if (isSelected) {
                                    Spacer(modifier = Modifier.weight(1f))
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Вибрано",
                                        tint = BalanceGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
} 