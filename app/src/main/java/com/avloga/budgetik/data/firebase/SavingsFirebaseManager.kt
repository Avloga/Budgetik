package com.avloga.budgetik.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.avloga.budgetik.data.model.*
import kotlinx.coroutines.tasks.await
import com.google.firebase.Timestamp

class SavingsFirebaseManager {
    private val db = FirebaseFirestore.getInstance()
    private val collectionName = "bank_savings"

    // Отримати всі накопичення користувача
    suspend fun getUserSavings(userId: String): List<SavingsBank> {
        return try {
            println("🔍 Запитую накопичення для користувача: $userId")
            println("📍 Колекція: $collectionName")
            
            // Спочатку отримаємо всі документи без фільтрів для діагностики
            val allDocs = db.collection(collectionName).get().await()
            println("📋 Всього документів в колекції: ${allDocs.documents.size}")
            
            allDocs.documents.forEach { doc ->
                val bank = doc.toObject(SavingsBank::class.java)
                println("📄 Документ ${doc.id}: userId='${bank?.userId}', isActive=${bank?.isActive}, name='${bank?.name}'")
            }
            
            // Тепер робимо основний запит
            println("🔍 Виконую основний запит з фільтрами...")
            println("🔍 Шукаю документи з userId='$userId' та active=true")
            
            // Спочатку спробуємо без orderBy
            println("🔍 Тестую запит без orderBy...")
            val testSnapshot = db.collection(collectionName)
                .whereEqualTo("userId", userId)
                .whereEqualTo("active", true)
                .get()
                .await()
            println("📊 Тестовий запит без orderBy: ${testSnapshot.documents.size} документів")
            
            // Тимчасово використовуємо тестовий запит без orderBy
            // Основний запит з orderBy (закоментовано поки не створиться індекс)
            /*
            val snapshot = db.collection(collectionName)
                .whereEqualTo("userId", userId)
                .whereEqualTo("active", true)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            */
            
            // Використовуємо тестовий запит
            val snapshot = testSnapshot
            
            println("📊 Отримано документів з фільтрами: ${snapshot.documents.size}")
            
            val banks = snapshot.documents.mapNotNull { doc ->
                val bank = doc.toObject(SavingsBank::class.java)
                println("✅ Знайдено банку: ${bank?.name}")
                bank?.copy(id = doc.id)
            }
            
            // Сортуємо на клієнті замість сервера
            val sortedBanks = banks.sortedByDescending { it.createdAt }
            
            println("✅ Знайдено активних банок: ${sortedBanks.size}")
            sortedBanks.forEach { bank ->
                println("🏦 ${bank.name}: ${bank.currentAmount}/${bank.targetAmount} ₴ (isActive: ${bank.isActive})")
            }
            
            sortedBanks
        } catch (e: Exception) {
            println("❌ Помилка отримання накопичень: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }

    // Створити нову банку накопичень
    suspend fun createSavingsBank(
        userId: String,
        request: CreateSavingsBankRequest,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            println("🔨 SavingsFirebaseManager: Створюю банку '${request.name}' для userId: $userId")
            
            val savingsBank = SavingsBank(
                name = request.name,
                targetAmount = request.targetAmount,
                currentAmount = 0.0,
                description = request.description,
                category = request.category,
                color = request.color,
                userId = userId,
                createdAt = Timestamp.now(),
                updatedAt = Timestamp.now(),
                isActive = true
            )
            
            println("📝 SavingsFirebaseManager: Створюю об'єкт банки:")
            println("   - name: ${savingsBank.name}")
            println("   - userId: '${savingsBank.userId}'")
            println("   - isActive: ${savingsBank.isActive}")
            println("   - createdAt: ${savingsBank.createdAt}")

            println("📝 SavingsFirebaseManager: Зберігаю банку в Firestore...")
            val docRef = db.collection(collectionName)
                .add(savingsBank)
                .await()

            println("✅ SavingsFirebaseManager: Банка '${request.name}' створена з ID: ${docRef.id}")
            onSuccess()
        } catch (e: Exception) {
            println("❌ SavingsFirebaseManager: Помилка створення банки '${request.name}': ${e.message}")
            e.printStackTrace()
            onFailure("Помилка створення банки: ${e.message}")
        }
    }

    // Оновити банку накопичень
    suspend fun updateSavingsBank(
        bankId: String,
        request: UpdateSavingsBankRequest,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            val updates = mutableMapOf<String, Any>()
            
            request.name?.let { updates["name"] = it }
            request.targetAmount?.let { updates["targetAmount"] = it }
            request.currentAmount?.let { updates["currentAmount"] = it }
            request.description?.let { updates["description"] = it }
            request.category?.let { updates["category"] = it }
            request.color?.let { updates["color"] = it }
            request.isActive?.let { updates["active"] = it } // Використовуємо "active" для Firestore
            
            updates["updatedAt"] = Timestamp.now()

            db.collection(collectionName)
                .document(bankId)
                .update(updates)
                .await()

            onSuccess()
        } catch (e: Exception) {
            onFailure("Помилка оновлення банки: ${e.message}")
        }
    }

    // Додати суму до накопичення
    suspend fun addToSavings(
        bankId: String,
        amount: Double,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            val bankRef = db.collection(collectionName).document(bankId)
            
            db.runTransaction { transaction ->
                val bankDoc = transaction.get(bankRef)
                val currentAmount = bankDoc.getDouble("currentAmount") ?: 0.0
                val newAmount = currentAmount + amount
                
                transaction.update(bankRef, mapOf(
                    "currentAmount" to newAmount,
                    "updatedAt" to Timestamp.now()
                ))
            }.await()

            onSuccess()
        } catch (e: Exception) {
            onFailure("Помилка додавання суми: ${e.message}")
        }
    }

