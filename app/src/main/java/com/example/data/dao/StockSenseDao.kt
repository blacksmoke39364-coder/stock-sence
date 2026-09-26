package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AdjustmentEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.DeliveryEntity
import com.example.data.model.DeliveryItemEntity
import com.example.data.model.LocationEntity
import com.example.data.model.ProductEntity
import com.example.data.model.ReceiptEntity
import com.example.data.model.ReceiptItemEntity
import com.example.data.model.StockEntity
import com.example.data.model.StockLedgerEntity
import com.example.data.model.TransferEntity
import com.example.data.model.TransferItemEntity
import com.example.data.model.UserEntity
import com.example.data.model.WarehouseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StockSenseDao {

    // --- Users ---
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: String): UserEntity?

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    // --- Categories ---
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE id = :id LIMIT 1")
    suspend fun getCategoryById(id: String): CategoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    // --- Warehouses ---
    @Query("SELECT * FROM warehouses ORDER BY name ASC")
    fun getAllWarehouses(): Flow<List<WarehouseEntity>>

    @Query("SELECT * FROM warehouses WHERE id = :id LIMIT 1")
    suspend fun getWarehouseById(id: String): WarehouseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWarehouse(warehouse: WarehouseEntity)

    // --- Locations ---
    @Query("SELECT * FROM locations ORDER BY name ASC")
    fun getAllLocations(): Flow<List<LocationEntity>>

    @Query("SELECT * FROM locations WHERE warehouseId = :warehouseId ORDER BY name ASC")
    fun getLocationsForWarehouse(warehouseId: String): Flow<List<LocationEntity>>

    @Query("SELECT * FROM locations WHERE id = :id LIMIT 1")
    suspend fun getLocationById(id: String): LocationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: LocationEntity)

    // --- Products ---
    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: String): ProductEntity?

    @Query("SELECT * FROM products WHERE sku = :sku LIMIT 1")
    suspend fun getProductBySku(sku: String): ProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    // --- Stock ---
    @Query("SELECT * FROM stock")
    fun getAllStock(): Flow<List<StockEntity>>

    @Query("SELECT * FROM stock WHERE productId = :productId")
    fun getStockForProduct(productId: String): Flow<List<StockEntity>>

    @Query("SELECT * FROM stock WHERE productId = :productId AND warehouseId = :warehouseId AND locationId = :locationId LIMIT 1")
    suspend fun getStockByLocation(productId: String, warehouseId: String, locationId: String): StockEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStock(stock: StockEntity)

    @Update
    suspend fun updateStock(stock: StockEntity)

    // --- Receipts ---
    @Query("SELECT * FROM receipts ORDER BY createdAt DESC")
    fun getAllReceipts(): Flow<List<ReceiptEntity>>

    @Query("SELECT * FROM receipts WHERE id = :id LIMIT 1")
    suspend fun getReceiptById(id: String): ReceiptEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceipt(receipt: ReceiptEntity)

    @Update
    suspend fun updateReceipt(receipt: ReceiptEntity)

    @Query("SELECT * FROM receipt_items WHERE receiptId = :receiptId")
    suspend fun getReceiptItems(receiptId: String): List<ReceiptItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceiptItems(items: List<ReceiptItemEntity>)

    // --- Deliveries ---
    @Query("SELECT * FROM deliveries ORDER BY createdAt DESC")
    fun getAllDeliveries(): Flow<List<DeliveryEntity>>

    @Query("SELECT * FROM deliveries WHERE id = :id LIMIT 1")
    suspend fun getDeliveryById(id: String): DeliveryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDelivery(delivery: DeliveryEntity)

    @Update
    suspend fun updateDelivery(delivery: DeliveryEntity)

    @Query("SELECT * FROM delivery_items WHERE deliveryId = :deliveryId")
    suspend fun getDeliveryItems(deliveryId: String): List<DeliveryItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeliveryItems(items: List<DeliveryItemEntity>)

    // --- Internal Transfers ---
    @Query("SELECT * FROM internal_transfers ORDER BY createdAt DESC")
    fun getAllTransfers(): Flow<List<TransferEntity>>

    @Query("SELECT * FROM internal_transfers WHERE id = :id LIMIT 1")
    suspend fun getTransferById(id: String): TransferEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransfer(transfer: TransferEntity)

    @Update
    suspend fun updateTransfer(transfer: TransferEntity)

    @Query("SELECT * FROM transfer_items WHERE transferId = :transferId")
    suspend fun getTransferItems(transferId: String): List<TransferItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransferItems(items: List<TransferItemEntity>)

    // --- Inventory Adjustments ---
    @Query("SELECT * FROM inventory_adjustments ORDER BY createdAt DESC")
    fun getAllAdjustments(): Flow<List<AdjustmentEntity>>

    @Query("SELECT * FROM inventory_adjustments WHERE id = :id LIMIT 1")
    suspend fun getAdjustmentById(id: String): AdjustmentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdjustment(adjustment: AdjustmentEntity)

    @Update
    suspend fun updateAdjustment(adjustment: AdjustmentEntity)

    // --- Stock Ledger (Append-Only) ---
    @Query("SELECT * FROM stock_ledger ORDER BY timestamp DESC")
    fun getAllLedgerEntries(): Flow<List<StockLedgerEntity>>

    @Query("SELECT * FROM stock_ledger WHERE productId = :productId ORDER BY timestamp DESC")
    fun getLedgerForProduct(productId: String): Flow<List<StockLedgerEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertLedgerEntry(entry: StockLedgerEntity)
}
