package com.avloga.budgetik.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.navigation.NavController
import com.avloga.budgetik.data.model.SavingsBank
import com.avloga.budgetik.ui.theme.BalanceGreen
import com.avloga.budgetik.util.MoneyUtils.formatMoneyTruncated
import com.avloga.budgetik.ui.components.CustomTopBar
import com.avloga.budgetik.ui.components.SavingsViewModel
import com.avloga.budgetik.ui.components.AddSavingsBankDialog
import com.avloga.budgetik.util.AccountType
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun SavingsScreen(
    navController: NavController,
    userId: String,
    viewModel: SavingsViewModel = viewModel()
) {
    val savingsData by viewModel.savingsData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val showAddBankDialog by viewModel.showAddBankDialog.collectAsState()
    
    val totalSavings = savingsData?.totalSavings ?: 0.0
    val banks = savingsData?.banks ?: emptyList()
    
    // Логування для діагностики
    LaunchedEffect(savingsData) {
        println("🖥️ SavingsScreen: Отримано дані - totalSavings: $totalSavings, banks: ${banks.size}")
        banks.forEach { bank ->
            println("🏦 SavingsScreen: Банка ${bank.name} - ${bank.currentAmount}/${bank.targetAmount} ₴")
        }
    }
    
    // Завантажити дані при першому запуску
    LaunchedEffect(userId) {
        println("🚀 SavingsScreen: Запускаю завантаження для userId: $userId")
        viewModel.loadUserSavings(userId)
    }
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // TopBar з кнопкою "Назад"
            CustomTopBar(
                modifier = Modifier.fillMaxWidth(),
                onMenuClick = { /* Не потрібно для цієї сторінки */ },
                selectedAccount = AccountType.ALL,
                userId = userId,
                onAvatarClick = { /* Не потрібно для цієї сторінки */ },
                onSavingsClick = { navController.navigateUp() },
                isSavingsScreen = true
            )
            
            // Основний контент
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Заголовок по центру (без кнопки "Назад")
                Text(
                    text = "Накопичення в гривнях",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                
                // Загальна сума накопичень
                Text(
                    text = "${formatMoneyTruncated(totalSavings)} ₴",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                
                // Кнопка "Відкрити Банку"
                Button(
                    onClick = { viewModel.toggleAddBankDialog() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BalanceGreen
                    ),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Відкрити Банку",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Кнопка для заповнення тестовими даними (тільки для демонстрації)
                if (banks.isEmpty()) {
                    OutlinedButton(
                        onClick = { viewModel.populateWithTestData(userId) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    ) {
                        Text("Заповнити тестовими даними")
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // Заголовок "Банки"
                Text(
                    text = "Банки",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                // Підпис з сумою
                Text(
                    text = "У гривні ${formatMoneyTruncated(totalSavings)}",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Список банок
                if (isLoading && banks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = BalanceGreen,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                } else if (banks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "У вас поки немає банок накопичень.\nНатисніть 'Відкрити Банку' щоб створити першу!",
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(
                            items = banks,
                            key = { it.id }
                        ) { bank ->
                            SavingsBankItem(
                                bank = bank,
                                onBankClick = { selectedBank ->
                                    navController.navigate(
                                        "bank_details/${selectedBank.id}/${selectedBank.name}/${selectedBank.currentAmount}/${selectedBank.targetAmount}/${selectedBank.withdrawnAmount}"
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
        
        // Діалог додавання банки
        if (showAddBankDialog) {
            AddSavingsBankDialog(
                onDismiss = { viewModel.toggleAddBankDialog() },
                onConfirm = { name, targetAmount, description, category ->
                    viewModel.createSavingsBank(
                        userId = userId,
                        name = name,
                        targetAmount = targetAmount,
                        description = description,
                        category = category
                    )
                },
                isLoading = isLoading
            )
        }
        
        // Показ помилок
        error?.let { errorMessage ->
            LaunchedEffect(errorMessage) {
                // Показати Toast або Snackbar з помилкою
                viewModel.clearError()
            }
        }
    }
}

@Composable
fun SavingsBankItem(
    bank: SavingsBank,
    onBankClick: (SavingsBank) -> Unit
) {
    val progress = remember(bank.id, bank.currentAmount, bank.targetAmount) {
        (bank.currentAmount / bank.targetAmount).coerceIn(0.0, 1.0)
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onBankClick(bank) },
        verticalAlignment = Alignment.Top
    ) {
        // Зелений круг
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(BalanceGreen)
                .padding(top = 8.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Основний контент
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // Перший рядок: назва банки та бажана сума
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = bank.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                
                Text(
                    text = "${formatMoneyTruncated(bank.targetAmount)} ₴",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Прогрес бар
            LinearProgressIndicator(
                progress = progress.toFloat(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = BalanceGreen,
                trackColor = Color.LightGray.copy(alpha = 0.3f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Текст "Накопичено"
            Text(
                text = "Накопичено ${formatMoneyTruncated(bank.currentAmount)} ₴",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}
