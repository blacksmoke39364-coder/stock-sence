package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.ui.components.ClayCategoryTag
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
fun StockLedgerScreen(
    viewModel: InventoryViewModel,
    onMenuClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val ledgerEntries by viewModel.ledgerEntries.collectAsState()
    val products by viewModel.products.collectAsState()
    val warehouses by viewModel.warehouses.collectAsState()
    val locations by viewModel.locations.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    val verticalScroll = rememberScrollState()
    val horizontalScroll = rememberScrollState()
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US) }

    val prodMap = products.associateBy { it.product.id }
    val whMap = warehouses.associateBy { it.id }
    val locMap = locations.associateBy { it.id }

    val filtered = ledgerEntries.filter { entry ->
        val prodName = prodMap[entry.productId]?.product?.name ?: ""
        searchQuery.isBlank() ||
            entry.referenceNumber.contains(searchQuery, ignoreCase = true) ||
            prodName.contains(searchQuery, ignoreCase = true) ||
            entry.operationType.contains(searchQuery, ignoreCase = true) ||
            entry.createdBy.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(verticalScroll)
            .padding(20.dp)
    ) {
        AppHeader(
            title = "Stock Ledger",
            subtitle = "Permanent, immutable audit trail of every stock modification",
            searchQuery = searchQuery,
            onSearchChange = { searchQuery = it },
            onNewOperationClick = { viewModel.showToast("Ledger is append-only. Perform an operation to add an entry.") },
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
                    Column {
                        Text(
                            text = "Ledger Transactions (${filtered.size})",
                            style = TextStyle(color = ForestInk, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Append-only database journal • Zero modification allowed",
                            style = TextStyle(color = MutedSageGrey, fontSize = 11.sp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(horizontalScroll)
                ) {
                    Column(modifier = Modifier.width(980.dp)) {
                        // Header
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .claySunken(cornerRadius = 12.dp, sunkenColor = ClaySunken)
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Text("TIMESTAMP", modifier = Modifier.width(130.dp), style = tableHeaderStyle)
                                Text("REF #", modifier = Modifier.width(100.dp), style = tableHeaderStyle)
                                Text("TYPE", modifier = Modifier.width(110.dp), style = tableHeaderStyle)
                                Text("PRODUCT", modifier = Modifier.width(170.dp), style = tableHeaderStyle)
                                Text("BEFORE", modifier = Modifier.width(75.dp), style = tableHeaderStyle)
                                Text("CHANGE", modifier = Modifier.width(80.dp), style = tableHeaderStyle)
                                Text("AFTER", modifier = Modifier.width(75.dp), style = tableHeaderStyle)
                                Text("ROUTE / DESTINATION", modifier = Modifier.width(140.dp), style = tableHeaderStyle)
                                Text("USER", modifier = Modifier.width(100.dp), style = tableHeaderStyle)
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
                                    text = "No ledger entries recorded",
                                    style = TextStyle(color = MutedSageGrey, fontSize = 13.sp)
                                )
                            }
                        } else {
                            filtered.forEachIndexed { index, entry ->
                                val rowBg = if (index % 2 == 0) ClaySurfaceAlt.copy(alpha = 0.5f) else Color.Transparent
                                val prodName = prodMap[entry.productId]?.product?.name ?: "Product"

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(rowBg)
                                        .padding(horizontal = 14.dp, vertical = 9.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Timestamp
                                    Text(
                                        text = dateFormat.format(Date(entry.timestamp)),
                                        modifier = Modifier.width(130.dp),
                                        style = TextStyle(color = MutedSageGrey, fontSize = 11.sp)
                                    )

                                    // Reference Number
                                    Text(
                                        text = entry.referenceNumber,
                                        modifier = Modifier.width(100.dp),
                                        style = TextStyle(color = ForestInk, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    )

                                    // Operation Type tag
                                    Box(modifier = Modifier.width(110.dp)) {
                                        ClayCategoryTag(name = entry.operationType)
                                    }

                                    // Product
                                    Text(
                                        text = prodName,
                                        modifier = Modifier.width(170.dp),
                                        style = TextStyle(color = ForestInk, fontSize = 12.sp, fontWeight = FontWeight.Medium),
                                        maxLines = 1
                                    )

                                    // Before
                                    Text(
                                        text = "${entry.quantityBefore.toInt()}",
                                        modifier = Modifier.width(75.dp),
                                        style = TextStyle(color = MutedSageGrey, fontSize = 12.sp)
                                    )

                                    // Change
                                    val changeStr = if (entry.quantityChange >= 0) "+${entry.quantityChange.toInt()}" else "${entry.quantityChange.toInt()}"
                                    Text(
                                        text = changeStr,
                                        modifier = Modifier.width(80.dp),
                                        style = TextStyle(
                                            color = if (entry.quantityChange >= 0) JewelTeal else JewelRust,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    )

                                    // After
                                    Text(
                                        text = "${entry.quantityAfter.toInt()}",
                                        modifier = Modifier.width(75.dp),
                                        style = TextStyle(color = ForestInk, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    )

                                    // Destination / Location
                                    Text(
                                        text = entry.destinationLocationName ?: "Warehouse",
                                        modifier = Modifier.width(140.dp),
                                        style = TextStyle(color = MutedSageGrey, fontSize = 11.sp),
                                        maxLines = 1
                                    )

                                    // User
                                    Text(
                                        text = entry.createdBy,
                                        modifier = Modifier.width(100.dp),
                                        style = TextStyle(color = ForestInk, fontSize = 11.sp, fontWeight = FontWeight.Medium),
                                        maxLines = 1
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

private val tableHeaderStyle = TextStyle(
    color = MutedSageGrey,
    fontSize = 10.sp,
    fontWeight = FontWeight.Bold,
    letterSpacing = 0.6.sp
)
