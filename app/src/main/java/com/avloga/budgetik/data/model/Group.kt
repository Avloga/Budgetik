package com.avloga.budgetik.data.model

data class Group(
    val familyId: String = "",
    val members: List<String> = listOf(),  // список нікнеймів користувачів
    val totalBalance: Double = 0.0,
    val categories: List<String> = listOf("Food", "Rent", "Travel") // можна розширити
)
