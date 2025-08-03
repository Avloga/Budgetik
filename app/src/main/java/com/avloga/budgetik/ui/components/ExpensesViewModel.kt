import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avloga.budgetik.data.firebase.FirebaseFirestoreManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ExpensesViewModel : ViewModel() {

    // FirebaseFirestoreManager має метод getExpensesFlow(): Flow<List<Expense>>
    val expensesFlow = FirebaseFirestoreManager.getExpensesFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _initialBalance = MutableStateFlow(1000.0) // стартове значення
    val initialBalance: StateFlow<Double> = _initialBalance

    fun updateInitialBalance(newBalance: Double) {
        _initialBalance.value = newBalance
    }

}
