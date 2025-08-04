package com.avloga.budgetik.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.avloga.budgetik.data.model.Expense
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseDialog(
    userId: String,
    onDismiss: () -> Unit,
    onSubmit: (Expense) -> Unit
) {
    val context = LocalContext.current
    var amountText by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("outcome") } // витрата за замовчуванням
    val types = listOf("Витрата", "Поповнення")
    var expandedType by remember { mutableStateOf(false) }

    val expenseCategories = listOf(
        "Зв'язок", "Їжа", "Кафе", "Транспорт", "Таксі", "Гігієна", 
        "Улюбленці", "Одяг", "Подарунки", "Спорт", "Здоров'я", 
        "Ігри", "Розваги", "Житло"
    )
    val incomeCategories = listOf("Зарплата", "Подарунок", "Заощадження")
    var selectedCategory by remember { mutableStateOf(expenseCategories.first()) }
    var expandedCategory by remember { mutableStateOf(false) }

    var commentText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val now = Date()
                    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                    val amount = amountText.toDoubleOrNull() ?: 0.0

                    val expense = Expense(
                        userName = userId,
                        amount = amount,
                        category = if (selectedType == "outcome") selectedCategory else null,
                        date = dateFormat.format(now),
                        time = timeFormat.format(now),
                        comment = commentText,
                        type = selectedType
                    )
                    
                    onSubmit(expense)
                }
            ) {
                Text("Додати")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Відмінити")
            }
        },
        title = { Text("Додати операцію") },
        text = {
            Column {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Сума") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(Modifier.height(8.dp))

                // Тип операції Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedType,
                    onExpandedChange = { expandedType = !expandedType }
                ) {
                    OutlinedTextField(
                        value = if (selectedType == "outcome") "Витрата" else "Поповнення",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Тип операції") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType)
                        },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedType,
                        onDismissRequest = { expandedType = false }
                    ) {
                        types.forEachIndexed { index, typeName ->
                            DropdownMenuItem(
                                text = { Text(typeName) },
                                onClick = {
                                    selectedType = if (index == 0) "outcome" else "income"
                                    // Автоматично змінюємо категорію при зміні типу
                                    selectedCategory = if (selectedType == "outcome") expenseCategories.first() else incomeCategories.first()
                                    expandedType = false
                                }
                            )
                        }
                    }
                }


                Spacer(Modifier.height(8.dp))

                // Категорія Dropdown (недоступна, якщо selectedType == income)
                ExposedDropdownMenuBox(
                    expanded = expandedCategory,
                    onExpandedChange = { if (selectedType == "outcome") expandedCategory = !expandedCategory }
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Категорія") },
                        enabled = selectedType == "outcome",
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory)
                        },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCategory,
                        onDismissRequest = { expandedCategory = false }
                    ) {
                        if (selectedType == "outcome") {
                            expenseCategories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category) },
                                    onClick = {
                                        selectedCategory = category
                                        expandedCategory = false
                                    }
                                )
                            }
                        } else {
                            incomeCategories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category) },
                                    onClick = {
                                        selectedCategory = category
                                        expandedCategory = false
                                    }
                                )
                            }
                        }
                    }
                }


                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    label = { Text("Коментар") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}
