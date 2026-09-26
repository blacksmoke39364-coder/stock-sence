package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OperationSummary
import com.example.ui.theme.ClaySunken
import com.example.ui.theme.ClaySurface
import com.example.ui.theme.ClaySurfaceAlt
import com.example.ui.theme.ForestInk
import com.example.ui.theme.JewelBrass
import com.example.ui.theme.JewelNavy
import com.example.ui.theme.JewelRust
import com.example.ui.theme.JewelTeal
import com.example.ui.theme.MutedSageGrey
import com.example.ui.theme.clayCard
import com.example.ui.theme.claySunken
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OperationsTableCard(
    operations: List<OperationSummary>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedDocType: String,
    onDocTypeChange: (String) -> Unit,
    selectedStatus: String,
    onStatusChange: (String) -> Unit,
    onViewAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val dateFormat = remember { SimpleDateFormat("MMM dd, HH:mm", Locale.US) }

    Box(
        modifier = modifier
            .clayCard(cornerRadius = 26.dp, surfaceColor = ClaySurface, elevation = 7.dp)
            .padding(18.dp)
    ) {
        Column {
            // Card Title & Count Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Recent Operations",
                        style = TextStyle(
                            color = ForestInk,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(JewelTeal.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${operations.size} moves",
                            style = TextStyle(
                                color = JewelTeal,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                ClayButton(
                    text = "View All",
                    onClick = onViewAllClick,
                    isPrimary = false,
                    cornerRadius = 14.dp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Filter bar: Search + Doc Type selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ClaySearchField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = "Filter reference or product...",
                    leadingIcon = Icons.Default.Search,
                    modifier = Modifier.weight(1f)
                )

                // Doc type pills
                ClaySegmentedToggle(
                    options = listOf("All", "Receipt", "Delivery", "Transfer"),
                    selectedIndex = when (selectedDocType) {
                        "Receipt" -> 1
                        "Delivery" -> 2
                        "Transfer" -> 3
                        else -> 0
                    },
                    onSelect = { index ->
                        val target = when (index) {
                            1 -> "Receipt"
                            2 -> "Delivery"
                            3 -> "Transfer"
                            else -> "All"
                        }
                        onDocTypeChange(target)
                    }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Horizontally Scrollable Table Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
            ) {
                Column(modifier = Modifier.width(780.dp)) {
                    // Sunken Clay Header Strip
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .claySunken(cornerRadius = 12.dp, sunkenColor = ClaySunken)
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("REFERENCE", modifier = Modifier.width(100.dp), style = tableHeaderStyle)
                            Text("TYPE", modifier = Modifier.width(95.dp), style = tableHeaderStyle)
                            Text("PRODUCT / DETAILS", modifier = Modifier.width(170.dp), style = tableHeaderStyle)
                            Text("LOCATION", modifier = Modifier.width(150.dp), style = tableHeaderStyle)
                            Text("QTY", modifier = Modifier.width(70.dp), style = tableHeaderStyle)
                            Text("STATUS", modifier = Modifier.width(100.dp), style = tableHeaderStyle)
                            Text("DATE", modifier = Modifier.width(95.dp), style = tableHeaderStyle)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Rows
                    if (operations.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No matching operations found",
                                style = TextStyle(color = MutedSageGrey, fontSize = 13.sp)
                            )
                        }
                    } else {
                        operations.take(8).forEachIndexed { index, op ->
                            val isEven = index % 2 == 0
                            val rowBg = if (isEven) ClaySurfaceAlt.copy(alpha = 0.5f) else Color.Transparent

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(rowBg)
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Reference
                                Text(
                                    text = op.reference,
                                    modifier = Modifier.width(100.dp),
                                    style = TextStyle(
                                        color = ForestInk,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )

                                // Type tag
                                Box(modifier = Modifier.width(95.dp)) {
                                    val (typeColor, tagText) = when (op.type) {
                                        "Receipt" -> Pair(JewelTeal, "Receipt")
                                        "Delivery" -> Pair(JewelNavy, "Delivery")
                                        "Transfer" -> Pair(JewelBrass, "Transfer")
                                        else -> Pair(JewelRust, "Adjustment")
                                    }
                                    ClayCategoryTag(name = tagText, color = typeColor)
                                }

                                // Product
                                Text(
                                    text = op.productName,
                                    modifier = Modifier.width(170.dp),
                                    style = TextStyle(
                                        color = ForestInk,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    maxLines = 1
                                )

                                // Location
                                Text(
                                    text = "${op.warehouseName} / ${op.locationName}",
                                    modifier = Modifier.width(150.dp),
                                    style = TextStyle(
                                        color = MutedSageGrey,
                                        fontSize = 12.sp
                                    ),
                                    maxLines = 1
                                )

                                // Quantity
                                val qtyStr = if (op.quantity >= 0) "+${op.quantity.toInt()}" else "${op.quantity.toInt()}"
                                Text(
                                    text = qtyStr,
                                    modifier = Modifier.width(70.dp),
                                    style = TextStyle(
                                        color = if (op.quantity >= 0) ForestInk else JewelRust,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )

                                // Status
                                Box(modifier = Modifier.width(100.dp)) {
                                    ClayStatusPill(status = op.status)
                                }

                                // Date
                                Text(
                                    text = dateFormat.format(Date(op.date)),
                                    modifier = Modifier.width(95.dp),
                                    style = TextStyle(
                                        color = MutedSageGrey,
                                        fontSize = 11.sp
                                    )
                                )
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
