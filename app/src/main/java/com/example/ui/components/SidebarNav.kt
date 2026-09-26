package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Outbox
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.data.model.UserEntity
import com.example.ui.theme.ClayHighlight
import com.example.ui.theme.ClaySunken
import com.example.ui.theme.ClaySurface
import com.example.ui.theme.ClaySurfaceAlt
import com.example.ui.theme.ForestInk
import com.example.ui.theme.JewelNavy
import com.example.ui.theme.JewelRust
import com.example.ui.theme.JewelTeal
import com.example.ui.theme.MutedSageGrey
import com.example.ui.theme.clayCard
import com.example.ui.theme.clayPill
import com.example.ui.theme.claySunken
import com.example.ui.viewmodel.Screen

@Composable
fun SidebarNav(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    currentUser: UserEntity?,
    onSwitchRole: (Boolean) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(260.dp)
            .clayCard(cornerRadius = 28.dp, surfaceColor = ClaySurface, elevation = 8.dp)
            .padding(18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(scrollState)
        ) {
            // Brand Logo & Wordmark
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate(Screen.Dashboard) }
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clayCard(cornerRadius = 14.dp, surfaceColor = JewelTeal, elevation = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Inventory,
                        contentDescription = "StockSense Logo",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "STOCKSENSE",
                        style = TextStyle(
                            color = ForestInk,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                    )
                    Text(
                        text = "Inventory Intelligence",
                        style = TextStyle(
                            color = MutedSageGrey,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Group: OPERATIONS
            NavGroupHeader(title = "OPERATIONS")

            NavItem(
                icon = Icons.Default.Dashboard,
                label = "Dashboard",
                isActive = currentScreen is Screen.Dashboard,
                onClick = { onNavigate(Screen.Dashboard) }
            )
            NavItem(
                icon = Icons.Default.Inventory,
                label = "Products",
                isActive = currentScreen is Screen.Products || currentScreen is Screen.ProductDetail,
                onClick = { onNavigate(Screen.Products) }
            )
            NavItem(
                icon = Icons.Default.LocalShipping,
                label = "Receipts",
                isActive = currentScreen is Screen.Receipts,
                onClick = { onNavigate(Screen.Receipts) }
            )
            NavItem(
                icon = Icons.Default.Outbox,
                label = "Delivery Orders",
                isActive = currentScreen is Screen.Deliveries,
                onClick = { onNavigate(Screen.Deliveries) }
            )
            NavItem(
                icon = Icons.Default.SwapHoriz,
                label = "Internal Transfers",
                isActive = currentScreen is Screen.Transfers,
                onClick = { onNavigate(Screen.Transfers) }
            )
            NavItem(
                icon = Icons.Default.Tune,
                label = "Inventory Adjustments",
                isActive = currentScreen is Screen.Adjustments,
                onClick = { onNavigate(Screen.Adjustments) }
            )
            NavItem(
                icon = Icons.Default.History,
                label = "Move History",
                isActive = currentScreen is Screen.MoveHistory,
                onClick = { onNavigate(Screen.MoveHistory) }
            )
            NavItem(
                icon = Icons.Default.Assessment,
                label = "Stock Ledger",
                isActive = currentScreen is Screen.StockLedger,
                onClick = { onNavigate(Screen.StockLedger) }
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Group: MANAGEMENT
            NavGroupHeader(title = "MANAGEMENT")

            NavItem(
                icon = Icons.Default.Store,
                label = "Warehouses",
                isActive = currentScreen is Screen.Warehouses,
                onClick = { onNavigate(Screen.Warehouses) }
            )
            NavItem(
                icon = Icons.Default.Category,
                label = "Categories",
                isActive = currentScreen is Screen.Categories,
                onClick = { onNavigate(Screen.Categories) }
            )
            NavItem(
                icon = Icons.Default.Checklist,
                label = "Reorder Rules",
                isActive = currentScreen is Screen.ReorderRules,
                onClick = { onNavigate(Screen.ReorderRules) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Data Credits Meter
            ClayProgressMeter(
                label = "DATA CREDITS",
                percent = 68,
                barColor = JewelTeal,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Account Chip
            AccountChip(
                user = currentUser,
                onSwitchRole = onSwitchRole,
                onProfileClick = { onNavigate(Screen.Profile) },
                onLogout = onLogout
            )
        }
    }
}

@Composable
fun NavGroupHeader(title: String) {
    Text(
        text = title,
        style = TextStyle(
            color = MutedSageGrey,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        ),
        modifier = Modifier.padding(start = 10.dp, bottom = 6.dp)
    )
}

@Composable
fun NavItem(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .then(
                if (isActive) {
                    Modifier.clayPill(surfaceColor = JewelTeal, elevation = 4.dp)
                } else {
                    Modifier.clip(RoundedCornerShape(999.dp))
                }
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 9.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) Color.White else MutedSageGrey,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = label,
                style = TextStyle(
                    color = if (isActive) Color.White else ForestInk,
                    fontSize = 13.sp,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium
                )
            )
        }
    }
}

@Composable
fun AccountChip(
    user: UserEntity?,
    onSwitchRole: (Boolean) -> Unit,
    onProfileClick: () -> Unit,
    onLogout: () -> Unit
) {
    val isManager = user?.role == "INVENTORY_MANAGER"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clayCard(cornerRadius = 18.dp, surfaceColor = ClaySurfaceAlt, elevation = 4.dp)
            .clickable { onProfileClick() }
            .padding(10.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular avatar with initials
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isManager) JewelTeal else JewelNavy),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user?.avatarInitials ?: "US",
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = user?.fullName ?: "Guest",
                        style = TextStyle(
                            color = ForestInk,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1
                    )
                    Text(
                        text = if (isManager) "Manager" else "Staff",
                        style = TextStyle(
                            color = MutedSageGrey,
                            fontSize = 11.sp
                        )
                    )
                }

                IconButton(
                    onClick = { onSwitchRole(!isManager) },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Switch Role",
                        tint = MutedSageGrey,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
