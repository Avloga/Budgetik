package com.avloga.budgetik.data.model

import com.google.firebase.Timestamp

data class Expence(
    val amount: Double = 0.0,
    val category: String = "",
    val description: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val createdBy: String = ""
)
