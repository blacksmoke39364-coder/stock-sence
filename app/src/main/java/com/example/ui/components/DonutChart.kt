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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ClaySurface
import com.example.ui.theme.ForestInk
import com.example.ui.theme.JewelBrass
import com.example.ui.theme.JewelNavy
import com.example.ui.theme.JewelRust
import com.example.ui.theme.JewelTeal
import com.example.ui.theme.MutedSageGrey
import com.example.ui.theme.clayCard

data class CategorySlice(
    val name: String,
    val percentage: Int,
    val color: Color
)

@Composable
fun TrafficDonutCard(
    modifier: Modifier = Modifier
) {
    val slices = listOf(
        CategorySlice("Raw Materials", 42, JewelTeal),
        CategorySlice("Finished Goods", 27, JewelRust),
        CategorySlice("Electrical", 19, JewelBrass),
        CategorySlice("Hardware", 12, JewelNavy)
    )

    Box(
        modifier = modifier
            .clayCard(cornerRadius = 26.dp, surfaceColor = ClaySurface, elevation = 7.dp)
            .padding(18.dp)
    ) {
        Column {
            Text(
                text = "Inventory by Category",
                style = TextStyle(
                    color = ForestInk,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Category distribution",
                style = TextStyle(
                    color = MutedSageGrey,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal
                )
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Donut with center label
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(140.dp)) {
                    val strokeWidth = 22.dp.toPx()
                    var startAngle = -90f
                    val sweepSpacing = 2.5f

                    slices.forEach { slice ->
                        val sweep = (slice.percentage / 100f) * 360f - sweepSpacing
                        drawArc(
                            color = slice.color,
                            startAngle = startAngle,
                            sweepAngle = sweep,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                        startAngle += sweep + sweepSpacing
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "12.8k",
                        style = TextStyle(
                            color = ForestInk,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp
                        )
                    )
                    Text(
                        text = "units total",
                        style = TextStyle(
                            color = MutedSageGrey,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Legend rows
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                slices.forEach { slice ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(9.dp)
                                    .clip(CircleShape)
                                    .background(slice.color)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = slice.name,
                                style = TextStyle(
                                    color = ForestInk,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }

                        Text(
                            text = "${slice.percentage}%",
                            style = TextStyle(
                                color = ForestInk,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}
