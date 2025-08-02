package com.avloga.budgetik.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.avloga.budgetik.data.model.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await

object FirebaseAuthManager {
    private val auth = Firebase.auth

    fun signInAs(uid: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        auth.signInAnonymously() // тимчасовий вхід, щоб обійти авторизацію
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onError(it.message ?: "Помилка авторизації")
            }
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
}
