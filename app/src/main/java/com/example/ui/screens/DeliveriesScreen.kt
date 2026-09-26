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
import androidx.compose.material.icons.filled.Outbox
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
import com.example.ui.components.CreateDeliveryDialog
import com.example.ui.theme.ClaySurface
import com.example.ui.theme.ClaySurfaceAlt
import com.example.ui.theme.ForestInk
import com.example.ui.theme.JewelNavy
import com.example.ui.theme.JewelTeal
import com.example.ui.theme.MutedSageGrey
import com.example.ui.theme.clayCard
import com.example.ui.viewmodel.InventoryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DeliveriesScreen(
    viewModel: InventoryViewModel,
    onMenuClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val deliveries by viewModel.deliveries.collectAsState()
    val products by viewModel.products.collectAsState()
    val warehouses by viewModel.warehouses.collectAsState()
    val locations by viewModel.locations.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showCreateDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.US) }
    val whMap = warehouses.associateBy { it.id }
    val locMap = locations.associateBy { it.id }

    val filtered = deliveries.filter { del ->
        searchQuery.isBlank() ||
            del.deliveryNumber.contains(searchQuery, ignoreCase = true) ||
            del.customer.contains(searchQuery, ignoreCase = true) ||
            del.status.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp)
    ) {
        AppHeader(
            title = "Delivery Orders",
            subtitle = "Outbound stock orders & fulfillment workflow",
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
                        text = "Outbound Deliveries (${deliveries.size})",
                        style = TextStyle(color = ForestInk, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    )
                    ClayButton(
                        text = "New Delivery",
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
                            text = "No delivery orders found",
                            style = TextStyle(color = MutedSageGrey, fontSize = 13.sp)
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        filtered.forEach { delivery ->
                            val whName = whMap[delivery.warehouseId]?.name ?: "Warehouse"
                            val locName = locMap[delivery.sourceLocationId]?.name ?: "Dispatch"

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
                                                imageVector = Icons.Default.Outbox,
                                                contentDescription = null,
                                                tint = JewelNavy,
                                                modifier = Modifier.padding(end = 8.dp)
                                            )
                                            Column {
                                                Text(
                                                    text = delivery.deliveryNumber,
                                                    style = TextStyle(color = ForestInk, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                                )
                                                Text(
                                                    text = "Customer: ${delivery.customer}",
                                                    style = TextStyle(color = MutedSageGrey, fontSize = 12.sp)
                                                )
                                            }
                                        }

                                        ClayStatusPill(status = delivery.status)
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "Source: $whName / $locName",
                                                style = TextStyle(color = ForestInk, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                            )
                                            Text(
                                                text = "Created by ${delivery.createdBy} • ${dateFormat.format(Date(delivery.createdAt))}",
                                                style = TextStyle(color = MutedSageGrey, fontSize = 11.sp)
                                            )
                                        }

                                        // Workflow: DRAFT -> READY -> PICKED -> PACKED -> DONE
                                        if (delivery.status != "DONE") {
                                            val actionLabel = when (delivery.status) {
                                                "DRAFT" -> "Mark Ready"
                                                "READY" -> "Pick Items"
                                                "PICKED" -> "Pack Order"
                                                "PACKED" -> "Complete & Deduct"
                                                else -> "Advance"
                                            }
                                            ClayButton(
                                                text = actionLabel,
                                                icon = if (delivery.status == "PACKED") Icons.Default.Check else null,
                                                onClick = { viewModel.advanceDelivery(delivery.id) },
                                                isPrimary = delivery.status == "PACKED",
                                                cornerRadius = 12.dp
                                            )
                                        } else {
                                            Text(
                                                text = "Delivered & Stock Deducted",
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

    if (showCreateDialog) {
        CreateDeliveryDialog(
            products = products,
            warehouses = warehouses,
            locations = locations,
            onDismiss = { showCreateDialog = false },
            onSubmit = { customer, whId, locId, prodId, qty, unit, notes ->
                viewModel.createDelivery(customer, whId, locId, prodId, qty, unit, notes) {
                    showCreateDialog = false
                }
            }
        )
    }
}
