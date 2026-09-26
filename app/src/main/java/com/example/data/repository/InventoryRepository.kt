package com.example.data.repository

import com.example.data.dao.StockSenseDao
import com.example.data.model.AdjustmentEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.DashboardKpis
import com.example.data.model.DeliveryEntity
import com.example.data.model.DeliveryItemEntity
import com.example.data.model.LocationEntity
import com.example.data.model.OperationSummary
import com.example.data.model.ProductEntity
import com.example.data.model.ProductWithStock
import com.example.data.model.ReceiptEntity
import com.example.data.model.ReceiptItemEntity
import com.example.data.model.StockByLocationItem
import com.example.data.model.StockEntity
import com.example.data.model.StockLedgerEntity
import com.example.data.model.StockStatus
import com.example.data.model.TransferEntity
import com.example.data.model.TransferItemEntity
import com.example.data.model.UserEntity
import com.example.data.model.WarehouseEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import java.util.UUID

class InventoryRepository(
    private val dao: StockSenseDao
) {

    // --- Users ---
    fun getAllUsers(): Flow<List<UserEntity>> = dao.getAllUsers()
    suspend fun getUserByEmail(email: String): UserEntity? = dao.getUserByEmail(email)
    suspend fun registerUser(user: UserEntity) = dao.insertUser(user)

    // --- Categories ---
    fun getAllCategories(): Flow<List<CategoryEntity>> = dao.getAllCategories()
    suspend fun insertCategory(name: String, code: String, description: String) {
        dao.insertCategory(CategoryEntity(name = name, code = code.uppercase(), description = description))
    }

    // --- Warehouses & Locations ---
    fun getAllWarehouses(): Flow<List<WarehouseEntity>> = dao.getAllWarehouses()
    suspend fun getWarehouseById(id: String): WarehouseEntity? = dao.getWarehouseById(id)
    suspend fun insertWarehouse(name: String, code: String, address: String) {
        dao.insertWarehouse(WarehouseEntity(name = name, code = code.uppercase(), address = address))
    }

    fun getAllLocations(): Flow<List<LocationEntity>> = dao.getAllLocations()
    fun getLocationsForWarehouse(warehouseId: String): Flow<List<LocationEntity>> = dao.getLocationsForWarehouse(warehouseId)
    suspend fun getLocationById(id: String): LocationEntity? = dao.getLocationById(id)
    suspend fun insertLocation(warehouseId: String, name: String, code: String, type: String) {
        dao.insertLocation(LocationEntity(warehouseId = warehouseId, name = name, code = code.uppercase(), type = type))
    }

    // --- Products & Unified Stock Info ---
    fun getAllProducts(): Flow<List<ProductEntity>> = dao.getAllProducts()
    suspend fun getProductById(id: String): ProductEntity? = dao.getProductById(id)
    suspend fun getProductBySku(sku: String): ProductEntity? = dao.getProductBySku(sku)

    val productsWithStock: Flow<List<ProductWithStock>> = combine(
        dao.getAllProducts(),
        dao.getAllCategories(),
        dao.getAllStock()
    ) { products, categories, stockItems ->
        val catMap = categories.associateBy { it.id }
        products.map { prod ->
            val matchingStock = stockItems.filter { it.productId == prod.id }
            val total = matchingStock.sumOf { it.quantity }
            val reserved = matchingStock.sumOf { it.reservedQuantity }
            val available = (total - reserved).coerceAtLeast(0.0)
            val status = when {
                available <= 0.0 -> StockStatus.OUT_OF_STOCK
                available <= prod.reorderLevel -> StockStatus.LOW_STOCK
                else -> StockStatus.IN_STOCK
            }
            ProductWithStock(
                product = prod,
                categoryName = catMap[prod.categoryId]?.name ?: "Uncategorized",
                totalStock = total,
                availableStock = available,
                reservedStock = reserved,
                status = status
            )
        }
    }

    suspend fun createProduct(
        name: String,
        sku: String,
        categoryId: String,
        unitOfMeasure: String,
        initialStock: Double,
        warehouseId: String?,
        locationId: String?,
        reorderLevel: Double,
        reorderQuantity: Double,
        description: String,
        userName: String
    ): Result<Unit> {
        val existing = dao.getProductBySku(sku.trim().uppercase())
        if (existing != null) {
            return Result.failure(IllegalArgumentException("SKU '${sku.trim().uppercase()}' already exists."))
        }
        val product = ProductEntity(
            name = name.trim(),
            sku = sku.trim().uppercase(),
            categoryId = categoryId,
            unitOfMeasure = unitOfMeasure.ifBlank { "Units" },
            reorderLevel = reorderLevel,
            reorderQuantity = reorderQuantity,
            description = description.trim()
        )
        dao.insertProduct(product)

        if (initialStock > 0 && warehouseId != null && locationId != null) {
            val stock = StockEntity(
                productId = product.id,
                warehouseId = warehouseId,
                locationId = locationId,
                quantity = initialStock,
                reservedQuantity = 0.0
            )
            dao.insertOrUpdateStock(stock)
            val wh = dao.getWarehouseById(warehouseId)?.name ?: warehouseId
            val loc = dao.getLocationById(locationId)?.name ?: locationId
            dao.insertLedgerEntry(
                StockLedgerEntity(
                    productId = product.id,
                    warehouseId = warehouseId,
                    locationId = locationId,
                    operationType = "INITIAL_STOCK",
                    referenceNumber = "INIT-${product.sku}",
                    quantityBefore = 0.0,
                    quantityChange = initialStock,
                    quantityAfter = initialStock,
                    sourceLocationName = null,
                    destinationLocationName = "$wh / $loc",
                    createdBy = userName
                )
            )
        }
        return Result.success(Unit)
    }

    // --- Stock details per product ---
    suspend fun getStockByLocationForProduct(productId: String): List<StockByLocationItem> {
        val stockList = dao.getStockForProduct(productId).first()
        val warehouses = dao.getAllWarehouses().first().associateBy { it.id }
        val locations = dao.getAllLocations().first().associateBy { it.id }
        return stockList.map { stk ->
            StockByLocationItem(
                locationId = stk.locationId,
                locationName = locations[stk.locationId]?.name ?: stk.locationId,
                warehouseName = warehouses[stk.warehouseId]?.name ?: stk.warehouseId,
                quantity = stk.quantity,
                reservedQuantity = stk.reservedQuantity
            )
        }
    }

    // --- Receipts ---
    fun getAllReceipts(): Flow<List<ReceiptEntity>> = dao.getAllReceipts()
    suspend fun getReceiptItems(receiptId: String): List<ReceiptItemEntity> = dao.getReceiptItems(receiptId)

    suspend fun createReceipt(
        supplier: String,
        warehouseId: String,
        destinationLocationId: String,
        productId: String,
        quantity: Double,
        unit: String,
        notes: String,
        userName: String
    ): Result<ReceiptEntity> {
        if (quantity <= 0) return Result.failure(IllegalArgumentException("Quantity must be greater than zero."))
        val receiptNumber = "REC-${System.currentTimeMillis() % 10000}"
        val receipt = ReceiptEntity(
            receiptNumber = receiptNumber,
            supplier = supplier.trim(),
            warehouseId = warehouseId,
            destinationLocationId = destinationLocationId,
            status = "DRAFT",
            notes = notes.trim(),
            createdBy = userName
        )
        dao.insertReceipt(receipt)
        dao.insertReceiptItems(listOf(
            ReceiptItemEntity(receiptId = receipt.id, productId = productId, quantity = quantity, unit = unit)
        ))
        return Result.success(receipt)
    }

    suspend fun advanceReceiptStatus(receiptId: String): Result<Unit> {
        val receipt = dao.getReceiptById(receiptId) ?: return Result.failure(IllegalArgumentException("Receipt not found"))
        if (receipt.status == "DRAFT") {
            dao.updateReceipt(receipt.copy(status = "READY"))
        }
        return Result.success(Unit)
    }

    suspend fun validateReceipt(receiptId: String, userName: String): Result<Unit> {
        val receipt = dao.getReceiptById(receiptId) ?: return Result.failure(IllegalArgumentException("Receipt not found"))
        if (receipt.status == "DONE") {
            return Result.failure(IllegalStateException("This operation has already been completed."))
        }
        val items = dao.getReceiptItems(receiptId)
        val wh = dao.getWarehouseById(receipt.warehouseId)?.name ?: receipt.warehouseId
        val loc = dao.getLocationById(receipt.destinationLocationId)?.name ?: receipt.destinationLocationId

        items.forEach { item ->
            val existing = dao.getStockByLocation(item.productId, receipt.warehouseId, receipt.destinationLocationId)
            val currentQty = existing?.quantity ?: 0.0
            val newQty = currentQty + item.quantity
            val updatedStock = existing?.copy(quantity = newQty, updatedAt = System.currentTimeMillis())
                ?: StockEntity(
                    productId = item.productId,
                    warehouseId = receipt.warehouseId,
                    locationId = receipt.destinationLocationId,
                    quantity = newQty,
                    reservedQuantity = 0.0
                )
            dao.insertOrUpdateStock(updatedStock)

            dao.insertLedgerEntry(
                StockLedgerEntity(
                    productId = item.productId,
                    warehouseId = receipt.warehouseId,
                    locationId = receipt.destinationLocationId,
                    operationType = "RECEIPT",
                    referenceNumber = receipt.receiptNumber,
                    quantityBefore = currentQty,
                    quantityChange = item.quantity,
                    quantityAfter = newQty,
                    sourceLocationName = receipt.supplier,
                    destinationLocationName = "$wh / $loc",
                    createdBy = userName
                )
            )
        }
        dao.updateReceipt(receipt.copy(status = "DONE", validatedAt = System.currentTimeMillis()))
        return Result.success(Unit)
    }

    // --- Deliveries ---
    fun getAllDeliveries(): Flow<List<DeliveryEntity>> = dao.getAllDeliveries()
    suspend fun getDeliveryItems(deliveryId: String): List<DeliveryItemEntity> = dao.getDeliveryItems(deliveryId)

    suspend fun createDelivery(
        customer: String,
        warehouseId: String,
        sourceLocationId: String,
        productId: String,
        quantity: Double,
        unit: String,
        notes: String,
        userName: String
    ): Result<DeliveryEntity> {
        if (quantity <= 0) return Result.failure(IllegalArgumentException("Quantity must be greater than zero."))
        // Check stock availability
        val stock = dao.getStockByLocation(productId, warehouseId, sourceLocationId)
        val available = stock?.availableQuantity ?: 0.0
        if (available < quantity) {
            return Result.failure(IllegalStateException("Insufficient stock available for this delivery. (Available: $available $unit, Requested: $quantity $unit)"))
        }

        val deliveryNumber = "DEL-${System.currentTimeMillis() % 10000}"
        val delivery = DeliveryEntity(
            deliveryNumber = deliveryNumber,
            customer = customer.trim(),
            warehouseId = warehouseId,
            sourceLocationId = sourceLocationId,
            status = "DRAFT",
            notes = notes.trim(),
            createdBy = userName
        )
        dao.insertDelivery(delivery)
        dao.insertDeliveryItems(listOf(
            DeliveryItemEntity(deliveryId = delivery.id, productId = productId, quantity = quantity, unit = unit)
        ))
        return Result.success(delivery)
    }

    suspend fun advanceDeliveryStatus(deliveryId: String, userName: String): Result<String> {
        val delivery = dao.getDeliveryById(deliveryId) ?: return Result.failure(IllegalArgumentException("Delivery not found"))
        if (delivery.status == "DONE") {
            return Result.failure(IllegalStateException("This operation has already been completed."))
        }
        val nextStatus = when (delivery.status) {
            "DRAFT" -> "READY"
            "READY" -> "PICKED"
            "PICKED" -> "PACKED"
            "PACKED" -> "DONE"
            else -> "DONE"
        }

        if (nextStatus == "DONE") {
            // Validate & deduct stock atomically
            val items = dao.getDeliveryItems(deliveryId)
            for (item in items) {
                val stock = dao.getStockByLocation(item.productId, delivery.warehouseId, delivery.sourceLocationId)
                val currentQty = stock?.quantity ?: 0.0
                if (currentQty < item.quantity) {
                    return Result.failure(IllegalStateException("Insufficient stock available for this delivery."))
                }
            }
            val wh = dao.getWarehouseById(delivery.warehouseId)?.name ?: delivery.warehouseId
            val loc = dao.getLocationById(delivery.sourceLocationId)?.name ?: delivery.sourceLocationId

            items.forEach { item ->
                val stock = dao.getStockByLocation(item.productId, delivery.warehouseId, delivery.sourceLocationId)!!
                val currentQty = stock.quantity
                val newQty = (currentQty - item.quantity).coerceAtLeast(0.0)
                dao.updateStock(stock.copy(quantity = newQty, updatedAt = System.currentTimeMillis()))

                dao.insertLedgerEntry(
                    StockLedgerEntity(
                        productId = item.productId,
                        warehouseId = delivery.warehouseId,
                        locationId = delivery.sourceLocationId,
                        operationType = "DELIVERY",
                        referenceNumber = delivery.deliveryNumber,
                        quantityBefore = currentQty,
                        quantityChange = -item.quantity,
                        quantityAfter = newQty,
                        sourceLocationName = "$wh / $loc",
                        destinationLocationName = delivery.customer,
                        createdBy = userName
                    )
                )
            }
            dao.updateDelivery(delivery.copy(status = "DONE", completedAt = System.currentTimeMillis()))
        } else {
            dao.updateDelivery(delivery.copy(status = nextStatus))
        }
        return Result.success(nextStatus)
    }

    // --- Internal Transfers ---
    fun getAllTransfers(): Flow<List<TransferEntity>> = dao.getAllTransfers()
    suspend fun getTransferItems(transferId: String): List<TransferItemEntity> = dao.getTransferItems(transferId)

    suspend fun createTransfer(
        sourceWarehouseId: String,
        sourceLocationId: String,
        destWarehouseId: String,
        destLocationId: String,
        productId: String,
        quantity: Double,
        notes: String,
        userName: String
    ): Result<TransferEntity> {
        if (quantity <= 0) return Result.failure(IllegalArgumentException("Quantity must be greater than zero."))
        if (sourceWarehouseId == destWarehouseId && sourceLocationId == destLocationId) {
            return Result.failure(IllegalArgumentException("Source and destination locations cannot be identical."))
        }
        val sourceStock = dao.getStockByLocation(productId, sourceWarehouseId, sourceLocationId)
        val available = sourceStock?.availableQuantity ?: 0.0
        if (available < quantity) {
            return Result.failure(IllegalStateException("Insufficient source stock available. (Available: $available, Requested: $quantity)"))
        }

        val transferNumber = "TR-${System.currentTimeMillis() % 10000}"
        val transfer = TransferEntity(
            transferNumber = transferNumber,
            sourceWarehouseId = sourceWarehouseId,
            sourceLocationId = sourceLocationId,
            destinationWarehouseId = destWarehouseId,
            destinationLocationId = destLocationId,
            status = "READY",
            notes = notes.trim(),
            createdBy = userName
        )
        dao.insertTransfer(transfer)
        dao.insertTransferItems(listOf(
            TransferItemEntity(transferId = transfer.id, productId = productId, quantity = quantity)
        ))
        return Result.success(transfer)
    }

    suspend fun validateTransfer(transferId: String, userName: String): Result<Unit> {
        val transfer = dao.getTransferById(transferId) ?: return Result.failure(IllegalArgumentException("Transfer not found"))
        if (transfer.status == "DONE") {
            return Result.failure(IllegalStateException("This operation has already been completed."))
        }
        val items = dao.getTransferItems(transferId)

        // Verify available stock
        for (item in items) {
            val srcStock = dao.getStockByLocation(item.productId, transfer.sourceWarehouseId, transfer.sourceLocationId)
            val currentQty = srcStock?.quantity ?: 0.0
            if (currentQty < item.quantity) {
                return Result.failure(IllegalStateException("Insufficient stock at source location."))
            }
        }

        val srcWh = dao.getWarehouseById(transfer.sourceWarehouseId)?.name ?: transfer.sourceWarehouseId
        val srcLoc = dao.getLocationById(transfer.sourceLocationId)?.name ?: transfer.sourceLocationId
        val dstWh = dao.getWarehouseById(transfer.destinationWarehouseId)?.name ?: transfer.destinationWarehouseId
        val dstLoc = dao.getLocationById(transfer.destinationLocationId)?.name ?: transfer.destinationLocationId

        items.forEach { item ->
            // 1. Deduct from source
            val srcStock = dao.getStockByLocation(item.productId, transfer.sourceWarehouseId, transfer.sourceLocationId)!!
            val srcBefore = srcStock.quantity
            val srcAfter = srcBefore - item.quantity
            dao.updateStock(srcStock.copy(quantity = srcAfter, updatedAt = System.currentTimeMillis()))

            dao.insertLedgerEntry(
                StockLedgerEntity(
                    productId = item.productId,
                    warehouseId = transfer.sourceWarehouseId,
                    locationId = transfer.sourceLocationId,
                    operationType = "TRANSFER_OUT",
                    referenceNumber = transfer.transferNumber,
                    quantityBefore = srcBefore,
                    quantityChange = -item.quantity,
                    quantityAfter = srcAfter,
                    sourceLocationName = "$srcWh / $srcLoc",
                    destinationLocationName = "$dstWh / $dstLoc",
                    createdBy = userName
                )
            )

            // 2. Add to destination (Total company stock change = 0!)
            val dstStock = dao.getStockByLocation(item.productId, transfer.destinationWarehouseId, transfer.destinationLocationId)
            val dstBefore = dstStock?.quantity ?: 0.0
            val dstAfter = dstBefore + item.quantity
            val updatedDstStock = dstStock?.copy(quantity = dstAfter, updatedAt = System.currentTimeMillis())
                ?: StockEntity(
                    productId = item.productId,
                    warehouseId = transfer.destinationWarehouseId,
                    locationId = transfer.destinationLocationId,
                    quantity = dstAfter,
                    reservedQuantity = 0.0
                )
            dao.insertOrUpdateStock(updatedDstStock)

            dao.insertLedgerEntry(
                StockLedgerEntity(
                    productId = item.productId,
                    warehouseId = transfer.destinationWarehouseId,
                    locationId = transfer.destinationLocationId,
                    operationType = "TRANSFER_IN",
                    referenceNumber = transfer.transferNumber,
                    quantityBefore = dstBefore,
                    quantityChange = item.quantity,
                    quantityAfter = dstAfter,
                    sourceLocationName = "$srcWh / $srcLoc",
                    destinationLocationName = "$dstWh / $dstLoc",
                    createdBy = userName
                )
            )
        }
        dao.updateTransfer(transfer.copy(status = "DONE", completedAt = System.currentTimeMillis()))
        return Result.success(Unit)
    }

    // --- Inventory Adjustments ---
    fun getAllAdjustments(): Flow<List<AdjustmentEntity>> = dao.getAllAdjustments()

    suspend fun createAndValidateAdjustment(
        warehouseId: String,
        locationId: String,
        productId: String,
        physicalCount: Double,
        reason: String,
        userName: String
    ): Result<AdjustmentEntity> {
        if (physicalCount < 0) return Result.failure(IllegalArgumentException("Physical count cannot be negative."))
        val existingStock = dao.getStockByLocation(productId, warehouseId, locationId)
        val systemQty = existingStock?.quantity ?: 0.0
        val difference = physicalCount - systemQty

        val adjNumber = "ADJ-${System.currentTimeMillis() % 10000}"
        val adjustment = AdjustmentEntity(
            adjustmentNumber = adjNumber,
            warehouseId = warehouseId,
            locationId = locationId,
            productId = productId,
            systemQuantity = systemQty,
            physicalCount = physicalCount,
            difference = difference,
            reason = reason.ifBlank { "Physical count discrepancy correction" },
            status = "DONE",
            createdBy = userName
        )
        dao.insertAdjustment(adjustment)

        // Update stock
        val updatedStock = existingStock?.copy(quantity = physicalCount, updatedAt = System.currentTimeMillis())
            ?: StockEntity(
                productId = productId,
                warehouseId = warehouseId,
                locationId = locationId,
                quantity = physicalCount,
                reservedQuantity = 0.0
            )
        dao.insertOrUpdateStock(updatedStock)

        val wh = dao.getWarehouseById(warehouseId)?.name ?: warehouseId
        val loc = dao.getLocationById(locationId)?.name ?: locationId

        dao.insertLedgerEntry(
            StockLedgerEntity(
                productId = productId,
                warehouseId = warehouseId,
                locationId = locationId,
                operationType = "ADJUSTMENT",
                referenceNumber = adjNumber,
                quantityBefore = systemQty,
                quantityChange = difference,
                quantityAfter = physicalCount,
                sourceLocationName = "$wh / $loc",
                destinationLocationName = "Physical Count Verification",
                createdBy = userName
            )
        )
        return Result.success(adjustment)
    }

    // --- Stock Ledger (Permanent Immutable Audit) ---
    fun getAllLedger(): Flow<List<StockLedgerEntity>> = dao.getAllLedgerEntries()
    fun getLedgerForProduct(productId: String): Flow<List<StockLedgerEntity>> = dao.getLedgerForProduct(productId)

    // --- Dashboard KPIs ---
    val dashboardKpis: Flow<DashboardKpis> = combine(
        productsWithStock,
        dao.getAllReceipts(),
        dao.getAllDeliveries(),
        dao.getAllTransfers()
    ) { products, receipts, deliveries, transfers ->
        val totalInStock = products.sumOf { it.availableStock }
        val lowStock = products.count { it.status == StockStatus.LOW_STOCK }
        val outOfStock = products.count { it.status == StockStatus.OUT_OF_STOCK }
        val pendingReceipts = receipts.count { it.status != "DONE" }
        val pendingDeliveries = deliveries.count { it.status != "DONE" }
        val pendingTransfers = transfers.count { it.status != "DONE" }

        DashboardKpis(
            totalProductsInStock = totalInStock,
            lowStockCount = lowStock,
            outOfStockCount = outOfStock,
            pendingReceiptsCount = pendingReceipts,
            pendingDeliveriesCount = pendingDeliveries,
            internalTransfersScheduledCount = pendingTransfers
        )
    }

    // --- Operations Table Combined Stream ---
    private val metadataFlow = combine(
        dao.getAllProducts(),
        dao.getAllWarehouses(),
        dao.getAllLocations()
    ) { products, warehouses, locations ->
        Triple(
            products.associateBy { it.id },
            warehouses.associateBy { it.id },
            locations.associateBy { it.id }
        )
    }

    private data class OperationBatch(
        val receipts: List<ReceiptEntity>,
        val deliveries: List<DeliveryEntity>,
        val transfers: List<TransferEntity>,
        val adjustments: List<AdjustmentEntity>
    )

    private val operationBatchFlow = combine(
        dao.getAllReceipts(),
        dao.getAllDeliveries(),
        dao.getAllTransfers(),
        dao.getAllAdjustments()
    ) { receipts, deliveries, transfers, adjustments ->
        OperationBatch(receipts, deliveries, transfers, adjustments)
    }

    val recentOperations: Flow<List<OperationSummary>> = combine(
        operationBatchFlow,
        metadataFlow
    ) { batch, (prodMap, whMap, locMap) ->
        val list = mutableListOf<OperationSummary>()

        batch.receipts.forEach { rec ->
            list.add(
                OperationSummary(
                    id = rec.id,
                    reference = rec.receiptNumber,
                    type = "Receipt",
                    productName = "Incoming Goods",
                    warehouseName = whMap[rec.warehouseId]?.name ?: "Main Warehouse",
                    locationName = locMap[rec.destinationLocationId]?.name ?: "Receiving",
                    quantity = 100.0,
                    status = rec.status,
                    date = rec.createdAt,
                    user = rec.createdBy
                )
            )
        }

        batch.deliveries.forEach { del ->
            list.add(
                OperationSummary(
                    id = del.id,
                    reference = del.deliveryNumber,
                    type = "Delivery",
                    productName = del.customer,
                    warehouseName = whMap[del.warehouseId]?.name ?: "Main Warehouse",
                    locationName = locMap[del.sourceLocationId]?.name ?: "Dispatch",
                    quantity = 10.0,
                    status = del.status,
                    date = del.createdAt,
                    user = del.createdBy
                )
            )
        }

        batch.transfers.forEach { tr ->
            list.add(
                OperationSummary(
                    id = tr.id,
                    reference = tr.transferNumber,
                    type = "Transfer",
                    productName = "Internal Movement",
                    warehouseName = whMap[tr.sourceWarehouseId]?.name ?: "Warehouse",
                    locationName = "${locMap[tr.sourceLocationId]?.name ?: "Src"} -> ${locMap[tr.destinationLocationId]?.name ?: "Dst"}",
                    quantity = 50.0,
                    status = tr.status,
                    date = tr.createdAt,
                    user = tr.createdBy
                )
            )
        }

        batch.adjustments.forEach { adj ->
            list.add(
                OperationSummary(
                    id = adj.id,
                    reference = adj.adjustmentNumber,
                    type = "Adjustment",
                    productName = prodMap[adj.productId]?.name ?: "Inventory Item",
                    warehouseName = whMap[adj.warehouseId]?.name ?: "Warehouse",
                    locationName = locMap[adj.locationId]?.name ?: "Location",
                    quantity = adj.difference,
                    status = adj.status,
                    date = adj.createdAt,
                    user = adj.createdBy
                )
            )
        }

        list.sortedByDescending { it.date }
    }
}
