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
import androidx.compose.material.icons.filled.Tune
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
import com.example.ui.components.CreateAdjustmentDialog
import com.example.ui.theme.ClaySurface
import com.example.ui.theme.ClaySurfaceAlt
import com.example.ui.theme.ForestInk
import com.example.ui.theme.JewelRust
import com.example.ui.theme.JewelTeal
import com.example.ui.theme.MutedSageGrey
import com.example.ui.theme.clayCard
import com.example.ui.viewmodel.InventoryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdjustmentsScreen(
    viewModel: InventoryViewModel,
    onMenuClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val adjustments by viewModel.adjustments.collectAsState()
    val products by viewModel.products.collectAsState()
    val warehouses by viewModel.warehouses.collectAsState()
    val locations by viewModel.locations.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showCreateDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.US) }
    val prodMap = products.associateBy { it.product.id }
    val whMap = warehouses.associateBy { it.id }
    val locMap = locations.associateBy { it.id }

    val filtered = adjustments.filter { adj ->
        searchQuery.isBlank() ||
            adj.adjustmentNumber.contains(searchQuery, ignoreCase = true) ||
            adj.reason.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp)
    ) {
        AppHeader(
            title = "Inventory Adjustments",
            subtitle = "Physical cycle counts & stock variance reconciliations",
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
                        text = "Physical Count Audits (${adjustments.size})",
                        style = TextStyle(color = ForestInk, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    )
                    ClayButton(
                        text = "Perform Physical Count",
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
                            text = "No adjustment records recorded",
                            style = TextStyle(color = MutedSageGrey, fontSize = 13.sp)
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        filtered.forEach { adj ->
                            val prod = prodMap[adj.productId]
                            val prodName = prod?.product?.name ?: "Product"
                            val whName = whMap[adj.warehouseId]?.name ?: "Warehouse"
                            val locName = locMap[adj.locationId]?.name ?: "Location"

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
                                                imageVector = Icons.Default.Tune,
                                                contentDescription = null,
                                                tint = JewelRust,
                                                modifier = Modifier.padding(end = 8.dp)
                                            )
                                            Column {
                                                Text(
                                                    text = "${adj.adjustmentNumber} • $prodName",
                                                    style = TextStyle(color = ForestInk, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                                )
                                                Text(
                                                    text = "Location: $whName / $locName",
                                                    style = TextStyle(color = MutedSageGrey, fontSize = 12.sp)
                                                )
                                            }
                                        }

                                        ClayStatusPill(status = adj.status)
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                            Column {
                                                Text("SYSTEM QTY", style = TextStyle(color = MutedSageGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold))
                                                Text("${adj.systemQuantity.toInt()}", style = TextStyle(color = ForestInk, fontSize = 14.sp, fontWeight = FontWeight.Bold))
                                            }
                                            Column {
                                                Text("PHYSICAL COUNT", style = TextStyle(color = MutedSageGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold))
                                                Text("${adj.physicalCount.toInt()}", style = TextStyle(color = ForestInk, fontSize = 14.sp, fontWeight = FontWeight.Bold))
                                            }
                                            Column {
                                                Text("VARIANCE", style = TextStyle(color = MutedSageGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold))
                                                val diffStr = if (adj.difference >= 0) "+${adj.difference.toInt()}" else "${adj.difference.toInt()}"
                                                Text(
                                                    text = diffStr,
                                                    style = TextStyle(
                                                        color = if (adj.difference >= 0) JewelTeal else JewelRust,
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.ExtraBold
                                                    )
                                                )
                                            }
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = adj.reason,
                                                style = TextStyle(color = ForestInk, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                            )
                                            Text(
                                                text = "Audited by ${adj.createdBy} • ${dateFormat.format(Date(adj.createdAt))}",
                                                style = TextStyle(color = MutedSageGrey, fontSize = 10.sp)
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
        CreateAdjustmentDialog(
            products = products,
            warehouses = warehouses,
            locations = locations,
            onDismiss = { showCreateDialog = false },
            onSubmit = { whId, locId, prodId, physCount, reason ->
                viewModel.createAdjustment(whId, locId, prodId, physCount, reason) {
                    showCreateDialog = false
                }
            }
        )
    }
}
