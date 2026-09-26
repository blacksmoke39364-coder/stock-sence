package com.example.ui.screens

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ClayButton
import com.example.ui.components.ClaySearchField
import com.example.ui.components.ClaySegmentedToggle
import com.example.ui.theme.ClaySurface
import com.example.ui.theme.DustySageGround
import com.example.ui.theme.ForestInk
import com.example.ui.theme.JewelTeal
import com.example.ui.theme.MutedSageGrey
import com.example.ui.theme.clayCard
import com.example.ui.viewmodel.InventoryViewModel

@Composable
fun AuthScreen(
    viewModel: InventoryViewModel,
    modifier: Modifier = Modifier
) {
    var isSignUp by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("alex@stocksense.io") }
    var password by remember { mutableStateOf("••••••••") }
    var selectedRole by remember { mutableStateOf(0) } // 0: Manager, 1: Staff

    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .width(420.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Brand Logo & Title
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clayCard(cornerRadius = 20.dp, surfaceColor = JewelTeal, elevation = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Inventory,
                    contentDescription = "StockSense",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "STOCKSENSE",
                style = TextStyle(
                    color = ForestInk,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
            )
            Text(
                text = "Centralized Inventory Management System",
                style = TextStyle(color = MutedSageGrey, fontSize = 13.sp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Auth Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clayCard(cornerRadius = 28.dp, surfaceColor = ClaySurface, elevation = 8.dp)
                    .padding(22.dp)
            ) {
                Column {
                    ClaySegmentedToggle(
                        options = listOf("Sign In", "Create Account"),
                        selectedIndex = if (isSignUp) 1 else 0,
                        onSelect = { isSignUp = it == 1 },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "EMAIL ADDRESS",
                        style = TextStyle(color = MutedSageGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp),
                        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                    )
                    ClaySearchField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "you@company.com",
                        leadingIcon = Icons.Default.Email
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "PASSWORD",
                        style = TextStyle(color = MutedSageGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp),
                        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                    )
                    ClaySearchField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = "Enter password",
                        leadingIcon = Icons.Default.Lock
                    )

                    if (isSignUp) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "ASSIGNED ROLE",
                            style = TextStyle(color = MutedSageGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp),
                            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                        )
                        ClaySegmentedToggle(
                            options = listOf("Inventory Manager", "Warehouse Staff"),
                            selectedIndex = selectedRole,
                            onSelect = { selectedRole = it },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    ClayButton(
                        text = if (isSignUp) "Register & Launch" else "Sign In",
                        onClick = {
                            val role = if (selectedRole == 0) "INVENTORY_MANAGER" else "WAREHOUSE_STAFF"
                            viewModel.login(email, role)
                        },
                        isPrimary = true,
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 16.dp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Quick Demo Login Buttons
                    Text(
                        text = "QUICK DEMO ACCESS",
                        style = TextStyle(color = MutedSageGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp),
                        modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ClayButton(
                            text = "Manager Demo",
                            onClick = {
                                viewModel.login("alex@stocksense.io", "INVENTORY_MANAGER")
                            },
                            isPrimary = false,
                            modifier = Modifier.weight(1f),
                            cornerRadius = 12.dp
                        )
                        ClayButton(
                            text = "Staff Demo",
                            onClick = {
                                viewModel.login("sam@stocksense.io", "WAREHOUSE_STAFF")
                            },
                            isPrimary = false,
                            modifier = Modifier.weight(1f),
                            cornerRadius = 12.dp
                        )
                    }
                }
            }
        }
    }
}
