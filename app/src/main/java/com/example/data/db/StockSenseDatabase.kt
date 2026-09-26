package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.StockSenseDao
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

@Database(
    entities = [
        UserEntity::class,
        CategoryEntity::class,
        WarehouseEntity::class,
        LocationEntity::class,
        ProductEntity::class,
        StockEntity::class,
        ReceiptEntity::class,
        ReceiptItemEntity::class,
        DeliveryEntity::class,
        DeliveryItemEntity::class,
        TransferEntity::class,
        TransferItemEntity::class,
        AdjustmentEntity::class,
        StockLedgerEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class StockSenseDatabase : RoomDatabase() {

    abstract fun stockSenseDao(): StockSenseDao

    companion object {
        @Volatile
        private var INSTANCE: StockSenseDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): StockSenseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StockSenseDatabase::class.java,
                    "stocksense_database"
                )
                .addCallback(StockSenseDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class StockSenseDatabaseCallback(
        private val scope: CoroutineScope
    ) : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database.stockSenseDao())
                }
            }
        }
    }
}

suspend fun populateInitialData(dao: StockSenseDao) {
    // 1. Users
    val managerId = "user-alex"
    val staffId = "user-sam"
    dao.insertUser(
        UserEntity(
            id = managerId,
            email = "alex@stocksense.io",
            passwordHash = "manager123",
            fullName = "Alex Vance",
            role = "INVENTORY_MANAGER",
            avatarInitials = "AV"
        )
    )
    dao.insertUser(
        UserEntity(
            id = staffId,
            email = "sam@stocksense.io",
            passwordHash = "staff123",
            fullName = "Sam Rivera",
            role = "WAREHOUSE_STAFF",
            avatarInitials = "SR"
        )
    )

    // 2. Categories
    val catRaw = CategoryEntity("cat-1", "Raw Materials", "RAW", "Metals, alloys, and raw components")
    val catFinished = CategoryEntity("cat-2", "Finished Goods", "FIN", "Fully assembled commercial products")
    val catElectrical = CategoryEntity("cat-3", "Electrical", "ELE", "Wiring, harnesses, and electrical supplies")
    val catHardware = CategoryEntity("cat-4", "Hardware", "HDW", "Fasteners, fittings, and pipes")
    val catOffice = CategoryEntity("cat-5", "Office Equipment", "OFC", "Desks, seating, and facility gear")
    listOf(catRaw, catFinished, catElectrical, catHardware, catOffice).forEach { dao.insertCategory(it) }

    // 3. Warehouses
    val whMain = WarehouseEntity("wh-1", "Main Warehouse", "WH-MAIN", "Central Logistics Park, Sector 4")
    val whProd = WarehouseEntity("wh-2", "Production Warehouse", "WH-PROD", "Factory Unit 2, Industrial Blvd")
    dao.insertWarehouse(whMain)
    dao.insertWarehouse(whProd)

    // 4. Locations
    val locMainRackA = LocationEntity("loc-1", whMain.id, "Rack A", "RACK-A", "RACK")
    val locMainRackB = LocationEntity("loc-2", whMain.id, "Rack B", "RACK-B", "RACK")
    val locMainFloor = LocationEntity("loc-3", whMain.id, "Production Floor", "PROD-FLR", "FLOOR")
    val locMainDispatch = LocationEntity("loc-4", whMain.id, "Dispatch Area", "DISP-BAY", "DISPATCH")

    val locProdRackA = LocationEntity("loc-5", whProd.id, "Rack A", "RACK-A", "RACK")
    val locProdRackB = LocationEntity("loc-6", whProd.id, "Rack B", "RACK-B", "RACK")
    listOf(locMainRackA, locMainRackB, locMainFloor, locMainDispatch, locProdRackA, locProdRackB).forEach { dao.insertLocation(it) }

    // 5. Products
    val prodSteelRods = ProductEntity("prod-1", "Steel Rods", "SKU-STL-001", catRaw.id, "KG", 100.0, 250.0, "High tensile carbon steel reinforcing bars")
    val prodSteelSheets = ProductEntity("prod-2", "Steel Sheets", "SKU-STL-002", catRaw.id, "Units", 40.0, 100.0, "Cold rolled 2mm galvanized steel sheets")
    val prodOfficeChairs = ProductEntity("prod-3", "Office Chairs", "SKU-CHR-101", catOffice.id, "Units", 25.0, 50.0, "Ergonomic mesh executive swivel chairs")
    val prodWoodenTables = ProductEntity("prod-4", "Wooden Tables", "SKU-TBL-202", catFinished.id, "Units", 15.0, 30.0, "Solid treated oak workspace conference desks")
    val prodCopperWire = ProductEntity("prod-5", "Copper Wire", "SKU-ELE-303", catElectrical.id, "Meters", 200.0, 500.0, "12 AWG insulated high-conductivity copper cable")
    val prodPvcPipes = ProductEntity("prod-6", "PVC Pipes", "SKU-HDW-404", catHardware.id, "Units", 30.0, 80.0, "Heavy duty 4-inch drainage conduits")
    listOf(prodSteelRods, prodSteelSheets, prodOfficeChairs, prodWoodenTables, prodCopperWire, prodPvcPipes).forEach { dao.insertProduct(it) }

    // 6. Stock allocation across Product + Location
    val now = System.currentTimeMillis()
    val day = 86_400_000L

    val stockList = listOf(
        StockEntity("stk-1", prodSteelRods.id, whMain.id, locMainRackA.id, 300.0, 0.0, now - 4 * day),
        StockEntity("stk-2", prodSteelRods.id, whProd.id, locProdRackA.id, 150.0, 0.0, now - 3 * day),
        StockEntity("stk-3", prodSteelSheets.id, whMain.id, locMainRackB.id, 85.0, 15.0, now - 5 * day),
        StockEntity("stk-4", prodOfficeChairs.id, whMain.id, locMainDispatch.id, 90.0, 10.0, now - 2 * day),
        StockEntity("stk-5", prodWoodenTables.id, whMain.id, locMainFloor.id, 12.0, 0.0, now - 1 * day), // Low stock (<= 15)
        StockEntity("stk-6", prodCopperWire.id, whProd.id, locProdRackB.id, 650.0, 0.0, now - 6 * day),
        StockEntity("stk-7", prodPvcPipes.id, whMain.id, locMainRackA.id, 0.0, 0.0, now - 1 * day) // Out of stock
    )
    stockList.forEach { dao.insertOrUpdateStock(it) }

    // 7. Initial Stock Ledger Entries
    val seedLedger = listOf(
        StockLedgerEntity("led-1", prodSteelRods.id, whMain.id, locMainRackA.id, "INITIAL_STOCK", "INIT-001", 0.0, 300.0, 300.0, null, "Main Warehouse / Rack A", "Alex Vance", now - 5 * day),
        StockLedgerEntity("led-2", prodSteelRods.id, whProd.id, locProdRackA.id, "INITIAL_STOCK", "INIT-002", 0.0, 150.0, 150.0, null, "Production Warehouse / Rack A", "Alex Vance", now - 5 * day),
        StockLedgerEntity("led-3", prodSteelSheets.id, whMain.id, locMainRackB.id, "INITIAL_STOCK", "INIT-003", 0.0, 85.0, 85.0, null, "Main Warehouse / Rack B", "Alex Vance", now - 5 * day),
        StockLedgerEntity("led-4", prodOfficeChairs.id, whMain.id, locMainDispatch.id, "RECEIPT", "REC-001", 0.0, 100.0, 100.0, "Apex Supplies Ltd", "Main Warehouse / Dispatch Area", "Alex Vance", now - 3 * day),
        StockLedgerEntity("led-5", prodOfficeChairs.id, whMain.id, locMainDispatch.id, "DELIVERY", "DEL-014", 100.0, -10.0, 90.0, "Main Warehouse / Dispatch Area", "Metro Tech Corp", "Sam Rivera", now - 2 * day),
        StockLedgerEntity("led-6", prodCopperWire.id, whProd.id, locProdRackB.id, "INITIAL_STOCK", "INIT-004", 0.0, 650.0, 650.0, null, "Production Warehouse / Rack B", "Alex Vance", now - 5 * day),
        StockLedgerEntity("led-7", prodWoodenTables.id, whMain.id, locMainFloor.id, "ADJUSTMENT", "ADJ-002", 15.0, -3.0, 12.0, "Main Warehouse / Production Floor", "Physical Count Variance", "Sam Rivera", now - 1 * day)
    )
    seedLedger.forEach { dao.insertLedgerEntry(it) }

    // 8. Receipts (Done & Pending)
    val rec1 = ReceiptEntity(
        id = "rec-001",
        receiptNumber = "REC-001",
        supplier = "Apex Supplies Ltd",
        warehouseId = whMain.id,
        destinationLocationId = locMainDispatch.id,
        status = "DONE",
        notes = "Standard replenishment batch",
        createdBy = "Alex Vance",
        createdAt = now - 3 * day,
        validatedAt = now - 3 * day
    )
    val rec2 = ReceiptEntity(
        id = "rec-002",
        receiptNumber = "REC-002",
        supplier = "Global Industrial Alloys",
        warehouseId = whMain.id,
        destinationLocationId = locMainRackA.id,
        status = "READY",
        notes = "Incoming 100 KG Steel Rods shipment",
        createdBy = "Alex Vance",
        createdAt = now - 4 * 3600_000L
    )
    dao.insertReceipt(rec1)
    dao.insertReceipt(rec2)
    dao.insertReceiptItems(listOf(
        ReceiptItemEntity("ritm-1", rec1.id, prodOfficeChairs.id, 100.0, "Units"),
        ReceiptItemEntity("ritm-2", rec2.id, prodSteelRods.id, 100.0, "KG")
    ))

    // 9. Deliveries (Done & Pending)
    val del1 = DeliveryEntity(
        id = "del-014",
        deliveryNumber = "DEL-014",
        customer = "Metro Tech Corp",
        warehouseId = whMain.id,
        sourceLocationId = locMainDispatch.id,
        status = "DONE",
        notes = "Client workspace outfitting",
        createdBy = "Sam Rivera",
        createdAt = now - 2 * day,
        completedAt = now - 2 * day
    )
    val del2 = DeliveryEntity(
        id = "del-015",
        deliveryNumber = "DEL-015",
        customer = "Horizon Builders",
        warehouseId = whMain.id,
        sourceLocationId = locMainRackB.id,
        status = "READY",
        notes = "Urgent construction materials batch",
        createdBy = "Alex Vance",
        createdAt = now - 6 * 3600_000L
    )
    dao.insertDelivery(del1)
    dao.insertDelivery(del2)
    dao.insertDeliveryItems(listOf(
        DeliveryItemEntity("ditm-1", del1.id, prodOfficeChairs.id, 10.0, "Units"),
        DeliveryItemEntity("ditm-2", del2.id, prodSteelSheets.id, 15.0, "Units")
    ))

    // 10. Transfers
    val tr1 = TransferEntity(
        id = "tr-008",
        transferNumber = "TR-008",
        sourceWarehouseId = whMain.id,
        sourceLocationId = locMainRackA.id,
        destinationWarehouseId = whProd.id,
        destinationLocationId = locProdRackA.id,
        status = "READY",
        notes = "Shift stock to production buffer line",
        createdBy = "Alex Vance",
        createdAt = now - 2 * 3600_000L
    )
    dao.insertTransfer(tr1)
    dao.insertTransferItems(listOf(
        TransferItemEntity("titm-1", tr1.id, prodSteelRods.id, 50.0)
    ))

    // 11. Adjustments
    val adj1 = AdjustmentEntity(
        id = "adj-002",
        adjustmentNumber = "ADJ-002",
        warehouseId = whMain.id,
        locationId = locMainFloor.id,
        productId = prodWoodenTables.id,
        systemQuantity = 15.0,
        physicalCount = 12.0,
        difference = -3.0,
        reason = "Damaged during transit inspection",
        status = "DONE",
        createdBy = "Sam Rivera",
        createdAt = now - 1 * day
    )
    dao.insertAdjustment(adj1)
}