    // Видалити банку накопичень (деактивувати)
    suspend fun deleteSavingsBank(
        bankId: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            db.collection(collectionName)
                .document(bankId)
                .update(mapOf(
                    "active" to false, // Використовуємо "active" для Firestore
                    "updatedAt" to Timestamp.now()
                ))
                .await()

            onSuccess()
        } catch (e: Exception) {
            onFailure("Помилка видалення банки: ${e.message}")
        }
    }

    // Отримати статистику накопичень
    suspend fun getSavingsStats(userId: String): SavingsData? {
        return try {
            val banks = getUserSavings(userId)
            val totalSavings = banks.sumOf { it.currentAmount }
            
            SavingsData(
                totalSavings = totalSavings,
                banks = banks
            )
        } catch (e: Exception) {
            println("Помилка отримання статистики: ${e.message}")
            null
        }
    }

    // Заповнити базу тестовими даними
    suspend fun populateWithTestData(userId: String) {
        println("🏗️ SavingsFirebaseManager: Починаю створення тестових банок для userId: $userId")
        
        val testBanks = listOf(
            CreateSavingsBankRequest("На подорож", 50000.0, "Накопичення на подорож за кордон", "Подорожі"),
            CreateSavingsBankRequest("На машину", 200000.0, "Накопичення на нову машину", "Транспорт"),
            CreateSavingsBankRequest("На квартиру", 1000000.0, "Перший внесок на квартиру", "Нерухомість"),
            CreateSavingsBankRequest("На освіту", 100000.0, "Накопичення на навчання", "Освіта"),
            CreateSavingsBankRequest("На ремонт", 75000.0, "Ремонт квартири", "Будинок"),
            CreateSavingsBankRequest("На свято", 25000.0, "Накопичення на святкування", "Розваги"),
            CreateSavingsBankRequest("На хобі", 15000.0, "Накопичення на хобі", "Хобі"),
            CreateSavingsBankRequest("На подарунки", 10000.0, "Накопичення на подарунки", "Подарунки"),
            CreateSavingsBankRequest("На запас", 50000.0, "Подушка безпеки", "Запас"),
            CreateSavingsBankRequest("На інвестиції", 100000.0, "Накопичення для інвестування", "Інвестиції")
        )

        println("📋 SavingsFirebaseManager: Створюю ${testBanks.size} тестових банок...")
        
        testBanks.forEach { bank ->
            println("🏦 SavingsFirebaseManager: Створюю банку: ${bank.name}")
            createSavingsBank(
                userId = userId,
                request = bank,
                onSuccess = { println("✅ Створено банку: ${bank.name}") },
                onFailure = { error -> println("❌ Помилка створення банки ${bank.name}: $error") }
            )
        }

        println("💰 SavingsFirebaseManager: Додаю випадкові суми до накопичень...")
        
        // Додати випадкові суми до накопичень
        val existingBanks = getUserSavings(userId)
        println("📊 SavingsFirebaseManager: Знайдено ${existingBanks.size} банок для додавання сум")
        
        existingBanks.forEach { bank ->
            val randomAmount = (1000..50000).random().toDouble()
            println("💸 SavingsFirebaseManager: Додаю ${randomAmount} до банки ${bank.name}")
            addToSavings(
                bankId = bank.id,
                amount = randomAmount,
                onSuccess = { println("✅ Додано ${randomAmount} до ${bank.name}") },
                onFailure = { error -> println("❌ Помилка додавання суми до ${bank.name}: $error") }
            )
        }
        
        println("🎉 SavingsFirebaseManager: Тестові дані створено успішно!")
    }
}
