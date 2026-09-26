package com.example.ui.screens

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
import com.example.ui.components.CreateAdjustmentDialog
import com.example.ui.components.CreateDeliveryDialog
import com.example.ui.components.CreateProductDialog
import com.example.ui.components.CreateReceiptDialog
import com.example.ui.components.CreateTransferDialog
import com.example.ui.components.OperationSelectionDialog
import com.example.ui.components.OperationsTableCard
import com.example.ui.components.RevenueAreaChartCard
import com.example.ui.components.StatKpiRow
import com.example.ui.components.TrafficDonutCard
import com.example.ui.viewmodel.InventoryViewModel
import com.example.ui.viewmodel.Screen

@Composable
fun DashboardScreen(
    viewModel: InventoryViewModel,
    onMenuClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val kpis by viewModel.kpis.collectAsState()
    val operations by viewModel.filteredOperations.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedDocType by viewModel.selectedDocType.collectAsState()
    val selectedStatus by viewModel.selectedStatus.collectAsState()
    val products by viewModel.products.collectAsState()
    val warehouses by viewModel.warehouses.collectAsState()
    val locations by viewModel.locations.collectAsState()
    val categories by viewModel.categories.collectAsState()

    var showActionMenu by remember { mutableStateOf(false) }
    var showCreateProduct by remember { mutableStateOf(false) }
    var showCreateReceipt by remember { mutableStateOf(false) }
    var showCreateDelivery by remember { mutableStateOf(false) }
    var showCreateTransfer by remember { mutableStateOf(false) }
    var showCreateAdjustment by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp)
    ) {
        // Topbar
        AppHeader(
            title = "Dashboard",
            subtitle = "Inventory overview - Updated 2 min ago",
            searchQuery = searchQuery,
            onSearchChange = { viewModel.searchQuery.value = it },
            onNewOperationClick = { showActionMenu = true },
            onMenuClick = onMenuClick,
            onNotificationsClick = {
                viewModel.showToast("StockSense: All warehouse locations active and synchronized.")
            }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 6-Up KPI Stat Row
        StatKpiRow(
            kpis = kpis,
            onKpiClick = { target ->
                when (target) {
                    "stock" -> viewModel.navigateTo(Screen.Products)
                    "low_stock", "out_of_stock" -> viewModel.navigateTo(Screen.ReorderRules)
                    "receipts" -> viewModel.navigateTo(Screen.Receipts)
                    "deliveries" -> viewModel.navigateTo(Screen.Deliveries)
                    "transfers" -> viewModel.navigateTo(Screen.Transfers)
                }
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 8 / 4 Split: Area Chart + Traffic Donut Chart
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            RevenueAreaChartCard(modifier = Modifier.weight(1.8f))
            TrafficDonutCard(modifier = Modifier.weight(1.2f))
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Dashboard Operations Table
        OperationsTableCard(
            operations = operations,
            searchQuery = searchQuery,
            onSearchChange = { viewModel.searchQuery.value = it },
            selectedDocType = selectedDocType,
            onDocTypeChange = { viewModel.selectedDocType.value = it },
            selectedStatus = selectedStatus,
            onStatusChange = { viewModel.selectedStatus.value = it },
            onViewAllClick = { viewModel.navigateTo(Screen.MoveHistory) },
            modifier = Modifier.fillMaxWidth()
        )
    }

    // Workflow Selection & Modal Forms
    if (showActionMenu) {
        OperationSelectionDialog(
            onDismiss = { showActionMenu = false },
            onSelectReceipt = { showCreateReceipt = true },
            onSelectDelivery = { showCreateDelivery = true },
            onSelectTransfer = { showCreateTransfer = true },
            onSelectAdjustment = { showCreateAdjustment = true },
            onSelectProduct = { showCreateProduct = true }
        )
    }

    if (showCreateProduct) {
        CreateProductDialog(
            categories = categories,
            warehouses = warehouses,
            locations = locations,
            onDismiss = { showCreateProduct = false },
            onSubmit = { name, sku, catId, unit, initStock, whId, locId, reorderLvl, reorderQty, desc ->
                viewModel.createProduct(
                    name = name,
                    sku = sku,
                    categoryId = catId,
                    unit = unit,
                    initialStock = initStock,
                    warehouseId = whId,
                    locationId = locId,
                    reorderLevel = reorderLvl,
                    reorderQuantity = reorderQty,
                    description = desc,
                    onSuccess = { showCreateProduct = false }
                )
            }
        )
    }

    if (showCreateReceipt) {
        CreateReceiptDialog(
            products = products,
            warehouses = warehouses,
            locations = locations,
            onDismiss = { showCreateReceipt = false },
            onSubmit = { supplier, whId, locId, prodId, qty, unit, notes ->
                viewModel.createReceipt(supplier, whId, locId, prodId, qty, unit, notes) {
                    showCreateReceipt = false
                }
            }
        )
    }

    if (showCreateDelivery) {
        CreateDeliveryDialog(
            products = products,
            warehouses = warehouses,
            locations = locations,
            onDismiss = { showCreateDelivery = false },
            onSubmit = { customer, whId, locId, prodId, qty, unit, notes ->
                viewModel.createDelivery(customer, whId, locId, prodId, qty, unit, notes) {
                    showCreateDelivery = false
                }
            }
        )
    }

    if (showCreateTransfer) {
        CreateTransferDialog(
            products = products,
            warehouses = warehouses,
            locations = locations,
            onDismiss = { showCreateTransfer = false },
            onSubmit = { srcWh, srcLoc, dstWh, dstLoc, prodId, qty, notes ->
                viewModel.createTransfer(srcWh, srcLoc, dstWh, dstLoc, prodId, qty, notes) {
                    showCreateTransfer = false
                }
            }
        )
    }

    if (showCreateAdjustment) {
        CreateAdjustmentDialog(
            products = products,
            warehouses = warehouses,
            locations = locations,
            onDismiss = { showCreateAdjustment = false },
            onSubmit = { whId, locId, prodId, physCount, reason ->
                viewModel.createAdjustment(whId, locId, prodId, physCount, reason) {
                    showCreateAdjustment = false
                }
            }
        )
    }
}
