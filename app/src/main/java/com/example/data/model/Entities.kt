package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

enum class UserRole(val displayName: String) {
    INVENTORY_MANAGER("Inventory Manager"),
    WAREHOUSE_STAFF("Warehouse Staff")
}

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val email: String,
    val passwordHash: String,
    val fullName: String,
    val role: String, // "INVENTORY_MANAGER" or "WAREHOUSE_STAFF"
    val avatarInitials: String = "US"
)

@Entity(
    tableName = "categories",
    indices = [Index(value = ["code"], unique = true)]
)
data class CategoryEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val code: String,
    val description: String = ""
)

@Entity(
    tableName = "warehouses",
    indices = [Index(value = ["code"], unique = true)]
)
data class WarehouseEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val code: String,
    val address: String = ""
)

@Entity(
    tableName = "locations",
    foreignKeys = [
        ForeignKey(
            entity = WarehouseEntity::class,
            parentColumns = ["id"],
            childColumns = ["warehouseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["warehouseId"])]
)
data class LocationEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val warehouseId: String,
    val name: String,
    val code: String,
    val type: String = "RACK" // RACK, FLOOR, DISPATCH, RECEIVING
)

@Entity(
    tableName = "products",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index(value = ["sku"], unique = true), Index(value = ["categoryId"])]
)
data class ProductEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val sku: String,
    val categoryId: String,
    val unitOfMeasure: String = "Units", // KG, Units, Box, Meters
    val reorderLevel: Double = 10.0,
    val reorderQuantity: Double = 50.0,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "stock",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = WarehouseEntity::class,
            parentColumns = ["id"],
            childColumns = ["warehouseId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = LocationEntity::class,
            parentColumns = ["id"],
            childColumns = ["locationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["productId", "warehouseId", "locationId"], unique = true),
        Index(value = ["productId"]),
        Index(value = ["warehouseId"]),
        Index(value = ["locationId"])
    ]
)
data class StockEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val productId: String,
    val warehouseId: String,
    val locationId: String,
    val quantity: Double = 0.0,
    val reservedQuantity: Double = 0.0,
    val updatedAt: Long = System.currentTimeMillis()
) {
    val availableQuantity: Double get() = (quantity - reservedQuantity).coerceAtLeast(0.0)
}

@Entity(
    tableName = "receipts",
    indices = [Index(value = ["receiptNumber"], unique = true)]
)
data class ReceiptEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val receiptNumber: String,
    val supplier: String,
    val warehouseId: String,
    val destinationLocationId: String,
    val status: String = "DRAFT", // DRAFT, READY, DONE
    val notes: String = "",
    val createdBy: String,
    val createdAt: Long = System.currentTimeMillis(),
    val validatedAt: Long? = null
)

@Entity(
    tableName = "receipt_items",
    foreignKeys = [
        ForeignKey(
            entity = ReceiptEntity::class,
            parentColumns = ["id"],
            childColumns = ["receiptId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index(value = ["receiptId"]), Index(value = ["productId"])]
)
data class ReceiptItemEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val receiptId: String,
    val productId: String,
    val quantity: Double,
    val unit: String = "Units"
)

@Entity(
    tableName = "deliveries",
    indices = [Index(value = ["deliveryNumber"], unique = true)]
)
data class DeliveryEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val deliveryNumber: String,
    val customer: String,
    val warehouseId: String,
    val sourceLocationId: String,
    val status: String = "DRAFT", // DRAFT, READY, PICKED, PACKED, DONE
    val notes: String = "",
    val createdBy: String,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)

@Entity(
    tableName = "delivery_items",
    foreignKeys = [
        ForeignKey(
            entity = DeliveryEntity::class,
            parentColumns = ["id"],
            childColumns = ["deliveryId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index(value = ["deliveryId"]), Index(value = ["productId"])]
)
data class DeliveryItemEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val deliveryId: String,
    val productId: String,
    val quantity: Double,
    val unit: String = "Units"
)

@Entity(
    tableName = "internal_transfers",
    indices = [Index(value = ["transferNumber"], unique = true)]
)
data class TransferEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val transferNumber: String,
    val sourceWarehouseId: String,
    val sourceLocationId: String,
    val destinationWarehouseId: String,
    val destinationLocationId: String,
    val status: String = "DRAFT", // DRAFT, READY, DONE
    val notes: String = "",
    val createdBy: String,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)

@Entity(
    tableName = "transfer_items",
    foreignKeys = [
        ForeignKey(
            entity = TransferEntity::class,
            parentColumns = ["id"],
            childColumns = ["transferId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index(value = ["transferId"]), Index(value = ["productId"])]
)
data class TransferItemEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val transferId: String,
    val productId: String,
    val quantity: Double
)

@Entity(
    tableName = "inventory_adjustments",
    indices = [Index(value = ["adjustmentNumber"], unique = true)]
)
data class AdjustmentEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val adjustmentNumber: String,
    val warehouseId: String,
    val locationId: String,
    val productId: String,
    val systemQuantity: Double,
    val physicalCount: Double,
    val difference: Double, // physicalCount - systemQuantity
    val reason: String = "",
    val status: String = "DRAFT", // DRAFT, DONE
    val createdBy: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "stock_ledger",
    indices = [
        Index(value = ["productId"]),
        Index(value = ["timestamp"]),
        Index(value = ["referenceNumber"])
    ]
)
data class StockLedgerEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val productId: String,
    val warehouseId: String,
    val locationId: String,
    val operationType: String, // RECEIPT, DELIVERY, TRANSFER_OUT, TRANSFER_IN, ADJUSTMENT, INITIAL_STOCK
    val referenceNumber: String,
    val quantityBefore: Double,
    val quantityChange: Double,
    val quantityAfter: Double,
    val sourceLocationName: String? = null,
    val destinationLocationName: String? = null,
    val createdBy: String,
    val timestamp: Long = System.currentTimeMillis()
)
