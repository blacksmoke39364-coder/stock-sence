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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.ui.components.ClayCategoryTag
import com.example.ui.components.ClayStatusPill
import com.example.ui.theme.ClaySurface
import com.example.ui.theme.ClaySurfaceAlt
import com.example.ui.theme.ForestInk
import com.example.ui.theme.JewelNavy
import com.example.ui.theme.JewelRust
import com.example.ui.theme.JewelTeal
import com.example.ui.theme.MutedSageGrey
import com.example.ui.theme.clayCard
import com.example.ui.viewmodel.InventoryViewModel

@Composable
fun ProfileScreen(
    viewModel: InventoryViewModel,
    onMenuClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val scrollState = rememberScrollState()

    val isManager = currentUser?.role == "INVENTORY_MANAGER"

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp)
    ) {
        AppHeader(
            title = "My Profile & Access",
            subtitle = "Role permissions & session authentication status",
            searchQuery = "",
            onSearchChange = {},
            onNewOperationClick = { viewModel.showToast("Profile settings synced.") },
            onMenuClick = onMenuClick
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Profile Identity Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clayCard(cornerRadius = 26.dp, surfaceColor = ClaySurface, elevation = 7.dp)
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(if (isManager) JewelTeal else JewelNavy),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentUser?.avatarInitials ?: "US",
                            style = TextStyle(color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentUser?.fullName ?: "Guest User",
                            style = TextStyle(color = ForestInk, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = currentUser?.email ?: "user@stocksense.io",
                            style = TextStyle(color = MutedSageGrey, fontSize = 13.sp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        ClayCategoryTag(
                            name = if (isManager) "INVENTORY MANAGER (FULL ACCESS)" else "WAREHOUSE STAFF (OPERATIONAL ACCESS)",
                            color = if (isManager) JewelTeal else JewelNavy
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Account Switcher for Demo testing
                Text(
                    text = "SWITCH DEMO ROLE",
                    style = TextStyle(color = MutedSageGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ClayButton(
                        text = "Alex Vance (Manager)",
                        onClick = { viewModel.switchDemoAccount(true) },
                        isPrimary = isManager,
                        modifier = Modifier.weight(1f),
                        cornerRadius = 14.dp
                    )
                    ClayButton(
                        text = "Sam Rivera (Staff)",
                        onClick = { viewModel.switchDemoAccount(false) },
                        isPrimary = !isManager,
                        modifier = Modifier.weight(1f),
                        cornerRadius = 14.dp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                ClayButton(
                    text = "Sign Out",
                    icon = Icons.Default.Logout,
                    onClick = { viewModel.logout() },
                    isPrimary = false,
                    backgroundColor = JewelRust.copy(alpha = 0.15f),
                    contentColor = JewelRust,
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 14.dp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Role-Based Permissions Matrix
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clayCard(cornerRadius = 26.dp, surfaceColor = ClaySurface, elevation = 7.dp)
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "Role Permissions Matrix",
                    style = TextStyle(color = ForestInk, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Enforced through transactional repository security logic",
                    style = TextStyle(color = MutedSageGrey, fontSize = 12.sp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                val permissions = listOf(
                    Triple("Create & Edit Products", true, isManager),
                    Triple("Manage Warehouses & Racks", true, isManager),
                    Triple("Manage Categories", true, isManager),
                    Triple("Receive Inbound Stock & Validate", true, true),
                    Triple("Fulfill Delivery Orders & Deduct", true, true),
                    Triple("Schedule & Validate Transfers", true, true),
                    Triple("Perform Physical Count Adjustments", true, true),
                    Triple("View Permanent Stock Ledger", true, isManager),
                    Triple("Configure Reorder Rules", true, isManager)
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    permissions.forEach { (perm, managerHas, currentHas) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(ClaySurfaceAlt.copy(alpha = 0.4f))
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = perm,
                                style = TextStyle(color = ForestInk, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (currentHas) Icons.Default.CheckCircle else Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = if (currentHas) JewelTeal else JewelRust,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (currentHas) "Authorized" else "Restricted",
                                    style = TextStyle(
                                        color = if (currentHas) JewelTeal else JewelRust,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
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
