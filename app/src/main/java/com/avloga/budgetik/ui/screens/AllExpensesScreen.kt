import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.avloga.budgetik.data.model.Expense
import com.avloga.budgetik.ui.components.ExpenseRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllExpensesScreen(
    navController: NavController,
    expenses: List<Expense>
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Усі операції") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Close, contentDescription = "Закрити")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.fillMaxSize()
        ) {
            items(expenses) { expense ->
                ExpenseRow(expense = expense)
                Divider(color = Color.LightGray, thickness = 1.dp)
            }
        }
    }
}
