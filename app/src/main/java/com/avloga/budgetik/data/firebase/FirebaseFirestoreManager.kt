package com.avloga.budgetik.data.firebase

import android.util.Log
import com.avloga.budgetik.data.model.Group
import com.avloga.budgetik.data.model.Expence
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseFirestoreManager(private val firestore: FirebaseFirestore) {

    private val familiesCollection = firestore.collection("families")

    // Створити групу
    suspend fun createGroup(group: Group): Boolean {
        return try {
            familiesCollection.document(group.familyId).set(group).await()
            Log.d("FirestoreManager", "Групу створено: ${group.familyId}")
            true
        } catch (e: Exception) {
            Log.e("FirestoreManager", "Помилка при створенні групи: ${e.message}")
            false
        }
    }

    // Додати витрату
    suspend fun addExpence(familyId: String, expence: Expence): Boolean {
        return try {
            familiesCollection.document(familyId)
                .collection("expenses")
                .add(expence)
                .await()
            Log.d("FirestoreManager", "Витрату додано: $expence")
            true
        } catch (e: Exception) {
            Log.e("FirestoreManager", "Помилка при додаванні витрати: ${e.message}")
            false
        }
    }

    // Тут можна додавати методи getGroup(), getExpenses() тощо
}
