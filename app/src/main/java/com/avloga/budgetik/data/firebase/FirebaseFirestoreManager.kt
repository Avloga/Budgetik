package com.avloga.budgetik.data.firebase

import com.avloga.budgetik.data.model.Expense
import com.avloga.budgetik.util.AccountType
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

object FirebaseFirestoreManager {

    private val db = Firebase.firestore

    // Колекції для різних типів рахунків
    private val cashExpensesCollection = db.collection("cash_expenses")
    private val cardExpensesCollection = db.collection("card_expenses")

    private fun com.google.firebase.firestore.DocumentSnapshot.toExpenseWithId(): Expense? {
        val expense = this.toObject(Expense::class.java) ?: return null
        return if (expense.operationId.isBlank()) {
            expense.copy(operationId = this.id)
        } else expense
    }

    /**
     * Отримує колекцію залежно від типу рахунку
     */
    private fun getCollectionForAccountType(accountType: AccountType) = when (accountType) {
        AccountType.CASH -> cashExpensesCollection
        AccountType.CARD -> cardExpensesCollection
        AccountType.ALL -> throw IllegalArgumentException("ALL не підтримується для окремої колекції")
    }

    /**
     * Додає витрату до Firestore залежно від типу рахунку
     *
     * @param expense - об'єкт витрати
     * @param accountType - тип рахунку (CASH або CARD)
     * @param onSuccess - викликається, якщо все ок
     * @param onFailure - викликається з повідомленням, якщо сталася помилка
     */
    suspend fun addExpense(
        expense: Expense,
        accountType: AccountType,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            if (accountType == AccountType.ALL) {
                onFailure("Неможливо додати операцію до 'Усі рахунки'. Оберіть конкретний рахунок.")
                return
            }
            
            val id = if (expense.operationId.isNotBlank()) expense.operationId else UUID.randomUUID().toString()
            getCollectionForAccountType(accountType)
                .document(id)
                .set(expense.copy(operationId = id))
                .await()
            onSuccess()
        } catch (e: Exception) {
            onFailure(e.message ?: "Невідома помилка при додаванні витрати")
        }
    }

    /**
     * Видаляє одну операцію, намагаючись знайти її за полями (без operationId).
     * Якщо знайдено кілька — видаляє першу.
     */
    suspend fun deleteExpense(
        expense: Expense,
        accountType: AccountType,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            if (accountType == AccountType.ALL) {
                onFailure("Неможливо видалити з 'Усі рахунки'. Оберіть конкретний рахунок.")
                return
            }

            val collection = getCollectionForAccountType(accountType)
            if (expense.operationId.isNotBlank()) {
                collection.document(expense.operationId).delete().await()
            } else {
                var query = collection
                    .whereEqualTo("userName", expense.userName)
                    .whereEqualTo("amount", expense.amount)
                    .whereEqualTo("date", expense.date)
                    .whereEqualTo("time", expense.time)
                    .whereEqualTo("comment", expense.comment)
                    .whereEqualTo("type", expense.type)

                // Категорія могла бути null у старих записах; якщо є — додаємо фільтр
                if (expense.category != null) {
                    query = query.whereEqualTo("category", expense.category)
                }

                val snapshot = query.get().await()
                val doc = snapshot.documents.firstOrNull()
                if (doc == null) {
                    onFailure("Не вдалося знайти операцію для видалення")
                    return
                }

                doc.reference.delete().await()
            }
            onSuccess()
        } catch (e: Exception) {
            onFailure(e.message ?: "Помилка видалення")
        }
    }

    /**
     * Отримує Flow з витратами для конкретного типу рахунку
     */
    fun getExpensesFlow(accountType: AccountType): Flow<List<Expense>> = when (accountType) {
        AccountType.CASH -> callbackFlow {
            val listenerRegistration = cashExpensesCollection
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }

                    val expenses = snapshot?.documents?.mapNotNull { doc ->
                        doc.toExpenseWithId()
                    } ?: emptyList()

                    trySend(expenses)
                }

            awaitClose { listenerRegistration.remove() }
        }
        AccountType.CARD -> callbackFlow {
            val listenerRegistration = cardExpensesCollection
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }

                    val expenses = snapshot?.documents?.mapNotNull { doc ->
                        doc.toExpenseWithId()
                    } ?: emptyList()

                    trySend(expenses)
                }

            awaitClose { listenerRegistration.remove() }
        }
        AccountType.ALL -> getAllExpensesFlow()
    }

    /**
     * Отримує Flow з усіма витратами (об'єднані з обох рахунків)
     */
    fun getAllExpensesFlow(): Flow<List<Expense>> = callbackFlow {
        var cashListener: com.google.firebase.firestore.ListenerRegistration? = null
        var cardListener: com.google.firebase.firestore.ListenerRegistration? = null
        
        cashListener = cashExpensesCollection.addSnapshotListener { cashSnapshot, cashError ->
            if (cashError != null) {
                close(cashError)
                return@addSnapshotListener
            }

            cardListener = cardExpensesCollection.addSnapshotListener { cardSnapshot, cardError ->
                if (cardError != null) {
                    close(cardError)
                    return@addSnapshotListener
                }

                val cashExpenses = cashSnapshot?.documents?.mapNotNull { doc ->
                    doc.toExpenseWithId()
                } ?: emptyList()

                val cardExpenses = cardSnapshot?.documents?.mapNotNull { doc ->
                    doc.toExpenseWithId()
                } ?: emptyList()

                val allExpenses = cashExpenses + cardExpenses
                trySend(allExpenses)
            }
        }

        awaitClose { 
            cashListener?.remove()
            cardListener?.remove()
        }
    }

    /**
     * Отримує баланс для конкретного типу рахунку
     */
    suspend fun getBalanceForAccount(accountType: AccountType): Double {
        return try {
            val snapshot = getCollectionForAccountType(accountType).get().await()
            val expenses = snapshot.documents.mapNotNull { doc -> doc.toExpenseWithId() }
            
            expenses.sumOf { expense ->
                if (expense.type == "income") expense.amount else -expense.amount
            }
        } catch (e: Exception) {
            0.0
        }
    }

    /**
     * Отримує загальний баланс (сума всіх рахунків)
     */
    suspend fun getTotalBalance(): Double {
        return try {
            val cashBalance = getBalanceForAccount(AccountType.CASH)
            val cardBalance = getBalanceForAccount(AccountType.CARD)
            cashBalance + cardBalance
        } catch (e: Exception) {
            0.0
        }
    }
}
