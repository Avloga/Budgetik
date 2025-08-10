package com.avloga.budgetik.ui.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avloga.budgetik.data.firebase.FirebaseFirestoreManager
import com.avloga.budgetik.data.model.Expense
import com.avloga.budgetik.util.Period
import com.avloga.budgetik.util.PeriodUtils
import com.avloga.budgetik.util.AccountType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest

class ExpensesViewModel : ViewModel() {

    // Поточний вибраний рахунок
    private val _selectedAccount = MutableStateFlow(AccountType.CASH)
    val selectedAccount: StateFlow<AccountType> = _selectedAccount

    // Поточний вибраний період
    private val _selectedPeriod = MutableStateFlow(Period.DAY)
    val selectedPeriod: StateFlow<Period> = _selectedPeriod

    // FirebaseFirestoreManager має метод getExpensesFlow(accountType): Flow<List<Expense>>
    val expensesFlow = _selectedAccount.flatMapLatest { accountType ->
        FirebaseFirestoreManager.getExpensesFlow(accountType)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList<Expense>())

    // Всі витрати (для навігації до екрану всіх операцій)
    val allExpensesFlow = FirebaseFirestoreManager.getAllExpensesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList<Expense>())

    private val _initialBalance = MutableStateFlow(1000.0) // стартове значення
    val initialBalance: StateFlow<Double> = _initialBalance

    // Фільтровані витрати за поточним періодом
    val filteredExpensesFlow = combine(expensesFlow, selectedPeriod) { expenses, period ->
        PeriodUtils.filterExpensesByPeriod(expenses, period)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList<Expense>())

    // Відформатована дата для поточного періоду
    val formattedDateFlow = combine(expensesFlow, selectedPeriod) { expenses, period ->
        PeriodUtils.formatDateForPeriod(period, expenses)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    // Баланси для кожного рахунку
    private val _cashBalance = MutableStateFlow(0.0)
    val cashBalance: StateFlow<Double> = _cashBalance

    private val _cardBalance = MutableStateFlow(0.0)
    val cardBalance: StateFlow<Double> = _cardBalance

    // Загальний баланс
    val totalBalanceFlow = combine(_cashBalance, _cardBalance) { cash, card ->
        cash + card
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    fun updateInitialBalance(newBalance: Double) {
        _initialBalance.value = newBalance
    }

    fun setSelectedPeriod(period: Period) {
        _selectedPeriod.value = period
    }

    fun setSelectedPeriodFromString(periodName: String) {
        val period = PeriodUtils.periodFromString(periodName)
        _selectedPeriod.value = period
    }

    fun setSelectedAccount(accountType: AccountType) {
        _selectedAccount.value = accountType
    }

    fun setSelectedAccountFromString(accountName: String) {
        val accountType = when (accountName) {
            "Готівка" -> AccountType.CASH
            "Платіжна картка" -> AccountType.CARD
            "Усі рахунки" -> AccountType.ALL
            "CASH" -> AccountType.CASH
            "CARD" -> AccountType.CARD
            "ALL" -> AccountType.ALL
            else -> AccountType.CASH
        }
        _selectedAccount.value = accountType
    }

    // Оновлює баланси для всіх рахунків
    suspend fun refreshBalances() {
        try {
            val cashBalance = FirebaseFirestoreManager.getBalanceForAccount(AccountType.CASH)
            val cardBalance = FirebaseFirestoreManager.getBalanceForAccount(AccountType.CARD)
            
            _cashBalance.value = cashBalance
            _cardBalance.value = cardBalance
        } catch (e: Exception) {
            // Обробка помилок
        }
    }
}
