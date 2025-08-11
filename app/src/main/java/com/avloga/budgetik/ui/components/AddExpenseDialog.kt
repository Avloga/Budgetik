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
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseDialog(
    userId: String,
    onDismiss: () -> Unit,
    onSubmit: (Expense) -> Unit,
    presetType: String? = null // "outcome" або "income"; якщо задано — тип зафіксовано
) {
    val context = LocalContext.current
    var amountText by remember { mutableStateOf("") }
    var selectedType by remember(presetType) { mutableStateOf(presetType ?: "outcome") } // витрата за замовчуванням або передвибраний
    val types = listOf("Витрата", "Поповнення")
    var expandedType by remember { mutableStateOf(false) }

    val expenseCategories = remember { Categories.expenseNames() }
    val incomeCategories = remember { Categories.incomeNames() }
    var selectedCategory by remember(selectedType) {
        mutableStateOf(if (selectedType == "outcome") expenseCategories.first() else incomeCategories.first())
    }
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
                    val normalizedAmountText = amountText.trim().replace(',', '.')
                    val amount = normalizedAmountText.toDoubleOrNull() ?: 0.0

                    val expense = Expense(
                        operationId = UUID.randomUUID().toString(),
                        userName = userId,
                        amount = amount,
                        category = selectedCategory,
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
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                Spacer(Modifier.height(8.dp))

                // Тип операції: якщо задано presetType — просто показуємо зафіксоване значення; інакше даємо вибір
                if (presetType != null) {
                    OutlinedTextField(
                        value = if (selectedType == "outcome") "Витрата" else "Поповнення",
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        label = { Text("Тип операції") },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
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
                }


                Spacer(Modifier.height(8.dp))

                // Категорія Dropdown (доступна для обох типів)
                ExposedDropdownMenuBox(
                    expanded = expandedCategory,
                    onExpandedChange = { expandedCategory = !expandedCategory }
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Категорія") },
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
