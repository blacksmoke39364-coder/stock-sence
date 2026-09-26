package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.SidebarNav
import com.example.ui.screens.AdjustmentsScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DeliveriesScreen
import com.example.ui.screens.MoveHistoryScreen
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.screens.ProductsScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ReceiptsScreen
import com.example.ui.screens.ReorderRulesScreen
import com.example.ui.screens.StockLedgerScreen
import com.example.ui.screens.TransfersScreen
import com.example.ui.screens.WarehousesScreen
import com.example.ui.theme.ClaySurface
import com.example.ui.theme.DustySageGround
import com.example.ui.theme.ForestInk
import com.example.ui.theme.JewelTeal
import com.example.ui.theme.clayCard
import com.example.ui.viewmodel.InventoryViewModel
import com.example.ui.viewmodel.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun StockSenseApp(
    viewModel: InventoryViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()

    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    // Back handling for Android navigation
    BackHandler(enabled = currentScreen !is Screen.Dashboard) {
        viewModel.navigateBack()
    }

    // Auto-dismiss toast after 3 seconds
    LaunchedEffect(toastMessage) {
        if (toastMessage != null) {
            delay(3000)
            viewModel.clearToast()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DustySageGround)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        if (currentUser == null) {
            AuthScreen(viewModel = viewModel)
        } else {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val isWideScreen = maxWidth >= 840.dp

                if (isWideScreen) {
                    // Two-pane desktop / tablet layout
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        // Left 260dp clay sidebar
                        SidebarNav(
                            currentScreen = currentScreen,
                            onNavigate = { viewModel.navigateTo(it) },
                            currentUser = currentUser,
                            onSwitchRole = { viewModel.switchDemoAccount(it) },
                            onLogout = { viewModel.logout() }
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        // Flexible main workspace pane
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f)
                        ) {
                            ScreenRouter(currentScreen, viewModel, onMenuClick = null)
                        }
                    }
                } else {
                    // Adaptive mobile layout with modal sliding drawer
                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            ModalDrawerSheet(
                                drawerContainerColor = DustySageGround,
                                modifier = Modifier.width(280.dp)
                            ) {
                                SidebarNav(
                                    currentScreen = currentScreen,
                                    onNavigate = {
                                        viewModel.navigateTo(it)
                                        scope.launch { drawerState.close() }
                                    },
                                    currentUser = currentUser,
                                    onSwitchRole = {
                                        viewModel.switchDemoAccount(it)
                                        scope.launch { drawerState.close() }
                                    },
                                    onLogout = {
                                        viewModel.logout()
                                        scope.launch { drawerState.close() }
                                    }
                                )
                            }
                        }
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            ScreenRouter(
                                currentScreen = currentScreen,
                                viewModel = viewModel,
                                onMenuClick = {
                                    scope.launch { drawerState.open() }
                                }
                            )
                        }
                    }
                }
            }
        }

        // Floating Toast Notification Banner
        AnimatedVisibility(
            visible = toastMessage != null,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 18.dp)
        ) {
            toastMessage?.let { msg ->
                Box(
                    modifier = Modifier
                        .clayCard(cornerRadius = 16.dp, surfaceColor = Color.White, elevation = 8.dp)
                        .clickable { viewModel.clearToast() }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = JewelTeal,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = msg,
                            style = TextStyle(color = ForestInk, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ScreenRouter(
    currentScreen: Screen,
    viewModel: InventoryViewModel,
    onMenuClick: (() -> Unit)?
) {
    when (currentScreen) {
        is Screen.Dashboard -> DashboardScreen(viewModel = viewModel, onMenuClick = onMenuClick)
        is Screen.Products -> ProductsScreen(viewModel = viewModel, onMenuClick = onMenuClick)
        is Screen.ProductDetail -> ProductDetailScreen(productId = currentScreen.productId, viewModel = viewModel)
        is Screen.Warehouses -> WarehousesScreen(viewModel = viewModel, onMenuClick = onMenuClick)
        is Screen.Categories -> CategoriesScreen(viewModel = viewModel, onMenuClick = onMenuClick)
        is Screen.Receipts -> ReceiptsScreen(viewModel = viewModel, onMenuClick = onMenuClick)
        is Screen.Deliveries -> DeliveriesScreen(viewModel = viewModel, onMenuClick = onMenuClick)
        is Screen.Transfers -> TransfersScreen(viewModel = viewModel, onMenuClick = onMenuClick)
        is Screen.Adjustments -> AdjustmentsScreen(viewModel = viewModel, onMenuClick = onMenuClick)
        is Screen.StockLedger -> StockLedgerScreen(viewModel = viewModel, onMenuClick = onMenuClick)
        is Screen.MoveHistory -> MoveHistoryScreen(viewModel = viewModel, onMenuClick = onMenuClick)
        is Screen.ReorderRules -> ReorderRulesScreen(viewModel = viewModel, onMenuClick = onMenuClick)
        is Screen.Profile -> ProfileScreen(viewModel = viewModel, onMenuClick = onMenuClick)
    }
}
