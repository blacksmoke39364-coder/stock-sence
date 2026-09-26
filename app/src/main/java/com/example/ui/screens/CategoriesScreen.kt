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
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
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
import com.example.ui.theme.JewelTeal
import com.example.ui.theme.MutedSageGrey
import com.example.ui.theme.clayCard
import com.example.ui.viewmodel.InventoryViewModel

@Composable
fun CategoriesScreen(
    viewModel: InventoryViewModel,
    onMenuClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val categories by viewModel.categories.collectAsState()
    val products by viewModel.products.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showAddCategory by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp)
    ) {
        AppHeader(
            title = "Categories",
            subtitle = "Inventory taxonomy and grouping classifications",
            searchQuery = searchQuery,
            onSearchChange = { searchQuery = it },
            onNewOperationClick = {
                if (currentUser?.role == "INVENTORY_MANAGER") {
                    showAddCategory = true
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
                        text = "Classification Categories (${categories.size})",
                        style = TextStyle(color = ForestInk, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    )
                    ClayButton(
                        text = "Add Category",
                        icon = Icons.Default.Add,
                        onClick = {
                            if (currentUser?.role == "INVENTORY_MANAGER") {
                                showAddCategory = true
                            } else {
                                viewModel.showToast("You do not have permission to perform this action.")
                            }
                        },
                        isPrimary = true,
                        cornerRadius = 14.dp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    categories.forEach { cat ->
                        val count = products.count { it.product.categoryId == cat.id }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(ClaySurfaceAlt.copy(alpha = 0.5f))
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Category,
                                        contentDescription = null,
                                        tint = JewelTeal,
                                        modifier = Modifier.padding(end = 10.dp)
                                    )
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = cat.name,
                                                style = TextStyle(color = ForestInk, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                            )
                                            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                                            ClayCategoryTag(name = cat.code)
                                        }
                                        Text(
                                            text = cat.description.ifBlank { "Standard inventory group" },
                                            style = TextStyle(color = MutedSageGrey, fontSize = 12.sp)
                                        )
                                    }
                                }

                                Text(
                                    text = "$count SKUs",
                                    style = TextStyle(color = ForestInk, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddCategory) {
        var name by remember { mutableStateOf("") }
        var code by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { showAddCategory = false }) {
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
                        Text("New Category", style = TextStyle(color = ForestInk, fontSize = 18.sp, fontWeight = FontWeight.Bold))
                        IconButton(onClick = { showAddCategory = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = MutedSageGrey)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    ClaySearchField(value = name, onValueChange = { name = it }, placeholder = "Category Name (e.g. Fasteners)")
                    Spacer(modifier = Modifier.height(8.dp))
                    ClaySearchField(value = code, onValueChange = { code = it }, placeholder = "Code (e.g. FST)")
                    Spacer(modifier = Modifier.height(8.dp))
                    ClaySearchField(value = description, onValueChange = { description = it }, placeholder = "Description")
                    Spacer(modifier = Modifier.height(16.dp))

                    ClayButton(
                        text = "Save Category",
                        onClick = {
                            if (name.isNotBlank() && code.isNotBlank()) {
                                viewModel.createCategory(name, code, description) {
                                    showAddCategory = false
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
