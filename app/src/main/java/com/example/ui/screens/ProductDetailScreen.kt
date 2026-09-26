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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ClayButton
import com.example.ui.components.ClayCategoryTag
import com.example.ui.components.ClayStatusPill
import com.example.ui.theme.ClaySunken
import com.example.ui.theme.ClaySurface
import com.example.ui.theme.ClaySurfaceAlt
import com.example.ui.theme.ForestInk
import com.example.ui.theme.JewelRust
import com.example.ui.theme.JewelTeal
import com.example.ui.theme.MutedSageGrey
import com.example.ui.theme.clayCard
import com.example.ui.theme.claySunken
import com.example.ui.viewmodel.InventoryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProductDetailScreen(
    productId: String,
    viewModel: InventoryViewModel,
    modifier: Modifier = Modifier
) {
    val products by viewModel.products.collectAsState()
    val ledger by viewModel.ledgerEntries.collectAsState()
    val locationStock by viewModel.selectedProductLocations.collectAsState()

    LaunchedEffect(productId) {
        viewModel.loadProductLocations(productId)
    }

    val productInfo = products.find { it.product.id == productId }
    val productLedger = ledger.filter { it.productId == productId }
    val scrollState = rememberScrollState()
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.US) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp)
    ) {
        // Back Button & Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateBack() },
                modifier = Modifier
                    .clayCard(cornerRadius = 14.dp, surfaceColor = ClaySurface, elevation = 4.dp)
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = ForestInk
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = productInfo?.product?.name ?: "Product Details",
                    style = TextStyle(color = ForestInk, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                )
                Text(
                    text = "SKU: ${productInfo?.product?.sku ?: ""} • Category: ${productInfo?.categoryName ?: ""}",
                    style = TextStyle(color = MutedSageGrey, fontSize = 12.sp)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        if (productInfo != null) {
            // Stock Summary KPIs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryKpiCard(
                    title = "TOTAL STOCK",
                    value = "${productInfo.totalStock.toInt()}",
                    unit = productInfo.product.unitOfMeasure,
                    modifier = Modifier.weight(1f)
                )
                SummaryKpiCard(
                    title = "AVAILABLE",
                    value = "${productInfo.availableStock.toInt()}",
                    unit = productInfo.product.unitOfMeasure,
                    highlightColor = JewelTeal,
                    modifier = Modifier.weight(1f)
                )
                SummaryKpiCard(
                    title = "RESERVED",
                    value = "${productInfo.reservedStock.toInt()}",
                    unit = productInfo.product.unitOfMeasure,
                    modifier = Modifier.weight(1f)
                )
                SummaryKpiCard(
                    title = "STATUS",
                    value = productInfo.status.label,
                    unit = "",
                    isStatus = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Reorder & Specs Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clayCard(cornerRadius = 24.dp, surfaceColor = ClaySurface, elevation = 6.dp)
                    .padding(18.dp)
            ) {
                Column {
                    Text(
                        text = "Reorder & Specifications",
                        style = TextStyle(color = ForestInk, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = productInfo.product.description.ifBlank { "No detailed specifications recorded for this SKU." },
                        style = TextStyle(color = MutedSageGrey, fontSize = 13.sp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SpecItem("Reorder Threshold", "${productInfo.product.reorderLevel.toInt()} ${productInfo.product.unitOfMeasure}")
                        SpecItem("Replenishment Qty", "${productInfo.product.reorderQuantity.toInt()} ${productInfo.product.unitOfMeasure}")
                        SpecItem("Unit of Measure", productInfo.product.unitOfMeasure)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stock by Warehouse & Location breakdown
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clayCard(cornerRadius = 24.dp, surfaceColor = ClaySurface, elevation = 6.dp)
                    .padding(18.dp)
            ) {
                Column {
                    Text(
                        text = "Inventory by Warehouse & Location",
                        style = TextStyle(color = ForestInk, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (locationStock.isEmpty()) {
                        Text(
                            text = "No location allocations recorded yet for this product.",
                            style = TextStyle(color = MutedSageGrey, fontSize = 13.sp)
                        )
                    } else {
                        locationStock.forEach { loc ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(ClaySurfaceAlt.copy(alpha = 0.5f))
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Warehouse,
                                        contentDescription = null,
                                        tint = JewelTeal,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    Column {
                                        Text(
                                            text = loc.warehouseName,
                                            style = TextStyle(color = ForestInk, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = "Location: ${loc.locationName}",
                                            style = TextStyle(color = MutedSageGrey, fontSize = 11.sp)
                                        )
                                    }
                                }

                                Text(
                                    text = "${loc.quantity.toInt()} ${productInfo.product.unitOfMeasure}",
                                    style = TextStyle(color = ForestInk, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stock Ledger / Movement Audit History for this Product
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clayCard(cornerRadius = 24.dp, surfaceColor = ClaySurface, elevation = 6.dp)
                    .padding(18.dp)
            ) {
                Column {
                    Text(
                        text = "Product Stock Movement Ledger",
                        style = TextStyle(color = ForestInk, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Permanent, immutable audit trail of every stock event",
                        style = TextStyle(color = MutedSageGrey, fontSize = 11.sp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    if (productLedger.isEmpty()) {
                        Text(
                            text = "No stock transactions recorded yet for this product.",
                            style = TextStyle(color = MutedSageGrey, fontSize = 13.sp)
                        )
                    } else {
                        productLedger.forEach { entry ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(ClaySurfaceAlt.copy(alpha = 0.4f))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = entry.referenceNumber,
                                            style = TextStyle(color = ForestInk, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        ClayCategoryTag(name = entry.operationType)
                                    }
                                    Text(
                                        text = "${entry.sourceLocationName ?: "Inbound"} -> ${entry.destinationLocationName ?: "Stock"} • ${entry.createdBy}",
                                        style = TextStyle(color = MutedSageGrey, fontSize = 11.sp)
                                    )
                                    Text(
                                        text = dateFormat.format(Date(entry.timestamp)),
                                        style = TextStyle(color = MutedSageGrey, fontSize = 10.sp)
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    val changeStr = if (entry.quantityChange >= 0) "+${entry.quantityChange.toInt()}" else "${entry.quantityChange.toInt()}"
                                    Text(
                                        text = changeStr,
                                        style = TextStyle(
                                            color = if (entry.quantityChange >= 0) JewelTeal else JewelRust,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    )
                                    Text(
                                        text = "Bal: ${entry.quantityAfter.toInt()}",
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

@Composable
fun SummaryKpiCard(
    title: String,
    value: String,
    unit: String,
    highlightColor: Color? = null,
    isStatus: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clayCard(cornerRadius = 18.dp, surfaceColor = ClaySurface, elevation = 4.dp)
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = title,
                style = TextStyle(color = MutedSageGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.6.sp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            if (isStatus) {
                ClayStatusPill(status = value)
            } else {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = value,
                        style = TextStyle(
                            color = highlightColor ?: ForestInk,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                    if (unit.isNotBlank()) {
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(text = unit, style = TextStyle(color = MutedSageGrey, fontSize = 10.sp))
                    }
                }
            }
        }
    }
}

@Composable
fun SpecItem(label: String, value: String) {
    Column {
        Text(text = label, style = TextStyle(color = MutedSageGrey, fontSize = 11.sp, fontWeight = FontWeight.Medium))
        Text(text = value, style = TextStyle(color = ForestInk, fontSize = 13.sp, fontWeight = FontWeight.Bold))
    }
}
