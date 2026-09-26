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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.window.Dialog
import com.example.ui.components.AppHeader
import com.example.ui.components.ClayButton
import com.example.ui.components.ClayCategoryTag
import com.example.ui.components.ClaySearchField
import com.example.ui.theme.ClaySurface
import com.example.ui.theme.ClaySurfaceAlt
import com.example.ui.theme.ForestInk
import com.example.ui.theme.JewelBrass
import com.example.ui.theme.JewelTeal
import com.example.ui.theme.MutedSageGrey
import com.example.ui.theme.clayCard
import com.example.ui.viewmodel.InventoryViewModel

@Composable
fun WarehousesScreen(
    viewModel: InventoryViewModel,
    onMenuClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val warehouses by viewModel.warehouses.collectAsState()
    val locations by viewModel.locations.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showAddWarehouse by remember { mutableStateOf(false) }
    var showAddLocationForWh by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp)
    ) {
        AppHeader(
            title = "Warehouses",
            subtitle = "Physical facilities, zones, and storage racks",
            searchQuery = searchQuery,
            onSearchChange = { searchQuery = it },
            onNewOperationClick = {
                if (currentUser?.role == "INVENTORY_MANAGER") {
                    showAddWarehouse = true
                } else {
                    viewModel.showToast("You do not have permission to perform this action.")
                }
            },
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
                        text = "Warehouse Locations (${warehouses.size})",
                        style = TextStyle(color = ForestInk, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    )
                    ClayButton(
                        text = "Add Warehouse",
                        icon = Icons.Default.Add,
                        onClick = {
                            if (currentUser?.role == "INVENTORY_MANAGER") {
                                showAddWarehouse = true
                            } else {
                                viewModel.showToast("You do not have permission to perform this action.")
                            }
                        },
                        isPrimary = true,
                        cornerRadius = 14.dp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    warehouses.forEach { wh ->
                        val whLocations = locations.filter { it.warehouseId == wh.id }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(ClaySurfaceAlt.copy(alpha = 0.5f))
                                .padding(16.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
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
                                                text = "${wh.name} (${wh.code})",
                                                style = TextStyle(color = ForestInk, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                            )
                                            Text(
                                                text = wh.address.ifBlank { "Primary Industrial Facility" },
                                                style = TextStyle(color = MutedSageGrey, fontSize = 12.sp)
                                            )
                                        }
                                    }

                                    ClayButton(
                                        text = "+ Add Location",
                                        onClick = {
                                            if (currentUser?.role == "INVENTORY_MANAGER") {
                                                showAddLocationForWh = wh.id
                                            } else {
                                                viewModel.showToast("You do not have permission to perform this action.")
                                            }
                                        },
                                        isPrimary = false,
                                        cornerRadius = 12.dp
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "DESIGNATED STORAGE LOCATIONS (${whLocations.size})",
                                    style = TextStyle(color = MutedSageGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    whLocations.forEach { loc ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(Color.White.copy(alpha = 0.7f))
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Layers,
                                                    contentDescription = null,
                                                    tint = JewelBrass,
                                                    modifier = Modifier.padding(end = 4.dp)
                                                )
                                                Text(
                                                    text = "${loc.name} (${loc.type})",
                                                    style = TextStyle(color = ForestInk, fontSize = 12.sp, fontWeight = FontWeight.Medium)
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
    }

    // Add Warehouse Dialog
    if (showAddWarehouse) {
        var name by remember { mutableStateOf("") }
        var code by remember { mutableStateOf("") }
        var address by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { showAddWarehouse = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clayCard(cornerRadius = 24.dp, surfaceColor = ClaySurface, elevation = 10.dp)
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Add Warehouse", style = TextStyle(color = ForestInk, fontSize = 18.sp, fontWeight = FontWeight.Bold))
                        IconButton(onClick = { showAddWarehouse = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = MutedSageGrey)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    ClaySearchField(value = name, onValueChange = { name = it }, placeholder = "Warehouse Name (e.g. West Distribution)")
                    Spacer(modifier = Modifier.height(8.dp))
                    ClaySearchField(value = code, onValueChange = { code = it }, placeholder = "Code (e.g. WH-WEST)")
                    Spacer(modifier = Modifier.height(8.dp))
                    ClaySearchField(value = address, onValueChange = { address = it }, placeholder = "Facility Address")
                    Spacer(modifier = Modifier.height(16.dp))

                    ClayButton(
                        text = "Create Warehouse",
                        onClick = {
                            if (name.isNotBlank() && code.isNotBlank()) {
                                viewModel.createWarehouse(name, code, address) {
                                    showAddWarehouse = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }

    // Add Location Dialog
    if (showAddLocationForWh != null) {
        var name by remember { mutableStateOf("") }
        var code by remember { mutableStateOf("") }
        var type by remember { mutableStateOf("RACK") }

        Dialog(onDismissRequest = { showAddLocationForWh = null }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clayCard(cornerRadius = 24.dp, surfaceColor = ClaySurface, elevation = 10.dp)
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Add Storage Location", style = TextStyle(color = ForestInk, fontSize = 18.sp, fontWeight = FontWeight.Bold))
                        IconButton(onClick = { showAddLocationForWh = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = MutedSageGrey)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    ClaySearchField(value = name, onValueChange = { name = it }, placeholder = "Location Name (e.g. Rack C, Assembly Bay)")
                    Spacer(modifier = Modifier.height(8.dp))
                    ClaySearchField(value = code, onValueChange = { code = it }, placeholder = "Code (e.g. RACK-C)")
                    Spacer(modifier = Modifier.height(8.dp))
                    ClaySearchField(value = type, onValueChange = { type = it }, placeholder = "Type (RACK, FLOOR, DISPATCH)")
                    Spacer(modifier = Modifier.height(16.dp))

                    ClayButton(
                        text = "Add Location",
                        onClick = {
                            if (name.isNotBlank() && code.isNotBlank()) {
                                viewModel.createLocation(showAddLocationForWh!!, name, code, type) {
                                    showAddLocationForWh = null
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
