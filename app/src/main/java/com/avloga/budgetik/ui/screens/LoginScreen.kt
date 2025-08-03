package com.avloga.budgetik.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.avloga.budgetik.R

@Composable
fun LoginScreen(
    navController: NavController,
    onLogin: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Оберіть свій аватар", modifier = Modifier.padding(bottom = 16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(R.drawable.pasha_avatar),
                    contentDescription = "Аватар Паші",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .clickable { onLogin("pasha") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Паша")
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(R.drawable.tanya_avatar),
                    contentDescription = "Аватар Тані",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .clickable { onLogin("tanya") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Таня")
            }
        }
    }
}
