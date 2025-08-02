package com.avloga.budgetik.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.avloga.budgetik.data.model.User
import kotlinx.coroutines.tasks.await

class FirebaseAuthManager(private val firestore: FirebaseFirestore) {

    private val usersCollection = firestore.collection("users")

    // Реєстрація: перевіряємо, чи такий нік вже є. Якщо ні — створюємо користувача
    suspend fun registerUser(nickname: String, password: String): Boolean {
        val doc = usersCollection.document(nickname).get().await()
        if (doc.exists()) {
            // Користувач з таким нікнеймом вже існує
            return false
        }

        // Створюємо нового користувача
        val user = User(nickname = nickname, password = password, familyId = "")
        usersCollection.document(nickname).set(user).await()
        return true
    }

    // Вхід: перевіряємо наявність користувача і відповідність пароля
    suspend fun loginUser(nickname: String, password: String): Boolean {
        val doc = usersCollection.document(nickname).get().await()
        if (!doc.exists()) {
            // Користувача не знайдено
            return false
        }

        val user = doc.toObject(User::class.java) ?: return false
        return user.password == password
    }
}
