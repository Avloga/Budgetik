package com.avloga.budgetik.data.model

import java.io.Serializable
data class Expense(
    val userName: String = "",
    val amount: Double = 0.0,
    val category: String? = "",
    val date: String = "",
    val time: String = "",
    val comment: String = "",
    val type: String = "" // "income" або "outcome"
) : Serializable
