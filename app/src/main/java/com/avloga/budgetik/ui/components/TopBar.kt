package com.avloga.budgetik.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avloga.budgetik.ui.theme.DarkGray
import com.avloga.budgetik.ui.theme.BalanceGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopBar(
    modifier: Modifier = Modifier,
    onMenuClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .background(BalanceGreen),
        color = BalanceGreen
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 0.dp)
                .padding(top = 30.dp), // Додатковий відступ зверху
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ліва частина - меню та заголовок
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onMenuClick
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Меню",
                        tint = androidx.compose.ui.graphics.Color.White
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = "Monefy",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = androidx.compose.ui.graphics.Color.White
                    )
                    Text(
                        text = "Усі рахунки",
                        fontSize = 12.sp,
                        color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f)
                    )
                }
            }
            
            // Центральна частина - місяць
            Text(
                text = "Серпень",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = androidx.compose.ui.graphics.Color.White
            )
            
            // Права частина - іконки
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { /* TODO: Пошук */ }
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Пошук",
                        tint = androidx.compose.ui.graphics.Color.White
                    )
                }
                
                IconButton(
                    onClick = { /* TODO: Синхронізація */ }
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Синхронізація",
                        tint = androidx.compose.ui.graphics.Color.White
                    )
                }
            }
        }
    }
} 