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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.SwapHoriz
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
import com.example.ui.components.AppHeader
import com.example.ui.components.ClayButton
import com.example.ui.components.ClayStatusPill
import com.example.ui.components.CreateTransferDialog
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
fun TransfersScreen(
    viewModel: InventoryViewModel,
    onMenuClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val transfers by viewModel.transfers.collectAsState()
    val products by viewModel.products.collectAsState()
    val warehouses by viewModel.warehouses.collectAsState()
    val locations by viewModel.locations.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showCreateDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.US) }
    val whMap = warehouses.associateBy { it.id }
    val locMap = locations.associateBy { it.id }

    val filtered = transfers.filter { tr ->
        searchQuery.isBlank() ||
            tr.transferNumber.contains(searchQuery, ignoreCase = true) ||
            tr.status.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp)
    ) {
        AppHeader(
            title = "Internal Transfers",
            subtitle = "Rack-to-rack and warehouse-to-warehouse stock movements",
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
                        text = "Transfer Orders (${transfers.size})",
                        style = TextStyle(color = ForestInk, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    )
                    ClayButton(
                        text = "Schedule Transfer",
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
                            text = "No internal transfer records found",
                            style = TextStyle(color = MutedSageGrey, fontSize = 13.sp)
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        filtered.forEach { transfer ->
                            val srcWh = whMap[transfer.sourceWarehouseId]?.name ?: "Warehouse"
                            val srcLoc = locMap[transfer.sourceLocationId]?.name ?: "Location"
                            val dstWh = whMap[transfer.destinationWarehouseId]?.name ?: "Warehouse"
                            val dstLoc = locMap[transfer.destinationLocationId]?.name ?: "Location"

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
                                                imageVector = Icons.Default.SwapHoriz,
                                                contentDescription = null,
                                                tint = JewelBrass,
                                                modifier = Modifier.padding(end = 8.dp)
                                            )
                                            Column {
                                                Text(
                                                    text = transfer.transferNumber,
                                                    style = TextStyle(color = ForestInk, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                                )
                                                Text(
                                                    text = "$srcWh ($srcLoc) ➔ $dstWh ($dstLoc)",
                                                    style = TextStyle(color = ForestInk, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                                )
                                            }
                                        }

                                        ClayStatusPill(status = transfer.status)
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = if (transfer.notes.isNotBlank()) "Note: ${transfer.notes}" else "Net company inventory change: 0",
                                                style = TextStyle(color = MutedSageGrey, fontSize = 11.sp)
                                            )
                                            Text(
                                                text = "Created by ${transfer.createdBy} • ${dateFormat.format(Date(transfer.createdAt))}",
                                                style = TextStyle(color = MutedSageGrey, fontSize = 10.sp)
                                            )
                                        }

                                        if (transfer.status != "DONE") {
                                            ClayButton(
                                                text = "Validate & Move Stock",
                                                icon = Icons.Default.Check,
                                                onClick = { viewModel.validateTransfer(transfer.id) },
                                                isPrimary = true,
                                                cornerRadius = 12.dp
                                            )
                                        } else {
                                            Text(
                                                text = "Completed (Net-Zero Stock Shift)",
                                                style = TextStyle(color = JewelTeal, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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

    if (showCreateDialog) {
        CreateTransferDialog(
            products = products,
            warehouses = warehouses,
            locations = locations,
            onDismiss = { showCreateDialog = false },
            onSubmit = { srcWh, srcLoc, dstWh, dstLoc, prodId, qty, notes ->
                viewModel.createTransfer(srcWh, srcLoc, dstWh, dstLoc, prodId, qty, notes) {
                    showCreateDialog = false
                }
            }
        )
    }
}
