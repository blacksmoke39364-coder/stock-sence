package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.data.db.StockSenseDatabase
import com.example.data.db.populateInitialData
import com.example.data.repository.InventoryRepository
import com.example.ui.StockSenseApp
import com.example.ui.theme.StockSenseTheme
import com.example.ui.viewmodel.InventoryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var database: StockSenseDatabase
    private lateinit var repository: InventoryRepository
    private lateinit var viewModel: InventoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        database = StockSenseDatabase.getDatabase(this, lifecycleScope)
        val dao = database.stockSenseDao()
        repository = InventoryRepository(dao)
        viewModel = InventoryViewModel(repository)

        // Ensure database is populated with initial seed data on startup if empty
        lifecycleScope.launch(Dispatchers.IO) {
            val users = dao.getAllUsers().first()
            if (users.isEmpty()) {
                populateInitialData(dao)
            }
        }

        setContent {
            StockSenseTheme {
                StockSenseApp(viewModel = viewModel)
            }
        }
    }
}
