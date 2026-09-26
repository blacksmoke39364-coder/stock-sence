package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ClayHighlight
import com.example.ui.theme.ClayShadow
import com.example.ui.theme.ClaySurface
import com.example.ui.theme.ForestInk
import com.example.ui.theme.JewelTeal
import com.example.ui.theme.JewelTealLight
import com.example.ui.theme.MutedSageGrey
import com.example.ui.theme.clayCard

@Composable
fun RevenueAreaChartCard(
    modifier: Modifier = Modifier
) {
    var selectedRange by remember { mutableStateOf(1) } // 0: 12M, 1: 30D, 2: 7D

    Box(
        modifier = modifier
            .clayCard(cornerRadius = 26.dp, surfaceColor = ClaySurface, elevation = 7.dp)
            .padding(18.dp)
    ) {
        Column {
            // Header row with title & range toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Stock Activity & Throughput",
                        style = TextStyle(
                            color = ForestInk,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Daily volume - Sep 15 to Oct 14",
                        style = TextStyle(
                            color = MutedSageGrey,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal
                        )
                    )
                }

                ClaySegmentedToggle(
                    options = listOf("12M", "30D", "7D"),
                    selectedIndex = selectedRange,
                    onSelect = { selectedRange = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tooltip and Area Chart container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                val dataPoints = remember {
                    listOf(
                        0.25f, 0.38f, 0.30f, 0.52f, 0.45f, 0.68f, 0.82f, 0.74f, 0.88f, 0.65f, 0.78f, 0.92f
                    )
                }
                val markerIndex = 6 // Target highlighted point (Oct 7)

                // Chart Canvas
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .align(Alignment.BottomCenter)
                ) {
                    val width = size.width
                    val height = size.height
                    val bottomMargin = 20.dp.toPx()
                    val plotHeight = height - bottomMargin
                    val stepX = width / (dataPoints.size - 1)

                    // Draw horizontal grid lines
                    val gridLines = 4
                    for (i in 0..gridLines) {
                        val y = plotHeight * (i.toFloat() / gridLines)
                        drawLine(
                            color = ClayShadow.copy(alpha = 0.12f),
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f))
                        )
                    }

                    // Build area path and line path
                    val linePath = Path()
                    val areaPath = Path()

                    val points = dataPoints.mapIndexed { index, value ->
                        Offset(
                            x = index * stepX,
                            y = plotHeight - (value * plotHeight * 0.85f)
                        )
                    }

                    points.forEachIndexed { index, point ->
                        if (index == 0) {
                            linePath.moveTo(point.x, point.y)
                            areaPath.moveTo(point.x, plotHeight)
                            areaPath.lineTo(point.x, point.y)
                        } else {
                            val prev = points[index - 1]
                            val cx1 = (prev.x + point.x) / 2
                            linePath.cubicTo(cx1, prev.y, cx1, point.y, point.x, point.y)
                            areaPath.cubicTo(cx1, prev.y, cx1, point.y, point.x, point.y)
                        }
                    }

                    areaPath.lineTo(width, plotHeight)
                    areaPath.close()

                    // Draw gradient fill
                    drawPath(
                        path = areaPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                JewelTeal.copy(alpha = 0.35f),
                                JewelTeal.copy(alpha = 0.10f),
                                Color.Transparent
                            ),
                            startY = 0f,
                            endY = plotHeight
                        )
                    )

                    // Draw smooth line
                    drawPath(
                        path = linePath,
                        color = JewelTeal,
                        style = Stroke(
                            width = 3.5.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    )

                    // Draw ringed marker at highlight point
                    val markerPoint = points[markerIndex]
                    // Outer white ring
                    drawCircle(
                        color = Color.White,
                        radius = 7.dp.toPx(),
                        center = markerPoint
                    )
                    // Teal ring
                    drawCircle(
                        color = JewelTeal,
                        radius = 5.dp.toPx(),
                        center = markerPoint
                    )
                    // Inner white center
                    drawCircle(
                        color = Color.White,
                        radius = 2.dp.toPx(),
                        center = markerPoint
                    )

                    // Draw connector stem to the tooltip above
                    drawLine(
                        color = JewelTeal,
                        start = Offset(markerPoint.x, markerPoint.y - 7.dp.toPx()),
                        end = Offset(markerPoint.x, markerPoint.y - 24.dp.toPx()),
                        strokeWidth = 2f
                    )
                }

                // Floating Anchored Clay Tooltip positioned directly above marker
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(bottom = 60.dp)
                        .clayCard(cornerRadius = 12.dp, surfaceColor = Color.White, elevation = 4.dp)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Oct 7",
                            style = TextStyle(
                                color = MutedSageGrey,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(JewelTeal)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "2,420 units",
                            style = TextStyle(
                                color = ForestInk,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            // X-axis date labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("Sep 15", "Sep 22", "Sep 29", "Oct 07", "Oct 14").forEach { label ->
                    Text(
                        text = label,
                        style = TextStyle(
                            color = MutedSageGrey,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}
