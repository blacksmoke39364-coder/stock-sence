package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StockStatus
import com.example.ui.components.AppHeader
import com.example.ui.components.ClayButton
import com.example.ui.components.ClayCategoryTag
import com.example.ui.components.ClaySearchField
import com.example.ui.components.ClaySegmentedToggle
import com.example.ui.components.ClayStatusPill
import com.example.ui.components.CreateProductDialog
import com.example.ui.theme.ClaySunken
import com.example.ui.theme.ClaySurface
import com.example.ui.theme.ClaySurfaceAlt
import com.example.ui.theme.ForestInk
import com.example.ui.theme.JewelTeal
import com.example.ui.theme.MutedSageGrey
import com.example.ui.theme.clayCard
import com.example.ui.theme.claySunken
import com.example.ui.viewmodel.InventoryViewModel
import com.example.ui.viewmodel.Screen

@Composable
fun ProductsScreen(
    viewModel: InventoryViewModel,
    onMenuClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val products by viewModel.products.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val warehouses by viewModel.warehouses.collectAsState()
    val locations by viewModel.locations.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf("All") }
    var showCreateDialog by remember { mutableStateOf(false) }

    val verticalScroll = rememberScrollState()
    val horizontalScroll = rememberScrollState()

    val filtered = products.filter { p ->
        val matchesQuery = searchQuery.isBlank() ||
            p.product.name.contains(searchQuery, ignoreCase = true) ||
            p.product.sku.contains(searchQuery, ignoreCase = true) ||
            p.categoryName.contains(searchQuery, ignoreCase = true)

        val matchesStatus = when (selectedStatusFilter) {
            "In Stock" -> p.status == StockStatus.IN_STOCK
            "Low Stock" -> p.status == StockStatus.LOW_STOCK
            "Out of Stock" -> p.status == StockStatus.OUT_OF_STOCK
            else -> true
        }
        matchesQuery && matchesStatus
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(verticalScroll)
            .padding(20.dp)
    ) {
        AppHeader(
            title = "Products",
            subtitle = "${products.size} tracked catalog items",
            searchQuery = searchQuery,
            onSearchChange = { searchQuery = it },
            onNewOperationClick = { showCreateDialog = true },
            onMenuClick = onMenuClick
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Main Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clayCard(cornerRadius = 26.dp, surfaceColor = ClaySurface, elevation = 7.dp)
                .padding(18.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ClaySegmentedToggle(
                        options = listOf("All", "In Stock", "Low Stock", "Out of Stock"),
                        selectedIndex = when (selectedStatusFilter) {
                            "In Stock" -> 1
                            "Low Stock" -> 2
                            "Out of Stock" -> 3
                            else -> 0
                        },
                        onSelect = {
                            selectedStatusFilter = when (it) {
                                1 -> "In Stock"
                                2 -> "Low Stock"
                                3 -> "Out of Stock"
                                else -> "All"
                            }
                        }
                    )

                    ClayButton(
                        text = "Add Product",
                        icon = Icons.Default.Add,
                        onClick = { showCreateDialog = true },
                        isPrimary = true,
                        cornerRadius = 14.dp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Scrollable Table
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(horizontalScroll)
                ) {
                    Column(modifier = Modifier.width(820.dp)) {
                        // Header
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .claySunken(cornerRadius = 12.dp, sunkenColor = ClaySunken)
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Text("PRODUCT", modifier = Modifier.width(180.dp), style = tableHeaderStyle)
                                Text("SKU", modifier = Modifier.width(130.dp), style = tableHeaderStyle)
                                Text("CATEGORY", modifier = Modifier.width(130.dp), style = tableHeaderStyle)
                                Text("TOTAL", modifier = Modifier.width(80.dp), style = tableHeaderStyle)
                                Text("AVAILABLE", modifier = Modifier.width(90.dp), style = tableHeaderStyle)
                                Text("REORDER", modifier = Modifier.width(80.dp), style = tableHeaderStyle)
                                Text("STATUS", modifier = Modifier.width(90.dp), style = tableHeaderStyle)
                                Text("", modifier = Modifier.width(40.dp), style = tableHeaderStyle)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        if (filtered.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No products found matching filters",
                                    style = TextStyle(color = MutedSageGrey, fontSize = 13.sp)
                                )
                            }
                        } else {
                            filtered.forEachIndexed { index, item ->
                                val rowBg = if (index % 2 == 0) ClaySurfaceAlt.copy(alpha = 0.5f) else Color.Transparent

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(rowBg)
                                        .clickable { viewModel.navigateTo(Screen.ProductDetail(item.product.id)) }
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Product Name
                                    Text(
                                        text = item.product.name,
                                        modifier = Modifier.width(180.dp),
                                        style = TextStyle(color = ForestInk, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    )

                                    // SKU
                                    Text(
                                        text = item.product.sku,
                                        modifier = Modifier.width(130.dp),
                                        style = TextStyle(color = MutedSageGrey, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                    )

                                    // Category
                                    Box(modifier = Modifier.width(130.dp)) {
                                        ClayCategoryTag(name = item.categoryName)
                                    }

                                    // Total
                                    Text(
                                        text = "${item.totalStock.toInt()} ${item.product.unitOfMeasure}",
                                        modifier = Modifier.width(80.dp),
                                        style = TextStyle(color = ForestInk, fontSize = 12.sp)
                                    )

                                    // Available
                                    Text(
                                        text = "${item.availableStock.toInt()}",
                                        modifier = Modifier.width(90.dp),
                                        style = TextStyle(
                                            color = if (item.availableStock > 0) ForestInk else Color.Red,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )

                                    // Reorder Level
                                    Text(
                                        text = "${item.product.reorderLevel.toInt()}",
                                        modifier = Modifier.width(80.dp),
                                        style = TextStyle(color = MutedSageGrey, fontSize = 12.sp)
                                    )

                                    // Status Pill
                                    Box(modifier = Modifier.width(90.dp)) {
                                        ClayStatusPill(status = item.status.label)
                                    }

                                    // View detail arrow
                                    IconButton(
                                        onClick = { viewModel.navigateTo(Screen.ProductDetail(item.product.id)) },
                                        modifier = Modifier.width(40.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowForward,
                                            contentDescription = "View Detail",
                                            tint = JewelTeal,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateProductDialog(
            categories = categories,
            warehouses = warehouses,
            locations = locations,
            onDismiss = { showCreateDialog = false },
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
                    onSuccess = { showCreateDialog = false }
                )
            }
        )
    }
}

private val tableHeaderStyle = TextStyle(
    color = MutedSageGrey,
    fontSize = 10.sp,
    fontWeight = FontWeight.Bold,
    letterSpacing = 0.6.sp
)
