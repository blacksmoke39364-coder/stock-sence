package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ClaySurface
import com.example.ui.theme.ForestInk
import com.example.ui.theme.JewelRust
import com.example.ui.theme.JewelTeal
import com.example.ui.theme.MutedSageGrey
import com.example.ui.theme.clayCard

@Composable
fun AppHeader(
    title: String,
    subtitle: String,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onNewOperationClick: () -> Unit,
    onMenuClick: (() -> Unit)? = null,
    onNotificationsClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Title + Subtitle (+ Hamburger on mobile)
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (onMenuClick != null) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clayCard(cornerRadius = 12.dp, surfaceColor = ClaySurface, elevation = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = onMenuClick) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Open Navigation",
                            tint = ForestInk,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
            }

            Column {
                Text(
                    text = title,
                    style = TextStyle(
                        color = ForestInk,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    )
                )
                Text(
                    text = subtitle,
                    style = TextStyle(
                        color = MutedSageGrey,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    )
                )
            }
        }

        // Right: Search + Notification Bell + Action Button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Sunken Search Field (compact)
            ClaySearchField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = "Search SKU / product...",
                leadingIcon = Icons.Default.Search,
                modifier = Modifier.width(220.dp)
            )

            // Notification Bell with Rust Dot
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clayCard(cornerRadius = 14.dp, surfaceColor = ClaySurface, elevation = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = { onNotificationsClick?.invoke() }) {
                    Box {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = ForestInk,
                            modifier = Modifier.size(20.dp)
                        )
                        // Rust alert notification dot
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(JewelRust)
                                .align(Alignment.TopEnd)
                        )
                    }
                }
            }

            // Teal clay action button
            ClayButton(
                text = "New Operation",
                icon = Icons.Default.Add,
                onClick = onNewOperationClick,
                isPrimary = true,
                cornerRadius = 16.dp
            )
        }
    }
}
