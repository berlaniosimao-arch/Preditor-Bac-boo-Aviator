package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AviatorStatsSummary
import com.example.data.model.BookmakerId
import com.example.data.model.FastEngineOutput
import com.example.data.model.UserEntity
import com.example.ui.components.AppTopBar
import com.example.ui.components.AviatorBracketDistribution
import com.example.ui.components.AviatorMultiplierSparkline
import com.example.ui.components.MetricStatBox
import com.example.ui.components.ResponsibleGamingDisclaimer
import com.example.ui.components.RiskChip
import com.example.ui.components.TrafficLightBadge
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CardSurfaceElevated
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.GoldLight
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.StatusYellow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AviatorScreen(
    currentUser: UserEntity,
    selectedBookmaker: BookmakerId,
    sampleSize: Int,
    onSelectSampleSize: (Int) -> Unit,
    stats: AviatorStatsSummary?,
    engineOutput: FastEngineOutput?,
    isAnalyzing: Boolean,
    onRunFastAnalysis: () -> Unit,
    onAddMultiplier: (Double) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Surface(color = BackgroundDark, modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            AppTopBar(
                title = "Aviator — ${selectedBookmaker.displayName}",
                currentUser = currentUser,
                onBackClick = onBackClick
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Header Action with Fast Engine Trigger
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(CardSurface)
                        .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "RADAR ANALÍTICO AVIATOR",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldLight
                                )
                                Text(
                                    text = "Distribuição de Multiplicadores",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                            Button(
                                onClick = { showAddDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = CardSurfaceElevated),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("btn_add_aviator_mult")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Registar", fontSize = 11.sp, color = ElectricBlue)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Sample Size Filter Chips (10, 25, 50, 100, 500)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val isPrem = currentUser.isPremiumActive()
                            val sampleOptions = listOf(10, 25, 50, 100, 500)
                            for (size in sampleOptions) {
                                val isLocked = size > 50 && !isPrem
                                val isSelected = sampleSize == size
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) PrimaryBlue else CardSurfaceElevated)
                                        .border(1.dp, if (isSelected) ElectricBlue else CardBorder, RoundedCornerShape(8.dp))
                                        .clickable {
                                            if (!isLocked) onSelectSampleSize(size)
                                        }
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                        .testTag("chip_aviator_sample_$size")
                                ) {
                                    Text(
                                        text = "$size Rondas" + if (isLocked) " 🔒 VIP" else "",
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else if (isLocked) TextMuted else TextSecondary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Fast Analysis Engine Trigger Button (<= 5s)
                        Button(
                            onClick = onRunFastAnalysis,
                            enabled = !isAnalyzing,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("btn_run_aviator_analysis")
                        ) {
                            if (isAnalyzing) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Processando motor de crash...", fontSize = 13.sp, color = Color.White)
                            } else {
                                Icon(Icons.Default.Bolt, contentDescription = null, tint = GoldLight)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "EXECUTAR ANÁLISE RÁPIDA (MÁX. 5s)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Engine Output
                if (engineOutput != null) {
                    TrafficLightBadge(
                        status = engineOutput.trafficLight,
                        processingTimeMs = engineOutput.processingTimeMs
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Probability Estimates Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(CardSurface)
                            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "ESTIMATIVAS DE DISPERSÃO E PROBABILIDADE",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                RiskChip(risk = engineOutput.riskIndicator)
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Nível de Confiança da Base: ${"%.1f".format(engineOutput.confidencePct)}%",
                                fontSize = 11.sp,
                                color = ElectricBlue
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            for (item in engineOutput.probabilityEstimates) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(Color(item.badgeColorHex))
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = item.label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "${"%.1f".format(item.observedPct)}%",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(item.badgeColorHex)
                                        )
                                        Text(
                                            text = "Intervalo: ${item.estimatedInterval}",
                                            fontSize = 10.sp,
                                            color = TextMuted
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = engineOutput.limitsAndUncertainties,
                                fontSize = 11.sp,
                                color = GoldLight,
                                lineHeight = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // General Stats & Cards
                if (stats != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricStatBox(
                            title = "Média Multiplicador",
                            value = "${"%.2f".format(stats.averageMultiplier)}x",
                            subtitle = "Mediana: ${"%.2f".format(stats.medianMultiplier)}x",
                            color = ElectricBlue,
                            modifier = Modifier.weight(1f)
                        )
                        MetricStatBox(
                            title = "Maior Vela",
                            value = "${"%.2f".format(stats.maxMultiplier)}x",
                            subtitle = "Na amostra atual",
                            color = GoldLight,
                            modifier = Modifier.weight(1f)
                        )
                        MetricStatBox(
                            title = "Crashes <1.50x",
                            value = "${"%.1f".format(stats.pctBelow150)}%",
                            subtitle = "${stats.countBelow150} de ${stats.sampleSize}",
                            color = StatusRed,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Bracket Distribution
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(CardSurface)
                            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        AviatorBracketDistribution(
                            pctBelow150 = stats.pctBelow150,
                            pct150To200 = stats.pct150To200,
                            pct200To500 = stats.pct200To500,
                            pctAbove500 = stats.pctAbove500
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Streak and Candle Radar Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(CardSurface)
                            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Text("RADAR DE SEQUÊNCIAS & VELAS ALTAS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Crash Rápido (<1.5x) Atual:", fontSize = 11.sp, color = TextSecondary)
                                    Text(
                                        text = "${stats.currentCrashStreak}x consecutivos",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (stats.currentCrashStreak >= 3) StatusRed else StatusYellow
                                    )
                                }
                                Column {
                                    Text("Rondas desde Vela >10x:", fontSize = 11.sp, color = TextSecondary)
                                    Text(
                                        text = "${stats.roundsSinceHighCandle} rondas",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ElectricBlue
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Sparkline
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(CardSurface)
                            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        AviatorMultiplierSparkline(rounds = stats.lastRounds)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                ResponsibleGamingDisclaimer()
            }
        }

        // Add Multiplier Dialog
        if (showAddDialog) {
            AddAviatorMultiplierDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { mult ->
                    onAddMultiplier(mult)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
private fun AddAviatorMultiplierDialog(
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var multiplierText by remember { mutableStateOf("2.15") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registar Multiplicador Aviator", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(
                    text = "Registo manual de resultados observados na ronda do Aviator.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = multiplierText,
                    onValueChange = { multiplierText = it },
                    label = { Text("Multiplicador (ex: 1.48 ou 5.20)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))
                Text("Valores Rápidos:", fontSize = 11.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (preset in listOf(1.15, 1.85, 2.50, 10.20)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(CardSurfaceElevated)
                                .border(1.dp, CardBorder, RoundedCornerShape(6.dp))
                                .clickable { multiplierText = "%.2f".format(preset) }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(text = "${preset}x", fontSize = 11.sp, color = ElectricBlue)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val m = multiplierText.replace(",", ".").toDoubleOrNull() ?: 1.50
                    onConfirm(m)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Gravar Multiplicador", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextSecondary)
            }
        },
        containerColor = CardSurface
    )
}
