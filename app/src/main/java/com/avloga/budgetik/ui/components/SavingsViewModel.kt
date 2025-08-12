package com.avloga.budgetik.ui.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avloga.budgetik.data.firebase.SavingsFirebaseManager
import com.avloga.budgetik.data.model.SavingsBank
import com.avloga.budgetik.data.model.SavingsData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SavingsViewModel : ViewModel() {
    private val savingsManager = SavingsFirebaseManager()
    
    private val _savingsData = MutableStateFlow<SavingsData?>(null)
    val savingsData: StateFlow<SavingsData?> = _savingsData.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _showAddBankDialog = MutableStateFlow(false)
    val showAddBankDialog: StateFlow<Boolean> = _showAddBankDialog.asStateFlow()
    
    // Завантажити накопичення користувача
    fun loadUserSavings(userId: String) {
        viewModelScope.launch {
            println("🔄 ViewModel: Завантажую накопичення для користувача: $userId")
            _isLoading.value = true
            _error.value = null
            
            try {
                val data = savingsManager.getSavingsStats(userId)
                println("📊 ViewModel: Отримано дані: totalSavings=${data?.totalSavings}, banks=${data?.banks?.size}")
                _savingsData.value = data
            } catch (e: Exception) {
                println("❌ ViewModel: Помилка завантаження: ${e.message}")
                e.printStackTrace()
                _error.value = "Помилка завантаження: ${e.message}"
            } finally {
                _isLoading.value = false
                println("✅ ViewModel: Завантаження завершено")
            }
        }
    }
    
    // Створити нову банку
    fun createSavingsBank(
        userId: String,
        name: String,
        targetAmount: Double,
        description: String = "",
        category: String = "Загальне"
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val request = com.avloga.budgetik.data.model.CreateSavingsBankRequest(
                name = name,
                targetAmount = targetAmount,
                description = description,
                category = category
            )
            
            savingsManager.createSavingsBank(
                userId = userId,
                request = request,
                onSuccess = {
                    loadUserSavings(userId) // Перезавантажити дані
                    _showAddBankDialog.value = false
                },
                onFailure = { error ->
                    _error.value = error
                }
            )
            
            _isLoading.value = false
        }
    }
    
    // Додати суму до накопичення
    fun addToSavings(
        bankId: String,
        amount: Double,
        userId: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            savingsManager.addToSavings(
                bankId = bankId,
                amount = amount,
                onSuccess = {
                    loadUserSavings(userId) // Перезавантажити дані
                },
                onFailure = { error ->
                    _error.value = error
                }
            )
            
            _isLoading.value = false
        }
    }
    
    // Заповнити тестовими даними
    fun populateWithTestData(userId: String) {
        viewModelScope.launch {
            println("🧪 ViewModel: Починаю створення тестових даних для userId: $userId")
            _isLoading.value = true
            _error.value = null
            
            try {
                savingsManager.populateWithTestData(userId)
                println("✅ ViewModel: Тестові дані створено, перезавантажую...")
                loadUserSavings(userId) // Перезавантажити дані
            } catch (e: Exception) {
                println("❌ ViewModel: Помилка створення тестових даних: ${e.message}")
                e.printStackTrace()
                _error.value = "Помилка заповнення тестовими даними: ${e.message}"
            } finally {
                _isLoading.value = false
                println("🏁 ViewModel: Створення тестових даних завершено")
            }
        }
    }
    
    // Показати/приховати діалог додавання банки
    fun toggleAddBankDialog() {
        _showAddBankDialog.value = !_showAddBankDialog.value
    }
    
    // Очистити помилку
    fun clearError() {
        _error.value = null
    }
    
    // Отримати загальну суму накопичень
    fun getTotalSavings(): Double {
        return _savingsData.value?.totalSavings ?: 0.0
    }
    
    // Отримати список банок
    fun getBanks(): List<SavingsBank> {
        return _savingsData.value?.banks ?: emptyList()
    }
}
