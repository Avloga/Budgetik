package com.avloga.budgetik.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.avloga.budgetik.ui.theme.BalanceGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSavingsBankDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, targetAmount: Double, description: String, category: String) -> Unit,
    isLoading: Boolean = false
) {
    var name by remember { mutableStateOf("") }
    var targetAmountText by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Загальне") }
    
    val categories = listOf(
        "Загальне", "Подорожі", "Транспорт", "Нерухомість", 
        "Освіта", "Будинок", "Розваги", "Хобі", "Подарунки", 
        "Запас", "Інвестиції", "Здоров'я", "Спорт"
    )
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Заголовок
                Text(
                    text = "Відкрити нову банку",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Назва банки
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Назва банки") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isLoading
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Бажана сума
                OutlinedTextField(
                    value = targetAmountText,
                    onValueChange = { 
                        if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                            targetAmountText = it
                        }
                    },
                    label = { Text("Бажана сума (₴)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isLoading
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Категорія
                ExposedDropdownMenuBox(
                    expanded = false,
                    onExpandedChange = { },
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Категорія") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        enabled = !isLoading
                    )
                    
                    DropdownMenu(
                        expanded = false,
                        onDismissRequest = { }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = { selectedCategory = category }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Опис
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Опис (необов'язково)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3,
                    enabled = !isLoading
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Кнопки
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Кнопка "Скасувати"
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading
                    ) {
                        Text("Скасувати")
                    }
                    
                    // Кнопка "Створити"
                    Button(
                        onClick = {
                            val targetAmount = targetAmountText.toDoubleOrNull() ?: 0.0
                            if (name.isNotBlank() && targetAmount > 0) {
                                onConfirm(name, targetAmount, description, selectedCategory)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading && name.isNotBlank() && targetAmountText.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BalanceGreen
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Створити")
                        }
                    }
                }
            }
        }
    }
}
