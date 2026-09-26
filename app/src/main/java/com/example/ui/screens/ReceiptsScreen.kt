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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalShipping
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AppHeader
import com.example.ui.components.ClayButton
import com.example.ui.components.ClayStatusPill
import com.example.ui.components.CreateReceiptDialog
import com.example.ui.theme.ClaySurface
import com.example.ui.theme.ClaySurfaceAlt
import com.example.ui.theme.ForestInk
import com.example.ui.theme.JewelBrass
import com.example.ui.theme.JewelTeal
import com.example.ui.theme.MutedSageGrey
import com.example.ui.theme.clayCard
import com.example.ui.viewmodel.InventoryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReceiptsScreen(
    viewModel: InventoryViewModel,
    onMenuClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val receipts by viewModel.receipts.collectAsState()
    val products by viewModel.products.collectAsState()
    val warehouses by viewModel.warehouses.collectAsState()
    val locations by viewModel.locations.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showCreateDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.US) }
    val whMap = warehouses.associateBy { it.id }
    val locMap = locations.associateBy { it.id }

    val filtered = receipts.filter { rec ->
        searchQuery.isBlank() ||
            rec.receiptNumber.contains(searchQuery, ignoreCase = true) ||
            rec.supplier.contains(searchQuery, ignoreCase = true) ||
            rec.status.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp)
    ) {
        AppHeader(
            title = "Receipts",
            subtitle = "Incoming stock shipments & supplier receipts",
            searchQuery = searchQuery,
            onSearchChange = { searchQuery = it },
            onNewOperationClick = { showCreateDialog = true },
            onMenuClick = onMenuClick
        )

        Spacer(modifier = Modifier.height(14.dp))

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
                    Text(
                        text = "Receipt Orders (${receipts.size})",
                        style = TextStyle(color = ForestInk, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    )
                    ClayButton(
                        text = "New Receipt",
                        icon = Icons.Default.Add,
                        onClick = { showCreateDialog = true },
                        isPrimary = true,
                        cornerRadius = 14.dp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (filtered.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No receipt records found",
                            style = TextStyle(color = MutedSageGrey, fontSize = 13.sp)
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        filtered.forEach { receipt ->
                            val whName = whMap[receipt.warehouseId]?.name ?: "Warehouse"
                            val locName = locMap[receipt.destinationLocationId]?.name ?: "Location"

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(ClaySurfaceAlt.copy(alpha = 0.5f))
                                    .padding(14.dp)
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.LocalShipping,
                                                contentDescription = null,
                                                tint = JewelTeal,
                                                modifier = Modifier.padding(end = 8.dp)
                                            )
                                            Column {
                                                Text(
                                                    text = receipt.receiptNumber,
                                                    style = TextStyle(color = ForestInk, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                                )
                                                Text(
                                                    text = "Supplier: ${receipt.supplier}",
                                                    style = TextStyle(color = MutedSageGrey, fontSize = 12.sp)
                                                )
                                            }
                                        }

                                        ClayStatusPill(status = receipt.status)
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "Destination: $whName / $locName",
                                                style = TextStyle(color = ForestInk, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                            )
                                            Text(
                                                text = "Created by ${receipt.createdBy} • ${dateFormat.format(Date(receipt.createdAt))}",
                                                style = TextStyle(color = MutedSageGrey, fontSize = 11.sp)
                                            )
                                        }

                                        // Workflow Actions: DRAFT -> READY -> DONE
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            if (receipt.status == "DRAFT") {
                                                ClayButton(
                                                    text = "Mark Ready",
                                                    onClick = { viewModel.advanceReceipt(receipt.id) },
                                                    isPrimary = false,
                                                    cornerRadius = 12.dp
                                                )
                                            }
                                            if (receipt.status != "DONE") {
                                                ClayButton(
                                                    text = "Validate & Add Stock",
                                                    icon = Icons.Default.Check,
                                                    onClick = { viewModel.validateReceipt(receipt.id) },
                                                    isPrimary = true,
                                                    cornerRadius = 12.dp
                                                )
                                            } else {
                                                Text(
                                                    text = "Stock Added",
                                                    style = TextStyle(color = JewelTeal, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
        }
    }

    if (showCreateDialog) {
        CreateReceiptDialog(
            products = products,
            warehouses = warehouses,
            locations = locations,
            onDismiss = { showCreateDialog = false },
            onSubmit = { supplier, whId, locId, prodId, qty, unit, notes ->
                viewModel.createReceipt(supplier, whId, locId, prodId, qty, unit, notes) {
                    showCreateDialog = false
                }
            }
        )
    }
}
