package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.JewelBrass
import com.example.ui.theme.JewelNavy
import com.example.ui.theme.JewelRust
import com.example.ui.theme.JewelTeal

enum class StockStatus(val label: String) {
    IN_STOCK("In Stock"),
    LOW_STOCK("Low Stock"),
    OUT_OF_STOCK("Out of Stock")
}

data class ProductWithStock(
    val product: ProductEntity,
    val categoryName: String,
    val totalStock: Double,
    val availableStock: Double,
    val reservedStock: Double,
    val status: StockStatus
)

data class OperationSummary(
    val id: String,
    val reference: String,
    val type: String, // "Receipt", "Delivery", "Transfer", "Adjustment"
    val productName: String,
    val warehouseName: String,
    val locationName: String,
    val quantity: Double,
    val status: String,
    val date: Long,
    val user: String
)

data class DashboardKpis(
    val totalProductsInStock: Double = 0.0,
    val lowStockCount: Int = 0,
    val outOfStockCount: Int = 0,
    val pendingReceiptsCount: Int = 0,
    val pendingDeliveriesCount: Int = 0,
    val internalTransfersScheduledCount: Int = 0
)

data class StockByLocationItem(
    val locationId: String,
    val locationName: String,
    val warehouseName: String,
    val quantity: Double,
    val reservedQuantity: Double
)

data class MovementPoint(
    val label: String,
    val value: Float,
    val displayValue: String
)

data class DonutSegment(
    val name: String,
    val percentage: Int,
    val color: Color,
    val detail: String
)
