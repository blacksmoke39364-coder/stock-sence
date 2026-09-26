package com.example.ui.theme

import android.graphics.BlurMaskFilter
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Custom Claymorphism Modifier for raised cards, tiles, and buttons.
 * Renders dual soft shadows:
 * - Soft dark bottom-right shadow (rgba(88,104,84,.45))
 * - Soft light top-left highlight (rgba(255,255,255,.95))
 * Combined with a gentle diffuse linear gradient across the surface.
 */
fun Modifier.clayCard(
    cornerRadius: Dp = 24.dp,
    surfaceColor: Color = ClaySurface,
    elevation: Dp = 8.dp,
    onClick: (() -> Unit)? = null
): Modifier = this
    .drawBehind {
        val radiusPx = cornerRadius.toPx()
        val blurPx = elevation.toPx() * 1.5f
        val darkOffset = Offset(elevation.toPx() * 0.7f, elevation.toPx() * 0.8f)
        val lightOffset = Offset(-elevation.toPx() * 0.5f, -elevation.toPx() * 0.6f)

        drawIntoCanvas { canvas ->
            // Dark bottom-right shadow
            val darkPaint = Paint().asFrameworkPaint().apply {
                color = ClayShadow.toArgb()
                isAntiAlias = true
                maskFilter = BlurMaskFilter(blurPx, BlurMaskFilter.Blur.NORMAL)
            }
            canvas.nativeCanvas.drawRoundRect(
                darkOffset.x,
                darkOffset.y,
                size.width + darkOffset.x,
                size.height + darkOffset.y,
                radiusPx,
                radiusPx,
                darkPaint
            )

            // Light top-left highlight
            val lightPaint = Paint().asFrameworkPaint().apply {
                color = ClayHighlight.copy(alpha = 0.85f).toArgb()
                isAntiAlias = true
                maskFilter = BlurMaskFilter(blurPx * 0.8f, BlurMaskFilter.Blur.NORMAL)
            }
            canvas.nativeCanvas.drawRoundRect(
                lightOffset.x,
                lightOffset.y,
                size.width + lightOffset.x,
                size.height + lightOffset.y,
                radiusPx,
                radiusPx,
                lightPaint
            )
        }
    }
    .clip(RoundedCornerShape(cornerRadius))
    .background(
        brush = Brush.linearGradient(
            colors = listOf(
                surfaceColor.copy(alpha = 0.98f),
                surfaceColor,
                surfaceColor.copy(red = surfaceColor.red * 0.97f, green = surfaceColor.green * 0.97f, blue = surfaceColor.blue * 0.97f)
            ),
            start = Offset(0f, 0f),
            end = Offset(400f, 600f)
        )
    )
    .border(
        width = 1.dp,
        brush = Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.6f),
                Color.Transparent,
                ClayShadow.copy(alpha = 0.15f)
            ),
            start = Offset(0f, 0f),
            end = Offset(200f, 400f)
        ),
        shape = RoundedCornerShape(cornerRadius)
    )

/**
 * Sunken / pressed-in clay modifier for search fields, progress meters, toggles, and headers.
 * Simulates inner recession.
 */
fun Modifier.claySunken(
    cornerRadius: Dp = 16.dp,
    sunkenColor: Color = ClaySunken
): Modifier = this
    .clip(RoundedCornerShape(cornerRadius))
    .background(sunkenColor)
    .drawBehind {
        val radiusPx = cornerRadius.toPx()
        val innerDark = Color(0x38586854)
        val innerLight = Color(0x90FFFFFF)

        // Subtle top and left inner shadow border
        drawRoundRect(
            color = innerDark,
            topLeft = Offset(0f, 0f),
            size = size,
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(radiusPx, radiusPx),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5f)
        )
        // Subtle bottom highlight
        drawRoundRect(
            color = innerLight,
            topLeft = Offset(1.5f, 2f),
            size = size,
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(radiusPx, radiusPx),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.2f)
        )
    }

/**
 * Full-round clay pill modifier.
 */
fun Modifier.clayPill(
    surfaceColor: Color = ClaySurface,
    elevation: Dp = 4.dp
): Modifier = this.clayCard(
    cornerRadius = 999.dp,
    surfaceColor = surfaceColor,
    elevation = elevation
)
