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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StockStatus
import com.example.ui.components.AppHeader
import com.example.ui.components.ClayButton
import com.example.ui.components.ClayCategoryTag
import com.example.ui.components.ClayStatusPill
import com.example.ui.components.CreateReceiptDialog
import com.example.ui.theme.ClaySurface
import com.example.ui.theme.ClaySurfaceAlt
import com.example.ui.theme.ForestInk
import com.example.ui.theme.JewelBrass
import com.example.ui.theme.JewelRust
import com.example.ui.theme.MutedSageGrey
import com.example.ui.theme.clayCard
import com.example.ui.viewmodel.InventoryViewModel

@Composable
fun ReorderRulesScreen(
    viewModel: InventoryViewModel,
    onMenuClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val products by viewModel.products.collectAsState()
    val warehouses by viewModel.warehouses.collectAsState()
    val locations by viewModel.locations.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showCreateReceipt by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    val lowStockItems = products.filter { it.status == StockStatus.LOW_STOCK || it.status == StockStatus.OUT_OF_STOCK }
    val normalItems = products.filter { it.status == StockStatus.IN_STOCK }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp)
    ) {
        AppHeader(
            title = "Reorder Rules & Alerts",
            subtitle = "Automatic inventory safety threshold monitors",
            searchQuery = searchQuery,
            onSearchChange = { searchQuery = it },
            onNewOperationClick = { showCreateReceipt = true },
            onMenuClick = onMenuClick
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Low stock warning banner
        if (lowStockItems.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clayCard(cornerRadius = 22.dp, surfaceColor = JewelRust.copy(alpha = 0.15f), elevation = 4.dp)
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.WarningAmber,
                        contentDescription = null,
                        tint = JewelRust,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Column {
                        Text(
                            text = "${lowStockItems.size} SKUs require immediate replenishment",
                            style = TextStyle(color = ForestInk, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Available inventory is below or equal to designated safety buffer reorder points.",
                            style = TextStyle(color = MutedSageGrey, fontSize = 12.sp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Active Reorder Rules Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clayCard(cornerRadius = 26.dp, surfaceColor = ClaySurface, elevation = 7.dp)
                .padding(18.dp)
        ) {
            Column {
                Text(
                    text = "Critical Replenishment Queue",
                    style = TextStyle(color = ForestInk, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(14.dp))

                if (lowStockItems.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 30.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "All products currently maintain optimal safety stock levels.",
                            style = TextStyle(color = MutedSageGrey, fontSize = 13.sp)
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        lowStockItems.forEach { item ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(ClaySurfaceAlt.copy(alpha = 0.5f))
                                    .padding(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = item.product.name,
                                                style = TextStyle(color = ForestInk, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                            )
                                            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                                            ClayCategoryTag(name = item.product.sku)
                                        }
                                        Text(
                                            text = "Available: ${item.availableStock.toInt()} ${item.product.unitOfMeasure} • Threshold: ${item.product.reorderLevel.toInt()}",
                                            style = TextStyle(color = MutedSageGrey, fontSize = 12.sp)
                                        )
                                        Text(
                                            text = "Recommended Replenish Order: +${item.product.reorderQuantity.toInt()} ${item.product.unitOfMeasure}",
                                            style = TextStyle(color = ForestInk, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        ClayStatusPill(status = item.status.label)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        ClayButton(
                                            text = "Reorder Now",
                                            icon = Icons.Default.Add,
                                            onClick = { showCreateReceipt = true },
                                            isPrimary = true,
                                            cornerRadius = 10.dp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Normal Stock Products
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clayCard(cornerRadius = 26.dp, surfaceColor = ClaySurface, elevation = 7.dp)
                .padding(18.dp)
        ) {
            Column {
                Text(
                    text = "Adequately Stocked Inventory (${normalItems.size})",
                    style = TextStyle(color = ForestInk, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    normalItems.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(ClaySurfaceAlt.copy(alpha = 0.35f))
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${item.product.name} (${item.product.sku})",
                                    style = TextStyle(color = ForestInk, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Available: ${item.availableStock.toInt()} ${item.product.unitOfMeasure} (Buffer: ${item.product.reorderLevel.toInt()})",
                                    style = TextStyle(color = MutedSageGrey, fontSize = 11.sp)
                                )
                            }
                            ClayStatusPill(status = item.status.label)
                        }
                    }
                }
            }
        }
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
}
