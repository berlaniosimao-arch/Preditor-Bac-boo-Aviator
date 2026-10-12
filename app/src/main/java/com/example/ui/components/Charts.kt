package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AviatorRoundEntity
import com.example.data.model.BacBoRoundEntity
import com.example.data.model.BacBoWinner
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardSurfaceElevated
import com.example.ui.theme.DragonRed
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TieGreen
import com.example.ui.theme.TigerBlue

@Composable
fun BacBoDistributionBarChart(
    dragonPct: Double,
    tigerPct: Double,
    tiePct: Double,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Distribuição de Frequências", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text("Obs. vs Teórico (44.6% / 44.6% / 10.8%)", fontSize = 11.sp, color = TextMuted)
        }
        Spacer(modifier = Modifier.height(10.dp))

        // Progress split bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .clip(RoundedCornerShape(6.dp))
        ) {
            val dWeight = (dragonPct.coerceAtLeast(1.0)).toFloat()
            val tWeight = (tigerPct.coerceAtLeast(1.0)).toFloat()
            val tieWeight = (tiePct.coerceAtLeast(1.0)).toFloat()

            Box(
                modifier = Modifier
                    .weight(dWeight)
                    .height(24.dp)
                    .background(DragonRed),
                contentAlignment = Alignment.Center
            ) {
                if (dragonPct > 15) {
                    Text(
                        text = "D %.0f%%".format(dragonPct),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            Box(
                modifier = Modifier
                    .weight(tieWeight)
                    .height(24.dp)
                    .background(TieGreen),
                contentAlignment = Alignment.Center
            ) {
                if (tiePct > 8) {
                    Text(
                        text = "E %.0f%%".format(tiePct),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            Box(
                modifier = Modifier
                    .weight(tWeight)
                    .height(24.dp)
                    .background(TigerBlue),
                contentAlignment = Alignment.Center
            ) {
                if (tigerPct > 15) {
                    Text(
                        text = "T %.0f%%".format(tigerPct),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        // Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            LegendItem(color = DragonRed, label = "Dragon: %.1f%%".format(dragonPct))
            LegendItem(color = TieGreen, label = "Empate: %.1f%%".format(tiePct))
            LegendItem(color = TigerBlue, label = "Tiger: %.1f%%".format(tigerPct))
        }
    }
}

@Composable
fun BacBoBeadRoad(
    rounds: List<BacBoRoundEntity>,
    modifier: Modifier = Modifier
) {
    // Show last 36 rounds in a scrollable bead road grid (rows of 6)
    val displayRounds = rounds.take(48).reversed() // chronological left to right
    val scrollState = rememberScrollState()

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Roadmap Oficial (Bead Road)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text("${displayRounds.size} rondas recentes", fontSize = 11.sp, color = TextMuted)
        }
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(CardSurfaceElevated)
                .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Group into columns of 6
                val columns = displayRounds.chunked(6)
                for (col in columns) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        for (r in col) {
                            val (bgColor, letter) = when (r.winner) {
                                BacBoWinner.DRAGON.name -> Pair(DragonRed, "D")
                                BacBoWinner.TIGER.name -> Pair(TigerBlue, "T")
                                else -> Pair(TieGreen, "E")
                            }
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(bgColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = letter,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AviatorBracketDistribution(
    pctBelow150: Double,
    pct150To200: Double,
    pct200To500: Double,
    pctAbove500: Double,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text("Distribuição por Faixas de Multiplicador", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        Spacer(modifier = Modifier.height(10.dp))

        BracketBar(label = "< 1,50x (Crash Rápido)", pct = pctBelow150, color = Color(0xFFEF4444))
        Spacer(modifier = Modifier.height(6.dp))
        BracketBar(label = "1,50x - 2,00x (Conservador)", pct = pct150To200, color = Color(0xFFF59E0B))
        Spacer(modifier = Modifier.height(6.dp))
        BracketBar(label = "2,00x - 5,00x (Moderado)", pct = pct200To500, color = Color(0xFF3B82F6))
        Spacer(modifier = Modifier.height(6.dp))
        BracketBar(label = "> 5,00x (Vela Alta)", pct = pctAbove500, color = Color(0xFF10B981))
    }
}

@Composable
private fun BracketBar(label: String, pct: Double, color: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 11.sp, color = TextSecondary)
            Text("%.1f%%".format(pct), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(CardSurfaceElevated)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth((pct / 100.0).toFloat().coerceIn(0f, 1f))
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
            )
        }
    }
}

@Composable
fun AviatorMultiplierSparkline(
    rounds: List<AviatorRoundEntity>,
    modifier: Modifier = Modifier
) {
    val recent = rounds.take(20).reversed()
    if (recent.size < 2) return

    val values = recent.map { it.multiplier.toFloat() }
    val maxVal = (values.maxOrNull() ?: 10f).coerceAtLeast(5f).coerceAtMost(30f)

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Trajetória Recente (${recent.size} rondas)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text("Linha Vermelha = 2.00x", fontSize = 10.sp, color = TextMuted)
        }
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(CardSurfaceElevated)
                .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxWidth().height(84.dp)) {
                val w = size.width
                val h = size.height

                // Draw 2.00x reference line
                val y2 = h - ((2.0f / maxVal) * h).coerceIn(0f, h)
                drawLine(
                    color = Color(0x66EF4444),
                    start = Offset(0f, y2),
                    end = Offset(w, y2),
                    strokeWidth = 2f
                )

                // Draw path connecting multipliers
                val stepX = w / (values.size - 1)
                val path = Path()

                values.forEachIndexed { index, mult ->
                    val x = index * stepX
                    val y = h - ((mult.coerceAtMost(maxVal) / maxVal) * h)
                    if (index == 0) {
                        path.moveTo(x, y)
                    } else {
                        path.lineTo(x, y)
                    }

                    // Dot for each multiplier
                    val dotColor = when {
                        mult < 1.50 -> Color(0xFFEF4444)
                        mult < 2.00 -> Color(0xFFF59E0B)
                        mult <= 5.00 -> Color(0xFF3B82F6)
                        else -> Color(0xFF10B981)
                    }
                    drawCircle(color = dotColor, radius = 4f, center = Offset(x, y))
                }

                drawPath(path = path, color = ElectricBlue, style = Stroke(width = 3f))
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, fontSize = 11.sp, color = TextSecondary)
    }
}
