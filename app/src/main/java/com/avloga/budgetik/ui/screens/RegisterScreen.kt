package com.avloga.budgetik.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.avloga.budgetik.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.avloga.budgetik.util.saveUserIdToPrefs


@Composable
fun RegisterScreen(navController: NavController) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    var nickname by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Реєстрація", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = nickname,
            onValueChange = { nickname = it },
            label = { Text("Нікнейм") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (nickname.isNotBlank() && password.isNotBlank()) {
                    db.collection("users")
                        .whereEqualTo("nickname", nickname)
                        .limit(1)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            if (querySnapshot.isEmpty) {
                                val newUser = User(nickname = nickname, password = password, familyId = "")
                                db.collection("users")
                                    .add(newUser)
                                    .addOnSuccessListener { documentReference ->
                                        saveUserIdToPrefs(context, documentReference.id)
                                        navController.navigate("CreateOrJoinBudget") {
                                            popUpTo("Register") { inclusive = true }
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        errorMessage = "Помилка створення користувача: ${e.message}"
                                        Log.e("RegisterScreen", "Add user error", e)
                                    }
                            } else {
                                errorMessage = "Користувач з таким нікнеймом вже існує"
                            }
                        }
                        .addOnFailureListener { e ->
                            errorMessage = "Помилка перевірки користувача: ${e.message}"
                            Log.e("RegisterScreen", "Check nickname error", e)
                        }
                } else {
                    errorMessage = "Заповніть усі поля"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Зареєструватися")
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }
    }
}