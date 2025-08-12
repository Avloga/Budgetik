package com.avloga.budgetik.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class SavingsBank(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val targetAmount: Double = 0.0,
    val currentAmount: Double = 0.0,
    val color: String = "#4CAF50",
    val userId: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now(),
    @PropertyName("active")
    val isActive: Boolean = true,
    val description: String = "",
    val category: String = "Загальне"
)

data class SavingsData(
    val totalSavings: Double,
    val banks: List<SavingsBank>
)

// DTO для створення нової банки
data class CreateSavingsBankRequest(
    val name: String,
    val targetAmount: Double,
    val description: String = "",
    val category: String = "Загальне",
    val color: String = "#4CAF50"
)

// DTO для оновлення банки
data class UpdateSavingsBankRequest(
    val name: String? = null,
    val targetAmount: Double? = null,
    val currentAmount: Double? = null,
    val description: String? = null,
    val category: String? = null,
    val color: String? = null,
    @PropertyName("active")
    val isActive: Boolean? = null
)
