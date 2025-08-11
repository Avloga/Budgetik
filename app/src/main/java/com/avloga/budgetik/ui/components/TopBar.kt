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
import com.avloga.budgetik.util.AccountType
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.avloga.budgetik.R
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.clickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopBar(
    modifier: Modifier = Modifier,
    onMenuClick: () -> Unit = {},
    selectedAccount: AccountType = AccountType.CASH,
    userId: String = "",
    onAvatarClick: () -> Unit = {}
) {
    val avatarRes: Int? = when (userId.lowercase()) {
        "pasha", "паша" -> R.drawable.pasha_avatar
        "tanya", "таня" -> R.drawable.tanya_avatar
        else -> null
    }
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
                        text = "Budgetik",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = androidx.compose.ui.graphics.Color.White
                    )
                    Text(
                        text = when (selectedAccount) {
                            AccountType.CASH -> "Готівка"
                            AccountType.CARD -> "Платіжна картка"
                            AccountType.ALL -> "Усі рахунки"
                        },
                        fontSize = 12.sp,
                        color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f)
                    )
                }
            }
            // Права частина - аватарка або заглушка
            if (avatarRes != null) {
                Image(
                    painter = painterResource(id = avatarRes),
                    contentDescription = "User Avatar",
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable { onAvatarClick() },
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.25f))
                        .clickable { onAvatarClick() }
                )
            }
        }
    }
}
