package com.avloga.budgetik.data.firebase

import android.util.Log
import com.avloga.budgetik.data.model.Group
import com.avloga.budgetik.data.model.Expence
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

object FirebaseFirestoreManager {
    private val db = Firebase.firestore

    fun getUserData(uid: String, onSuccess: (UserData) -> Unit, onError: (String) -> Unit) {
        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener { doc ->
                val name = doc.getString("name") ?: "?"
                val budget = doc.getDouble("budget") ?: 0.0
                onSuccess(UserData(name, budget))
            }
            .addOnFailureListener {
                onError(it.message ?: "Помилка отримання даних")
            }
    }
}

data class UserData(val name: String, val budget: Double)
