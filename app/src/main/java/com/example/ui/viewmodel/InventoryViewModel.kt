package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AdjustmentEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.DashboardKpis
import com.example.data.model.DeliveryEntity
import com.example.data.model.LocationEntity
import com.example.data.model.OperationSummary
import com.example.data.model.ProductEntity
import com.example.data.model.ProductWithStock
import com.example.data.model.ReceiptEntity
import com.example.data.model.StockByLocationItem
import com.example.data.model.StockLedgerEntity
import com.example.data.model.TransferEntity
import com.example.data.model.UserEntity
import com.example.data.model.WarehouseEntity
import com.example.data.repository.InventoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen {
    object Dashboard : Screen()
    object Products : Screen()
    data class ProductDetail(val productId: String) : Screen()
    object Warehouses : Screen()
    object Categories : Screen()
    object Receipts : Screen()
    object Deliveries : Screen()
    object Transfers : Screen()
    object Adjustments : Screen()
    object StockLedger : Screen()
    object MoveHistory : Screen()
    object ReorderRules : Screen()
    object Profile : Screen()
}

class InventoryViewModel(
    private val repository: InventoryRepository
) : ViewModel() {

    // --- User Session ---
    private val _currentUser = MutableStateFlow<UserEntity?>(
        UserEntity(
            id = "user-alex",
            email = "alex@stocksense.io",
            passwordHash = "manager123",
            fullName = "Alex Vance",
            role = "INVENTORY_MANAGER",
            avatarInitials = "AV"
        )
    )
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    // --- Navigation & Backstack ---
    private val _screenStack = MutableStateFlow<List<Screen>>(listOf(Screen.Dashboard))
    val currentScreen: StateFlow<Screen> = MutableStateFlow<Screen>(Screen.Dashboard).apply {
        viewModelScope.launch {
            _screenStack.collect { stack ->
                value = stack.lastOrNull() ?: Screen.Dashboard
            }
        }
    }

    fun navigateTo(screen: Screen) {
        val current = _screenStack.value
        if (current.lastOrNull() != screen) {
            _screenStack.value = current + screen
        }
    }

    fun navigateBack(): Boolean {
        val current = _screenStack.value
        if (current.size > 1) {
            _screenStack.value = current.dropLast(1)
            return true
        }
        return false
    }

    // --- Toast / Feedback Banner ---
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    // --- Dynamic Search & Filters ---
    val searchQuery = MutableStateFlow("")
    val selectedDocType = MutableStateFlow("All") // All, Receipt, Delivery, Transfer, Adjustment
    val selectedStatus = MutableStateFlow("All") // All, Draft, Ready, Picked, Packed, Done
    val selectedWarehouseFilter = MutableStateFlow<String?>("All")

    // --- Streams from Repository ---
    val kpis: StateFlow<DashboardKpis> = repository.dashboardKpis
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardKpis())

    val products: StateFlow<List<ProductWithStock>> = repository.productsWithStock
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val warehouses: StateFlow<List<WarehouseEntity>> = repository.getAllWarehouses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val locations: StateFlow<List<LocationEntity>> = repository.getAllLocations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<CategoryEntity>> = repository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val receipts: StateFlow<List<ReceiptEntity>> = repository.getAllReceipts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val deliveries: StateFlow<List<DeliveryEntity>> = repository.getAllDeliveries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val transfers: StateFlow<List<TransferEntity>> = repository.getAllTransfers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adjustments: StateFlow<List<AdjustmentEntity>> = repository.getAllAdjustments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val ledgerEntries: StateFlow<List<StockLedgerEntity>> = repository.getAllLedger()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Recent Operations
    val filteredOperations: StateFlow<List<OperationSummary>> = combine(
        repository.recentOperations,
        searchQuery,
        selectedDocType,
        selectedStatus,
        selectedWarehouseFilter
    ) { ops, query, docType, status, whFilter ->
        ops.filter { op ->
            val matchesQuery = query.isBlank() ||
                op.reference.contains(query, ignoreCase = true) ||
                op.productName.contains(query, ignoreCase = true) ||
                op.warehouseName.contains(query, ignoreCase = true) ||
                op.locationName.contains(query, ignoreCase = true)

            val matchesDocType = docType == "All" || op.type.equals(docType, ignoreCase = true)
            val matchesStatus = status == "All" || op.status.equals(status, ignoreCase = true)
            val matchesWh = whFilter == null || whFilter == "All" || op.warehouseName.equals(whFilter, ignoreCase = true)

            matchesQuery && matchesDocType && matchesStatus && matchesWh
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Product Detail State
    private val _selectedProductLocations = MutableStateFlow<List<StockByLocationItem>>(emptyList())
    val selectedProductLocations: StateFlow<List<StockByLocationItem>> = _selectedProductLocations.asStateFlow()

    fun loadProductLocations(productId: String) {
        viewModelScope.launch {
            _selectedProductLocations.value = repository.getStockByLocationForProduct(productId)
        }
    }

    // --- Authentication Actions ---
    fun login(email: String, role: String) {
        viewModelScope.launch {
            val user = repository.getUserByEmail(email)
            if (user != null) {
                _currentUser.value = user
                showToast("Logged in as ${user.fullName}")
            } else {
                val initials = email.take(2).uppercase()
                val newUser = UserEntity(
                    email = email,
                    passwordHash = "demo123",
                    fullName = email.substringBefore("@").replace(".", " ").capitalizeWords(),
                    role = role,
                    avatarInitials = initials
                )
                repository.registerUser(newUser)
                _currentUser.value = newUser
                showToast("Account created and logged in")
            }
        }
    }

    fun switchDemoAccount(isManager: Boolean) {
        viewModelScope.launch {
            if (isManager) {
                val user = repository.getUserByEmail("alex@stocksense.io")
                if (user != null) _currentUser.value = user
                showToast("Switched to Alex Vance (Inventory Manager)")
            } else {
                val user = repository.getUserByEmail("sam@stocksense.io")
                if (user != null) _currentUser.value = user
                showToast("Switched to Sam Rivera (Warehouse Staff)")
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        showToast("Signed out successfully")
    }

    // --- Operations ---
    fun createProduct(
        name: String,
        sku: String,
        categoryId: String,
        unit: String,
        initialStock: Double,
        warehouseId: String?,
        locationId: String?,
        reorderLevel: Double,
        reorderQuantity: Double,
        description: String,
        onSuccess: () -> Unit
    ) {
        val userName = _currentUser.value?.fullName ?: "Alex Vance"
        viewModelScope.launch {
            val result = repository.createProduct(
                name = name,
                sku = sku,
                categoryId = categoryId,
                unitOfMeasure = unit,
                initialStock = initialStock,
                warehouseId = warehouseId,
                locationId = locationId,
                reorderLevel = reorderLevel,
                reorderQuantity = reorderQuantity,
                description = description,
                userName = userName
            )
            result.onSuccess {
                showToast("Product $name ($sku) created successfully")
                onSuccess()
            }.onFailure {
                showToast(it.message ?: "Failed to create product")
            }
        }
    }

    fun createReceipt(
        supplier: String,
        warehouseId: String,
        locationId: String,
        productId: String,
        quantity: Double,
        unit: String,
        notes: String,
        onSuccess: () -> Unit
    ) {
        val userName = _currentUser.value?.fullName ?: "Staff"
        viewModelScope.launch {
            val res = repository.createReceipt(supplier, warehouseId, locationId, productId, quantity, unit, notes, userName)
            res.onSuccess {
                showToast("Receipt ${it.receiptNumber} created in Draft")
                onSuccess()
            }.onFailure {
                showToast(it.message ?: "Failed to create receipt")
            }
        }
    }

    fun advanceReceipt(receiptId: String) {
        viewModelScope.launch {
            repository.advanceReceiptStatus(receiptId)
            showToast("Receipt status updated to READY")
        }
    }

    fun validateReceipt(receiptId: String) {
        val userName = _currentUser.value?.fullName ?: "Staff"
        viewModelScope.launch {
            val res = repository.validateReceipt(receiptId, userName)
            res.onSuccess {
                showToast("Receipt validated successfully. Stock added!")
            }.onFailure {
                showToast(it.message ?: "Failed to validate receipt")
            }
        }
    }

    fun createDelivery(
        customer: String,
        warehouseId: String,
        locationId: String,
        productId: String,
        quantity: Double,
        unit: String,
        notes: String,
        onSuccess: () -> Unit
    ) {
        val userName = _currentUser.value?.fullName ?: "Staff"
        viewModelScope.launch {
            val res = repository.createDelivery(customer, warehouseId, locationId, productId, quantity, unit, notes, userName)
            res.onSuccess {
                showToast("Delivery ${it.deliveryNumber} created")
                onSuccess()
            }.onFailure {
                showToast(it.message ?: "Failed to create delivery")
            }
        }
    }

    fun advanceDelivery(deliveryId: String) {
        val userName = _currentUser.value?.fullName ?: "Staff"
        viewModelScope.launch {
            val res = repository.advanceDeliveryStatus(deliveryId, userName)
            res.onSuccess { next ->
                if (next == "DONE") {
                    showToast("Delivery completed! Stock deducted successfully.")
                } else {
                    showToast("Delivery moved to $next")
                }
            }.onFailure {
                showToast(it.message ?: "Error advancing delivery")
            }
        }
    }

    fun createTransfer(
        srcWh: String,
        srcLoc: String,
        dstWh: String,
        dstLoc: String,
        productId: String,
        quantity: Double,
        notes: String,
        onSuccess: () -> Unit
    ) {
        val userName = _currentUser.value?.fullName ?: "Staff"
        viewModelScope.launch {
            val res = repository.createTransfer(srcWh, srcLoc, dstWh, dstLoc, productId, quantity, notes, userName)
            res.onSuccess {
                showToast("Transfer ${it.transferNumber} scheduled")
                onSuccess()
            }.onFailure {
                showToast(it.message ?: "Failed to schedule transfer")
            }
        }
    }

    fun validateTransfer(transferId: String) {
        val userName = _currentUser.value?.fullName ?: "Staff"
        viewModelScope.launch {
            val res = repository.validateTransfer(transferId, userName)
            res.onSuccess {
                showToast("Stock transferred successfully. Total inventory unchanged.")
            }.onFailure {
                showToast(it.message ?: "Failed to validate transfer")
            }
        }
    }

    fun createAdjustment(
        warehouseId: String,
        locationId: String,
        productId: String,
        physicalCount: Double,
        reason: String,
        onSuccess: () -> Unit
    ) {
        val userName = _currentUser.value?.fullName ?: "Staff"
        viewModelScope.launch {
            val res = repository.createAndValidateAdjustment(warehouseId, locationId, productId, physicalCount, reason, userName)
            res.onSuccess {
                showToast("Inventory adjustment recorded (${if (it.difference >= 0) "+${it.difference}" else "${it.difference}"})")
                onSuccess()
            }.onFailure {
                showToast(it.message ?: "Failed to record adjustment")
            }
        }
    }

    fun createWarehouse(name: String, code: String, address: String, onSuccess: () -> Unit) {
        if (_currentUser.value?.role != "INVENTORY_MANAGER") {
            showToast("You do not have permission to perform this action.")
            return
        }
        viewModelScope.launch {
            repository.insertWarehouse(name, code, address)
            showToast("Warehouse '$name' added")
            onSuccess()
        }
    }

    fun createLocation(warehouseId: String, name: String, code: String, type: String, onSuccess: () -> Unit) {
        if (_currentUser.value?.role != "INVENTORY_MANAGER") {
            showToast("You do not have permission to perform this action.")
            return
        }
        viewModelScope.launch {
            repository.insertLocation(warehouseId, name, code, type)
            showToast("Location '$name' added")
            onSuccess()
        }
    }

    fun createCategory(name: String, code: String, description: String, onSuccess: () -> Unit) {
        if (_currentUser.value?.role != "INVENTORY_MANAGER") {
            showToast("You do not have permission to perform this action.")
            return
        }
        viewModelScope.launch {
            repository.insertCategory(name, code, description)
            showToast("Category '$name' created")
            onSuccess()
        }
    }
}

private fun String.capitalizeWords(): String =
    split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
