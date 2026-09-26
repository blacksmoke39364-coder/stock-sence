package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Outbox
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CategoryEntity
import com.example.data.model.LocationEntity
import com.example.data.model.ProductWithStock
import com.example.data.model.WarehouseEntity
import com.example.ui.theme.ClaySunken
import com.example.ui.theme.ClaySurface
import com.example.ui.theme.ForestInk
import com.example.ui.theme.JewelBrass
import com.example.ui.theme.JewelNavy
import com.example.ui.theme.JewelRust
import com.example.ui.theme.JewelTeal
import com.example.ui.theme.MutedSageGrey
import com.example.ui.theme.clayCard
import com.example.ui.theme.claySunken

@Composable
fun OperationSelectionDialog(
    onDismiss: () -> Unit,
    onSelectReceipt: () -> Unit,
    onSelectDelivery: () -> Unit,
    onSelectTransfer: () -> Unit,
    onSelectAdjustment: () -> Unit,
    onSelectProduct: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clayCard(cornerRadius = 28.dp, surfaceColor = ClaySurface, elevation = 12.dp)
                .padding(22.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "New Inventory Action",
                            style = TextStyle(color = ForestInk, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Select workflow operation to launch",
                            style = TextStyle(color = MutedSageGrey, fontSize = 12.sp)
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MutedSageGrey)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OperationTypeTile(
                        icon = Icons.Default.LocalShipping,
                        iconTint = JewelTeal,
                        title = "Receive Stock (Receipt)",
                        subtitle = "Incoming shipments from suppliers",
                        onClick = {
                            onDismiss()
                            onSelectReceipt()
                        }
                    )
                    OperationTypeTile(
                        icon = Icons.Default.Outbox,
                        iconTint = JewelNavy,
                        title = "Delivery Order",
                        subtitle = "Ship items out to customers or dispatch",
                        onClick = {
                            onDismiss()
                            onSelectDelivery()
                        }
                    )
                    OperationTypeTile(
                        icon = Icons.Default.SwapHoriz,
                        iconTint = JewelBrass,
                        title = "Internal Transfer",
                        subtitle = "Move items between racks and warehouses",
                        onClick = {
                            onDismiss()
                            onSelectTransfer()
                        }
                    )
                    OperationTypeTile(
                        icon = Icons.Default.Tune,
                        iconTint = JewelRust,
                        title = "Inventory Adjustment",
                        subtitle = "Record physical stock count difference",
                        onClick = {
                            onDismiss()
                            onSelectAdjustment()
                        }
                    )
                    OperationTypeTile(
                        icon = Icons.Default.Add,
                        iconTint = ForestInk,
                        title = "Create New Product",
                        subtitle = "Add SKU with initial stock & reorder rules",
                        onClick = {
                            onDismiss()
                            onSelectProduct()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun OperationTypeTile(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clayCard(cornerRadius = 16.dp, surfaceColor = ClaySurface, elevation = 3.dp)
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = TextStyle(color = ForestInk, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                )
                Text(
                    text = subtitle,
                    style = TextStyle(color = MutedSageGrey, fontSize = 11.sp)
                )
            }
        }
    }
}

@Composable
fun CreateProductDialog(
    categories: List<CategoryEntity>,
    warehouses: List<WarehouseEntity>,
    locations: List<LocationEntity>,
    onDismiss: () -> Unit,
    onSubmit: (
        name: String,
        sku: String,
        categoryId: String,
        unit: String,
        initialStock: Double,
        warehouseId: String?,
        locationId: String?,
        reorderLevel: Double,
        reorderQuantity: Double,
        description: String
    ) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var sku by remember { mutableStateOf("") }
    var selectedCatId by remember { mutableStateOf(categories.firstOrNull()?.id ?: "") }
    var unit by remember { mutableStateOf("Units") }
    var initialStock by remember { mutableStateOf("0") }
    var selectedWhId by remember { mutableStateOf(warehouses.firstOrNull()?.id ?: "") }
    var selectedLocId by remember { mutableStateOf(locations.firstOrNull()?.id ?: "") }
    var reorderLevel by remember { mutableStateOf("15") }
    var reorderQuantity by remember { mutableStateOf("50") }
    var description by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clayCard(cornerRadius = 28.dp, surfaceColor = ClaySurface, elevation = 12.dp)
                .padding(22.dp)
        ) {
            Column(modifier = Modifier.verticalScroll(scrollState)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Create New Product",
                        style = TextStyle(color = ForestInk, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MutedSageGrey)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                FormLabel("PRODUCT NAME *")
                ClaySearchField(value = name, onValueChange = { name = it }, placeholder = "e.g. Aluminum Struts")

                Spacer(modifier = Modifier.height(10.dp))

                FormLabel("SKU / UNIQUE CODE *")
                ClaySearchField(value = sku, onValueChange = { sku = it }, placeholder = "e.g. SKU-ALU-505")

                Spacer(modifier = Modifier.height(10.dp))

                FormLabel("CATEGORY")
                var catExpanded by remember { mutableStateOf(false) }
                val selectedCatName = categories.find { it.id == selectedCatId }?.name ?: "Select category"
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .claySunken(cornerRadius = 14.dp, sunkenColor = ClaySunken)
                        .clickable { catExpanded = true }
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Text(selectedCatName, style = TextStyle(color = ForestInk, fontSize = 13.sp))
                    DropdownMenu(expanded = catExpanded, onDismissRequest = { catExpanded = false }) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.name) },
                                onClick = {
                                    selectedCatId = cat.id
                                    catExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        FormLabel("UNIT OF MEASURE")
                        ClaySearchField(value = unit, onValueChange = { unit = it }, placeholder = "KG, Units, Box")
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        FormLabel("INITIAL STOCK")
                        ClaySearchField(value = initialStock, onValueChange = { initialStock = it }, placeholder = "0")
                    }
                }

                if ((initialStock.toDoubleOrNull() ?: 0.0) > 0) {
                    Spacer(modifier = Modifier.height(10.dp))
                    FormLabel("INITIAL LOCATION")
                    var locExpanded by remember { mutableStateOf(false) }
                    val locName = locations.find { it.id == selectedLocId }?.let { loc ->
                        val whName = warehouses.find { it.id == loc.warehouseId }?.name ?: "Warehouse"
                        "$whName / ${loc.name}"
                    } ?: "Select location"

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .claySunken(cornerRadius = 14.dp, sunkenColor = ClaySunken)
                            .clickable { locExpanded = true }
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        Text(locName, style = TextStyle(color = ForestInk, fontSize = 13.sp))
                        DropdownMenu(expanded = locExpanded, onDismissRequest = { locExpanded = false }) {
                            locations.forEach { loc ->
                                val wh = warehouses.find { it.id == loc.warehouseId }?.name ?: ""
                                DropdownMenuItem(
                                    text = { Text("$wh / ${loc.name}") },
                                    onClick = {
                                        selectedLocId = loc.id
                                        selectedWhId = loc.warehouseId
                                        locExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        FormLabel("REORDER LEVEL")
                        ClaySearchField(value = reorderLevel, onValueChange = { reorderLevel = it }, placeholder = "15")
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        FormLabel("REORDER QTY")
                        ClaySearchField(value = reorderQuantity, onValueChange = { reorderQuantity = it }, placeholder = "50")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                FormLabel("DESCRIPTION")
                ClaySearchField(value = description, onValueChange = { description = it }, placeholder = "Product specifications...")

                Spacer(modifier = Modifier.height(18.dp))

                ClayButton(
                    text = "Save Product",
                    onClick = {
                        if (name.isNotBlank() && sku.isNotBlank()) {
                            val initQty = initialStock.toDoubleOrNull() ?: 0.0
                            onSubmit(
                                name,
                                sku,
                                selectedCatId,
                                unit,
                                initQty,
                                if (initQty > 0) selectedWhId else null,
                                if (initQty > 0) selectedLocId else null,
                                reorderLevel.toDoubleOrNull() ?: 15.0,
                                reorderQuantity.toDoubleOrNull() ?: 50.0,
                                description
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun CreateReceiptDialog(
    products: List<ProductWithStock>,
    warehouses: List<WarehouseEntity>,
    locations: List<LocationEntity>,
    onDismiss: () -> Unit,
    onSubmit: (supplier: String, warehouseId: String, locationId: String, productId: String, quantity: Double, unit: String, notes: String) -> Unit
) {
    var supplier by remember { mutableStateOf("") }
    var selectedProdId by remember { mutableStateOf(products.firstOrNull()?.product?.id ?: "") }
    var quantity by remember { mutableStateOf("100") }
    var selectedLocId by remember { mutableStateOf(locations.firstOrNull()?.id ?: "") }
    var notes by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clayCard(cornerRadius = 28.dp, surfaceColor = ClaySurface, elevation = 12.dp)
                .padding(22.dp)
        ) {
            Column(modifier = Modifier.verticalScroll(scrollState)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "New Inbound Receipt",
                        style = TextStyle(color = ForestInk, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MutedSageGrey)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                FormLabel("SUPPLIER NAME *")
                ClaySearchField(value = supplier, onValueChange = { supplier = it }, placeholder = "e.g. Apex Industrial Supplies")

                Spacer(modifier = Modifier.height(10.dp))

                FormLabel("PRODUCT TO RECEIVE *")
                var prodExpanded by remember { mutableStateOf(false) }
                val prodName = products.find { it.product.id == selectedProdId }?.product?.name ?: "Select Product"
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .claySunken(cornerRadius = 14.dp, sunkenColor = ClaySunken)
                        .clickable { prodExpanded = true }
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Text(prodName, style = TextStyle(color = ForestInk, fontSize = 13.sp))
                    DropdownMenu(expanded = prodExpanded, onDismissRequest = { prodExpanded = false }) {
                        products.forEach { p ->
                            DropdownMenuItem(
                                text = { Text("${p.product.name} (${p.product.sku})") },
                                onClick = {
                                    selectedProdId = p.product.id
                                    prodExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                FormLabel("QUANTITY")
                ClaySearchField(value = quantity, onValueChange = { quantity = it }, placeholder = "100")

                Spacer(modifier = Modifier.height(10.dp))

                FormLabel("DESTINATION LOCATION")
                var locExpanded by remember { mutableStateOf(false) }
                val locObj = locations.find { it.id == selectedLocId }
                val locLabel = locObj?.let {
                    val wh = warehouses.find { w -> w.id == it.warehouseId }?.name ?: "Warehouse"
                    "$wh / ${it.name}"
                } ?: "Select location"

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .claySunken(cornerRadius = 14.dp, sunkenColor = ClaySunken)
                        .clickable { locExpanded = true }
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Text(locLabel, style = TextStyle(color = ForestInk, fontSize = 13.sp))
                    DropdownMenu(expanded = locExpanded, onDismissRequest = { locExpanded = false }) {
                        locations.forEach { loc ->
                            val wh = warehouses.find { it.id == loc.warehouseId }?.name ?: ""
                            DropdownMenuItem(
                                text = { Text("$wh / ${loc.name}") },
                                onClick = {
                                    selectedLocId = loc.id
                                    locExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                FormLabel("NOTES")
                ClaySearchField(value = notes, onValueChange = { notes = it }, placeholder = "Purchase order ref, batch...")

                Spacer(modifier = Modifier.height(18.dp))

                ClayButton(
                    text = "Create Inbound Receipt",
                    onClick = {
                        val qty = quantity.toDoubleOrNull() ?: 0.0
                        val loc = locations.find { it.id == selectedLocId }
                        if (supplier.isNotBlank() && selectedProdId.isNotBlank() && loc != null && qty > 0) {
                            val prodUnit = products.find { it.product.id == selectedProdId }?.product?.unitOfMeasure ?: "Units"
                            onSubmit(supplier, loc.warehouseId, loc.id, selectedProdId, qty, prodUnit, notes)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun CreateDeliveryDialog(
    products: List<ProductWithStock>,
    warehouses: List<WarehouseEntity>,
    locations: List<LocationEntity>,
    onDismiss: () -> Unit,
    onSubmit: (customer: String, warehouseId: String, locationId: String, productId: String, quantity: Double, unit: String, notes: String) -> Unit
) {
    var customer by remember { mutableStateOf("") }
    var selectedProdId by remember { mutableStateOf(products.firstOrNull()?.product?.id ?: "") }
    var quantity by remember { mutableStateOf("10") }
    var selectedLocId by remember { mutableStateOf(locations.firstOrNull()?.id ?: "") }
    var notes by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clayCard(cornerRadius = 28.dp, surfaceColor = ClaySurface, elevation = 12.dp)
                .padding(22.dp)
        ) {
            Column(modifier = Modifier.verticalScroll(scrollState)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "New Outbound Delivery",
                        style = TextStyle(color = ForestInk, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MutedSageGrey)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                FormLabel("CUSTOMER / DESTINATION *")
                ClaySearchField(value = customer, onValueChange = { customer = it }, placeholder = "e.g. Metro Tech Corp")

                Spacer(modifier = Modifier.height(10.dp))

                FormLabel("PRODUCT *")
                var prodExpanded by remember { mutableStateOf(false) }
                val prodObj = products.find { it.product.id == selectedProdId }
                val prodName = prodObj?.let { "${it.product.name} (Avail: ${it.availableStock.toInt()})" } ?: "Select Product"

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .claySunken(cornerRadius = 14.dp, sunkenColor = ClaySunken)
                        .clickable { prodExpanded = true }
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Text(prodName, style = TextStyle(color = ForestInk, fontSize = 13.sp))
                    DropdownMenu(expanded = prodExpanded, onDismissRequest = { prodExpanded = false }) {
                        products.forEach { p ->
                            DropdownMenuItem(
                                text = { Text("${p.product.name} - Available: ${p.availableStock.toInt()} ${p.product.unitOfMeasure}") },
                                onClick = {
                                    selectedProdId = p.product.id
                                    prodExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                FormLabel("DELIVERY QUANTITY")
                ClaySearchField(value = quantity, onValueChange = { quantity = it }, placeholder = "10")

                Spacer(modifier = Modifier.height(10.dp))

                FormLabel("DISPATCH FROM LOCATION")
                var locExpanded by remember { mutableStateOf(false) }
                val locObj = locations.find { it.id == selectedLocId }
                val locLabel = locObj?.let {
                    val wh = warehouses.find { w -> w.id == it.warehouseId }?.name ?: "Warehouse"
                    "$wh / ${it.name}"
                } ?: "Select location"

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .claySunken(cornerRadius = 14.dp, sunkenColor = ClaySunken)
                        .clickable { locExpanded = true }
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Text(locLabel, style = TextStyle(color = ForestInk, fontSize = 13.sp))
                    DropdownMenu(expanded = locExpanded, onDismissRequest = { locExpanded = false }) {
                        locations.forEach { loc ->
                            val wh = warehouses.find { it.id == loc.warehouseId }?.name ?: ""
                            DropdownMenuItem(
                                text = { Text("$wh / ${loc.name}") },
                                onClick = {
                                    selectedLocId = loc.id
                                    locExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                FormLabel("NOTES")
                ClaySearchField(value = notes, onValueChange = { notes = it }, placeholder = "Shipping address, tracking...")

                Spacer(modifier = Modifier.height(18.dp))

                ClayButton(
                    text = "Create Delivery Order",
                    onClick = {
                        val qty = quantity.toDoubleOrNull() ?: 0.0
                        val loc = locations.find { it.id == selectedLocId }
                        if (customer.isNotBlank() && selectedProdId.isNotBlank() && loc != null && qty > 0) {
                            val prodUnit = prodObj?.product?.unitOfMeasure ?: "Units"
                            onSubmit(customer, loc.warehouseId, loc.id, selectedProdId, qty, prodUnit, notes)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun CreateTransferDialog(
    products: List<ProductWithStock>,
    warehouses: List<WarehouseEntity>,
    locations: List<LocationEntity>,
    onDismiss: () -> Unit,
    onSubmit: (srcWh: String, srcLoc: String, dstWh: String, dstLoc: String, productId: String, quantity: Double, notes: String) -> Unit
) {
    var selectedProdId by remember { mutableStateOf(products.firstOrNull()?.product?.id ?: "") }
    var selectedSrcLocId by remember { mutableStateOf(locations.firstOrNull()?.id ?: "") }
    var selectedDstLocId by remember { mutableStateOf(locations.getOrNull(1)?.id ?: "") }
    var quantity by remember { mutableStateOf("50") }
    var notes by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clayCard(cornerRadius = 28.dp, surfaceColor = ClaySurface, elevation = 12.dp)
                .padding(22.dp)
        ) {
            Column(modifier = Modifier.verticalScroll(scrollState)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Schedule Internal Transfer",
                        style = TextStyle(color = ForestInk, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MutedSageGrey)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                FormLabel("PRODUCT TO TRANSFER *")
                var prodExpanded by remember { mutableStateOf(false) }
                val prodObj = products.find { it.product.id == selectedProdId }
                val prodName = prodObj?.let { "${it.product.name} (${it.product.sku})" } ?: "Select Product"

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .claySunken(cornerRadius = 14.dp, sunkenColor = ClaySunken)
                        .clickable { prodExpanded = true }
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Text(prodName, style = TextStyle(color = ForestInk, fontSize = 13.sp))
                    DropdownMenu(expanded = prodExpanded, onDismissRequest = { prodExpanded = false }) {
                        products.forEach { p ->
                            DropdownMenuItem(
                                text = { Text("${p.product.name} (${p.product.sku})") },
                                onClick = {
                                    selectedProdId = p.product.id
                                    prodExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                FormLabel("SOURCE LOCATION *")
                var srcExpanded by remember { mutableStateOf(false) }
                val srcLocObj = locations.find { it.id == selectedSrcLocId }
                val srcName = srcLocObj?.let {
                    val wh = warehouses.find { w -> w.id == it.warehouseId }?.name ?: ""
                    "$wh / ${it.name}"
                } ?: "Select source"

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .claySunken(cornerRadius = 14.dp, sunkenColor = ClaySunken)
                        .clickable { srcExpanded = true }
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Text(srcName, style = TextStyle(color = ForestInk, fontSize = 13.sp))
                    DropdownMenu(expanded = srcExpanded, onDismissRequest = { srcExpanded = false }) {
                        locations.forEach { loc ->
                            val wh = warehouses.find { it.id == loc.warehouseId }?.name ?: ""
                            DropdownMenuItem(
                                text = { Text("$wh / ${loc.name}") },
                                onClick = {
                                    selectedSrcLocId = loc.id
                                    srcExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                FormLabel("DESTINATION LOCATION *")
                var dstExpanded by remember { mutableStateOf(false) }
                val dstLocObj = locations.find { it.id == selectedDstLocId }
                val dstName = dstLocObj?.let {
                    val wh = warehouses.find { w -> w.id == it.warehouseId }?.name ?: ""
                    "$wh / ${it.name}"
                } ?: "Select destination"

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .claySunken(cornerRadius = 14.dp, sunkenColor = ClaySunken)
                        .clickable { dstExpanded = true }
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Text(dstName, style = TextStyle(color = ForestInk, fontSize = 13.sp))
                    DropdownMenu(expanded = dstExpanded, onDismissRequest = { dstExpanded = false }) {
                        locations.forEach { loc ->
                            val wh = warehouses.find { it.id == loc.warehouseId }?.name ?: ""
                            DropdownMenuItem(
                                text = { Text("$wh / ${loc.name}") },
                                onClick = {
                                    selectedDstLocId = loc.id
                                    dstExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                FormLabel("QUANTITY TO MOVE")
                ClaySearchField(value = quantity, onValueChange = { quantity = it }, placeholder = "50")

                Spacer(modifier = Modifier.height(10.dp))

                FormLabel("TRANSFER NOTES")
                ClaySearchField(value = notes, onValueChange = { notes = it }, placeholder = "Transfer purpose...")

                Spacer(modifier = Modifier.height(18.dp))

                ClayButton(
                    text = "Schedule Transfer",
                    onClick = {
                        val qty = quantity.toDoubleOrNull() ?: 0.0
                        val src = locations.find { it.id == selectedSrcLocId }
                        val dst = locations.find { it.id == selectedDstLocId }
                        if (src != null && dst != null && selectedProdId.isNotBlank() && qty > 0) {
                            onSubmit(src.warehouseId, src.id, dst.warehouseId, dst.id, selectedProdId, qty, notes)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun CreateAdjustmentDialog(
    products: List<ProductWithStock>,
    warehouses: List<WarehouseEntity>,
    locations: List<LocationEntity>,
    onDismiss: () -> Unit,
    onSubmit: (warehouseId: String, locationId: String, productId: String, physicalCount: Double, reason: String) -> Unit
) {
    var selectedProdId by remember { mutableStateOf(products.firstOrNull()?.product?.id ?: "") }
    var selectedLocId by remember { mutableStateOf(locations.firstOrNull()?.id ?: "") }
    var physicalCount by remember { mutableStateOf("0") }
    var reason by remember { mutableStateOf("Monthly physical inventory count") }

    val prodObj = products.find { it.product.id == selectedProdId }
    val systemQty = prodObj?.availableStock ?: 0.0
    val phys = physicalCount.toDoubleOrNull() ?: 0.0
    val diff = phys - systemQty

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clayCard(cornerRadius = 28.dp, surfaceColor = ClaySurface, elevation = 12.dp)
                .padding(22.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Physical Stock Count",
                        style = TextStyle(color = ForestInk, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MutedSageGrey)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                FormLabel("PRODUCT TO AUDIT")
                var prodExpanded by remember { mutableStateOf(false) }
                val prodName = prodObj?.product?.name ?: "Select Product"

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .claySunken(cornerRadius = 14.dp, sunkenColor = ClaySunken)
                        .clickable { prodExpanded = true }
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Text(prodName, style = TextStyle(color = ForestInk, fontSize = 13.sp))
                    DropdownMenu(expanded = prodExpanded, onDismissRequest = { prodExpanded = false }) {
                        products.forEach { p ->
                            DropdownMenuItem(
                                text = { Text("${p.product.name} (System: ${p.availableStock.toInt()})") },
                                onClick = {
                                    selectedProdId = p.product.id
                                    physicalCount = p.availableStock.toInt().toString()
                                    prodExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                FormLabel("AUDIT LOCATION")
                var locExpanded by remember { mutableStateOf(false) }
                val locObj = locations.find { it.id == selectedLocId }
                val locLabel = locObj?.let {
                    val wh = warehouses.find { w -> w.id == it.warehouseId }?.name ?: ""
                    "$wh / ${it.name}"
                } ?: "Select location"

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .claySunken(cornerRadius = 14.dp, sunkenColor = ClaySunken)
                        .clickable { locExpanded = true }
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Text(locLabel, style = TextStyle(color = ForestInk, fontSize = 13.sp))
                    DropdownMenu(expanded = locExpanded, onDismissRequest = { locExpanded = false }) {
                        locations.forEach { loc ->
                            val wh = warehouses.find { it.id == loc.warehouseId }?.name ?: ""
                            DropdownMenuItem(
                                text = { Text("$wh / ${loc.name}") },
                                onClick = {
                                    selectedLocId = loc.id
                                    locExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Comparison Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .claySunken(cornerRadius = 14.dp, sunkenColor = ClaySunken)
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("SYSTEM QUANTITY", style = TextStyle(color = MutedSageGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold))
                            Text("${systemQty.toInt()} units", style = TextStyle(color = ForestInk, fontSize = 15.sp, fontWeight = FontWeight.Bold))
                        }
                        Column {
                            Text("DIFFERENCE", style = TextStyle(color = MutedSageGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold))
                            Text(
                                if (diff >= 0) "+${diff.toInt()}" else "${diff.toInt()}",
                                style = TextStyle(
                                    color = if (diff >= 0) JewelTeal else JewelRust,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                FormLabel("PHYSICAL COUNTED QUANTITY")
                ClaySearchField(value = physicalCount, onValueChange = { physicalCount = it }, placeholder = "0")

                Spacer(modifier = Modifier.height(10.dp))

                FormLabel("REASON / DISCREPANCY NOTE")
                ClaySearchField(value = reason, onValueChange = { reason = it }, placeholder = "e.g. Audit variance, damaged stock...")

                Spacer(modifier = Modifier.height(18.dp))

                ClayButton(
                    text = "Record & Adjust Stock",
                    onClick = {
                        val loc = locations.find { it.id == selectedLocId }
                        if (loc != null && selectedProdId.isNotBlank() && phys >= 0) {
                            onSubmit(loc.warehouseId, loc.id, selectedProdId, phys, reason)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun FormLabel(text: String) {
    Text(
        text = text,
        style = TextStyle(
            color = MutedSageGrey,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
        ),
        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
    )
}
