package com.avloga.budgetik.data.firebase

import com.avloga.budgetik.data.model.Expense
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

object FirebaseFirestoreManager {

    private val db = Firebase.firestore

    // Колекція для збереження витрат
    private val expensesCollection = db.collection("expenses")

    /**
     * Додає витрату до Firestore
     *
     * @param expense - об'єкт витрати
     * @param onSuccess - викликається, якщо все ок
     * @param onFailure - викликається з повідомленням, якщо сталася помилка
     */
    suspend fun addExpense(
        expense: Expense,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            expensesCollection
                .add(expense)
                .await()
            onSuccess()
        } catch (e: Exception) {
            onFailure(e.message ?: "Невідома помилка при додаванні витрати")
        }
    }

    // Можеш додати інші методи, наприклад для отримання всіх витрат:
    /*
    suspend fun getExpenses(): List<Expense> {
        return try {
            expensesCollection
                .get()
                .await()
                .toObjects(Expense::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
    */
}
