package com.avloga.budgetik.ui.screens // Змініть на ваш пакет

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await // Для await
import com.avloga.budgetik.util.saveUserIdToPrefs


// --- Припустіть, що ці функції вже існують у відповідних файлах ---
// import com.avloga.budgetik.screens.saveUserIdToPrefs
// import com.avloga.budgetik.screens.findUserByNickname // Цю функцію треба буде додати, або зробити частиною FirebaseAuthManager

@Composable
fun LoginScreen(
    navController: NavController, // Приймаємо NavController
    auth: FirebaseAuth = Firebase.auth, // Ініціалізуємо Firebase Auth
    db: FirebaseFirestore = Firebase.firestore // Ініціалізуємо Firestore
) {
    var nickname by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    // Важливо: якщо ви хочете перемикатись між Входом/Реєстрацією в межах одного екрану,
    // Вам потрібен стан isLoginMode. Зазвичай це робиться за допомогою Navigation.
    // Для простоти, ми зробимо окремі екрани Login та Register.
    // Але якщо ви хочете саме так, як у вас, то потрібен буде ще один стан.

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Вхід", // Тільки вхід на цьому екрані, реєстрація буде окремо
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = nickname,
            onValueChange = { nickname = it },
            label = { Text("Нікнейм") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (nickname.isNotBlank() && password.isNotBlank()) {
                    db.collection("users")
                        .whereEqualTo("nickname", nickname)
                        .whereEqualTo("password", password)
                        .limit(1)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            if (!querySnapshot.isEmpty) {
                                val document = querySnapshot.documents.first()
                                val userId = document.id
                                saveUserIdToPrefs(context, userId)
                                navController.navigate("MainScreen") {
                                    popUpTo("Login") { inclusive = true }
                                }
                            } else {
                                errorMessage = "Невірний нікнейм або пароль"
                            }
                        }
                        .addOnFailureListener { e ->
                            errorMessage = "Помилка під час входу: ${e.message}"
                            Log.e("LoginScreen", "Login error", e)
                        }
                } else {
                    errorMessage = "Будь ласка, заповніть обидва поля"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Увійти")
        }

        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = { navController.navigate("Register") }) { // Перехід на екран реєстрації
            Text("Немає акаунту? Зареєструватися")
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}

// --- Потрібно мати ці функції в файлі Navigation.kt або в іншому доступному місці ---

// Функція для пошуку користувача за нікнеймом у Firestore
// (краще її перенести до файлу з навігацією або viewModel)
fun findUserByNickname(
    nickname: String,
    db: FirebaseFirestore,
    onResult: (String?) -> Unit
) {
    db.collection("users")
        .whereEqualTo("displayName", nickname)
        .limit(1)
        .get()
        .addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                val document = querySnapshot.documents.first()
                val email = document.getString("email")
                Log.d("LoginScreen", "Found user with email: $email")
                onResult(email)
            } else {
                Log.d("LoginScreen", "User with nickname '$nickname' not found")
                onResult(null)
            }
        }
        .addOnFailureListener { e ->
            Log.e("LoginScreen", "Error searching for nickname", e)
            onResult(null)
        }
}

// Функція для збереження UID користувача в SharedPreferences (як і раніше)
// (потрібно імпортувати з відповідного файлу, або скопіювати сюди)

// --- Додайте Preview, якщо потрібно ---
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    // Для прев'ю вам потрібен NavController, який можна створити за допомогою rememberNavController()
    // Але для Firebase операцій прев'ю може бути складним.
    // Тому краще просто показати UI структуру.
    // LoginScreen(navController = rememberNavController()) // Якщо є NavController
    // Заглушка для прев'ю:
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Login Screen Preview")
    }
}