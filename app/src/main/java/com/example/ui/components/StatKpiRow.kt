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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Outbox
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DashboardKpis
import com.example.ui.theme.ClayHighlight
import com.example.ui.theme.ClaySurface
import com.example.ui.theme.ForestInk
import com.example.ui.theme.JewelBrass
import com.example.ui.theme.JewelNavy
import com.example.ui.theme.JewelRust
import com.example.ui.theme.JewelTeal
import com.example.ui.theme.MutedSageGrey
import com.example.ui.theme.clayCard
import java.text.NumberFormat
import java.util.Locale

@Composable
fun StatKpiRow(
    kpis: DashboardKpis,
    onKpiClick: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val numberFormat = NumberFormat.getNumberInstance(Locale.US)

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // First row of 3
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            KpiTile(
                icon = Icons.Default.Inventory2,
                iconTint = JewelTeal,
                label = "TOTAL IN STOCK",
                value = numberFormat.format(kpis.totalProductsInStock.toLong()),
                unit = "units",
                deltaText = "+12.4%",
                isDeltaPositive = true,
                modifier = Modifier.weight(1f),
                onClick = { onKpiClick?.invoke("stock") }
            )
            KpiTile(
                icon = Icons.Default.WarningAmber,
                iconTint = JewelBrass,
                label = "LOW STOCK ITEMS",
                value = kpis.lowStockCount.toString(),
                unit = "skus",
                deltaText = if (kpis.lowStockCount > 0) "Needs Reorder" else "Optimal",
                isDeltaPositive = kpis.lowStockCount == 0,
                modifier = Modifier.weight(1f),
                onClick = { onKpiClick?.invoke("low_stock") }
            )
            KpiTile(
                icon = Icons.Default.ErrorOutline,
                iconTint = JewelRust,
                label = "OUT OF STOCK",
                value = kpis.outOfStockCount.toString(),
                unit = "skus",
                deltaText = if (kpis.outOfStockCount > 0) "Action Req." else "Clear",
                isDeltaPositive = kpis.outOfStockCount == 0,
                modifier = Modifier.weight(1f),
                onClick = { onKpiClick?.invoke("out_of_stock") }
            )
        }

        // Second row of 3
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            KpiTile(
                icon = Icons.Default.LocalShipping,
                iconTint = JewelTeal,
                label = "PENDING RECEIPTS",
                value = kpis.pendingReceiptsCount.toString(),
                unit = "orders",
                deltaText = "Inbound",
                isDeltaPositive = true,
                modifier = Modifier.weight(1f),
                onClick = { onKpiClick?.invoke("receipts") }
            )
            KpiTile(
                icon = Icons.Default.Outbox,
                iconTint = JewelNavy,
                label = "PENDING DELIVERIES",
                value = kpis.pendingDeliveriesCount.toString(),
                unit = "orders",
                deltaText = "Outbound",
                isDeltaPositive = null,
                modifier = Modifier.weight(1f),
                onClick = { onKpiClick?.invoke("deliveries") }
            )
            KpiTile(
                icon = Icons.Default.SwapHoriz,
                iconTint = JewelBrass,
                label = "TRANSFERS SCHEDULED",
                value = kpis.internalTransfersScheduledCount.toString(),
                unit = "moves",
                deltaText = "Internal",
                isDeltaPositive = null,
                modifier = Modifier.weight(1f),
                onClick = { onKpiClick?.invoke("transfers") }
            )
        }
    }
}

@Composable
fun KpiTile(
    icon: ImageVector,
    iconTint: Color,
    label: String,
    value: String,
    unit: String,
    deltaText: String,
    isDeltaPositive: Boolean?,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .clayCard(cornerRadius = 24.dp, surfaceColor = ClaySurface, elevation = 6.dp)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Soft icon chip
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(iconTint.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Delta or status pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(
                            when (isDeltaPositive) {
                                true -> JewelTeal.copy(alpha = 0.15f)
                                false -> JewelRust.copy(alpha = 0.15f)
                                null -> JewelNavy.copy(alpha = 0.12f)
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isDeltaPositive == true) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = null,
                                tint = JewelTeal,
                                modifier = Modifier.size(10.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                        } else if (isDeltaPositive == false) {
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = null,
                                tint = JewelRust,
                                modifier = Modifier.size(10.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                        }
                        Text(
                            text = deltaText,
                            style = TextStyle(
                                color = when (isDeltaPositive) {
                                    true -> JewelTeal
                                    false -> JewelRust
                                    null -> JewelNavy
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = label,
                style = TextStyle(
                    color = MutedSageGrey,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.7.sp
                )
            )

            Spacer(modifier = Modifier.height(2.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    style = TextStyle(
                        color = ForestInk,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = unit,
                    style = TextStyle(
                        color = MutedSageGrey,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.padding(bottom = 3.dp)
                )
            }
        }
    }
}
