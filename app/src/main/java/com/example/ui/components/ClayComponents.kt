package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ClayHighlight
import com.example.ui.theme.ClayShadow
import com.example.ui.theme.ClaySunken
import com.example.ui.theme.ClaySurface
import com.example.ui.theme.ClaySurfaceAlt
import com.example.ui.theme.DustySageGround
import com.example.ui.theme.ForestInk
import com.example.ui.theme.JewelBrass
import com.example.ui.theme.JewelNavy
import com.example.ui.theme.JewelRust
import com.example.ui.theme.JewelTeal
import com.example.ui.theme.MutedSageGrey
import com.example.ui.theme.clayCard
import com.example.ui.theme.clayPill
import com.example.ui.theme.claySunken

@Composable
fun ClayCardSurface(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    surfaceColor: Color = ClaySurface,
    elevation: Dp = 7.dp,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clayCard(cornerRadius = cornerRadius, surfaceColor = surfaceColor, elevation = elevation)
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
fun ClayButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    isPrimary: Boolean = true,
    backgroundColor: Color? = null,
    contentColor: Color? = null,
    cornerRadius: Dp = 20.dp
) {
    val bg = backgroundColor ?: if (isPrimary) JewelTeal else ClaySurface
    val fg = contentColor ?: if (isPrimary) Color.White else ForestInk

    Box(
        modifier = modifier
            .clayCard(cornerRadius = cornerRadius, surfaceColor = bg, elevation = if (isPrimary) 6.dp else 4.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = fg.copy(alpha = 0.3f)),
                onClick = onClick
            )
            .padding(horizontal = 18.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = fg,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                color = fg,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            )
        }
    }
}

@Composable
fun ClaySearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "Search SKU, product, warehouse...",
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null
) {
    Box(
        modifier = modifier
            .claySunken(cornerRadius = 20.dp, sunkenColor = ClaySunken)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = "Search",
                    tint = MutedSageGrey,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Box(modifier = Modifier.weight(1f)) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = TextStyle(
                            color = MutedSageGrey,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal
                        )
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    textStyle = TextStyle(
                        color = ForestInk,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    cursorBrush = SolidColor(JewelTeal),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (value.isNotEmpty()) {
                IconButton(
                    onClick = { onValueChange("") },
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = MutedSageGrey,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ClaySegmentedToggle(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .claySunken(cornerRadius = 999.dp, sunkenColor = ClaySunken)
            .padding(3.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            options.forEachIndexed { index, option ->
                val isActive = index == selectedIndex
                Box(
                    modifier = Modifier
                        .then(
                            if (isActive) {
                                Modifier.clayPill(surfaceColor = JewelTeal, elevation = 3.dp)
                            } else {
                                Modifier.clip(CircleShape)
                            }
                        )
                        .clickable { onSelect(index) }
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = option,
                        style = TextStyle(
                            color = if (isActive) Color.White else MutedSageGrey,
                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun ClayProgressMeter(
    label: String,
    percent: Int,
    modifier: Modifier = Modifier,
    barColor: Color = JewelTeal
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = TextStyle(
                    color = MutedSageGrey,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
            )
            Text(
                text = "$percent%",
                style = TextStyle(
                    color = ForestInk,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .claySunken(cornerRadius = 999.dp, sunkenColor = ClaySunken)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = (percent / 100f).coerceIn(0f, 1f))
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(barColor)
            )
        }
    }
}

@Composable
fun ClayStatusPill(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, dotColor) = when (status.uppercase()) {
        "DONE", "ACTIVE", "IN STOCK" -> Triple(JewelTeal.copy(alpha = 0.16f), JewelTeal, JewelTeal)
        "READY", "PICKED", "PACKED" -> Triple(JewelBrass.copy(alpha = 0.18f), JewelBrass, JewelBrass)
        "DRAFT", "PAUSED", "LOW STOCK" -> Triple(JewelRust.copy(alpha = 0.16f), JewelRust, JewelRust)
        "OUT OF STOCK" -> Triple(JewelRust.copy(alpha = 0.25f), JewelRust, JewelRust)
        else -> Triple(JewelNavy.copy(alpha = 0.15f), JewelNavy, JewelNavy)
    }

    Box(
        modifier = modifier
            .clayCard(cornerRadius = 999.dp, surfaceColor = ClaySurface, elevation = 2.dp)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = status,
                style = TextStyle(
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            )
        }
    }
}

@Composable
fun ClayCategoryTag(
    name: String,
    modifier: Modifier = Modifier,
    color: Color = JewelNavy
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = name,
            style = TextStyle(
                color = color,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}
