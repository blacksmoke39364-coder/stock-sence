package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.components.AppHeader
import com.example.ui.components.OperationsTableCard
import com.example.ui.viewmodel.InventoryViewModel

@Composable
fun MoveHistoryScreen(
    viewModel: InventoryViewModel,
    onMenuClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val operations by viewModel.filteredOperations.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedDocType by viewModel.selectedDocType.collectAsState()
    val selectedStatus by viewModel.selectedStatus.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp)
    ) {
        AppHeader(
            title = "Move History",
            subtitle = "Chronological log of all inventory movement events",
            searchQuery = searchQuery,
            onSearchChange = { viewModel.searchQuery.value = it },
            onNewOperationClick = { viewModel.showToast("Use Receipts, Deliveries, or Transfers to log moves.") },
            onMenuClick = onMenuClick
        )

        Spacer(modifier = Modifier.height(14.dp))

        OperationsTableCard(
            operations = operations,
            searchQuery = searchQuery,
            onSearchChange = { viewModel.searchQuery.value = it },
            selectedDocType = selectedDocType,
            onDocTypeChange = { viewModel.selectedDocType.value = it },
            selectedStatus = selectedStatus,
            onStatusChange = { viewModel.selectedStatus.value = it },
            onViewAllClick = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}
