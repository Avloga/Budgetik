package com.avloga.budgetik.data.firebase

import com.avloga.budgetik.data.model.Expense
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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

    fun getExpensesFlow(): Flow<List<Expense>> = callbackFlow {
        val listenerRegistration = Firebase.firestore
            .collection("expenses")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val expenses = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Expense::class.java)
                } ?: emptyList()

                trySend(expenses)
            }

        awaitClose { listenerRegistration.remove() }
    }


}
