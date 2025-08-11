package com.avloga.budgetik.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.avloga.budgetik.R
import com.avloga.budgetik.ui.theme.BalanceGreen

@Composable
fun LoginScreen(
    navController: NavController,
    onLogin: (String) -> Unit
) {
    val backgroundGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFFE0FFEB),  // темний зелений - початок (знизу ліворуч)
            Color(0xFFF9FFFB)   // світлий зелений - кінець (зверху праворуч)
        ),
        start = Offset(0f, 300f),    // нижній лівий кут
        end = Offset(900f, 0f)        // верхній правий кут
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        // Заголовок зверху
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Budgetik",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = BalanceGreen
            )
        }

        // Центрований блок з картками
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 34.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileCard(
                name = "Паша",
                avatarRes = R.drawable.pasha_avatar,
                tiltAngle = -6f,
                offsetX = (-16).dp, // зсув вліво
                onClick = { onLogin("pasha") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProfileCard(
                name = "Таня",
                avatarRes = R.drawable.tanya_avatar,
                tiltAngle = 6f,
                offsetX = 16.dp, // зсув вправо
                onClick = { onLogin("tanya") }
            )
        }
    }

}

@Composable
fun ProfileCard(
    name: String,
    avatarRes: Int,
    tiltAngle: Float,
    offsetX: Dp = 0.dp,
    onClick: () -> Unit
) {
    var size by remember { mutableStateOf(androidx.compose.ui.geometry.Size.Zero) }


    val gradientBrush = remember(size) {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFF78EF9E),  // темний зелений - початок (знизу ліворуч)
                Color(0xFFCCFFE0)   // світлий зелений - кінець (зверху праворуч)
            ),
            start = Offset(0f, 600f),    // нижній лівий кут
            end = Offset(600f, 0f)        // верхній правий кут
        )
    }


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = offsetX)
                .graphicsLayer {
                    rotationZ = tiltAngle
                }
                .clickable { onClick() }
                .background(brush = gradientBrush, shape = RoundedCornerShape(16.dp)), // фон градієнту на картці
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent // прозорий, бо фон задаємо через Modifier.background
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(24.dp)
            ) {
                Image(
                    painter = painterResource(id = avatarRes),
                    contentDescription = "$name avatar",
                    modifier = Modifier
                        .size(84.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = name,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

