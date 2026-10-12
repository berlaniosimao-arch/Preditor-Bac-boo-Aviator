package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BacBoStatsSummary
import com.example.data.model.BacBoWinner
import com.example.data.model.BookmakerId
import com.example.data.model.FastEngineOutput
import com.example.data.model.UserEntity
import com.example.ui.components.AppTopBar
import com.example.ui.components.BacBoBeadRoad
import com.example.ui.components.BacBoDistributionBarChart
import com.example.ui.components.MetricStatBox
import com.example.ui.components.ResponsibleGamingDisclaimer
import com.example.ui.components.RiskChip
import com.example.ui.components.TrafficLightBadge
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CardSurfaceElevated
import com.example.ui.theme.DragonRed
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.GoldLight
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusYellow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TieGreen
import com.example.ui.theme.TigerBlue

@Composable
fun BacBoScreen(
    currentUser: UserEntity,
    selectedBookmaker: BookmakerId,
    sampleSize: Int,
    onSelectSampleSize: (Int) -> Unit,
    stats: BacBoStatsSummary?,
    engineOutput: FastEngineOutput?,
    isAnalyzing: Boolean,
    onRunFastAnalysis: () -> Unit,
    onAddRound: (BacBoWinner, Int, Int) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Surface(color = BackgroundDark, modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            AppTopBar(
                title = "Bac Bo — ${selectedBookmaker.displayName}",
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
                                    text = "MOTOR ESTATÍSTICO BAC BO",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldLight
                                )
                                Text(
                                    text = "Janela de Análise Histórica",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                            Button(
                                onClick = { showAddDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = CardSurfaceElevated),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("btn_add_bac_bo_round")
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
                                        .testTag("chip_sample_$size")
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
                                .testTag("btn_run_bacbo_analysis")
                        ) {
                            if (isAnalyzing) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Processando motor estatístico...", fontSize = 13.sp, color = Color.White)
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

                // Latest Engine Output (Traffic Light & Confidence)
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
                                    text = "ESTIMATIVAS PROBABILÍSTICAS EMPÍRICAS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                RiskChip(risk = engineOutput.riskIndicator)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Nível de Confiança Amostral: ${"%.1f".format(engineOutput.confidencePct)}%",
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
                            title = "Dragon",
                            value = "${stats.dragonCount} (${"%.1f".format(stats.dragonObservedPct)}%)",
                            subtitle = "Teórico: 44.6%",
                            color = DragonRed,
                            modifier = Modifier.weight(1f)
                        )
                        MetricStatBox(
                            title = "Empate (Tie)",
                            value = "${stats.tieCount} (${"%.1f".format(stats.tieObservedPct)}%)",
                            subtitle = "Teórico: 10.8%",
                            color = TieGreen,
                            modifier = Modifier.weight(1f)
                        )
                        MetricStatBox(
                            title = "Tiger",
                            value = "${stats.tigerCount} (${"%.1f".format(stats.tigerObservedPct)}%)",
                            subtitle = "Teórico: 44.6%",
                            color = TigerBlue,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Distribution Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(CardSurface)
                            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        BacBoDistributionBarChart(
                            dragonPct = stats.dragonObservedPct,
                            tigerPct = stats.tigerObservedPct,
                            tiePct = stats.tieObservedPct
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Streaks info box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(CardSurface)
                            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Text("SEQUÊNCIAS HISTÓRICAS (STREAKS)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Sequência Atual:", fontSize = 11.sp, color = TextSecondary)
                                    Text(
                                        text = if (stats.currentStreakWinner != null) "${stats.currentStreakLength}x ${stats.currentStreakWinner.name}" else "Nenhuma",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (stats.currentStreakWinner == BacBoWinner.DRAGON) DragonRed else if (stats.currentStreakWinner == BacBoWinner.TIGER) TigerBlue else TieGreen
                                    )
                                }
                                Column {
                                    Text("Maior Streak Dragon:", fontSize = 11.sp, color = TextSecondary)
                                    Text("${stats.maxDragonStreak}x consecutivos", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DragonRed)
                                }
                                Column {
                                    Text("Maior Streak Tiger:", fontSize = 11.sp, color = TextSecondary)
                                    Text("${stats.maxTigerStreak}x consecutivos", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TigerBlue)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Bead Road Visual Matrix
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(CardSurface)
                            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        BacBoBeadRoad(rounds = stats.lastRounds)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                ResponsibleGamingDisclaimer()
            }
        }

        // Add Round Dialog
        if (showAddDialog) {
            AddBacBoRoundDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { winner, dTotal, tTotal ->
                    onAddRound(winner, dTotal, tTotal)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
private fun AddBacBoRoundDialog(
    onDismiss: () -> Unit,
    onConfirm: (BacBoWinner, Int, Int) -> Unit
) {
    var winner by remember { mutableStateOf(BacBoWinner.DRAGON) }
    var dragonTotal by remember { mutableStateOf("8") }
    var tigerTotal by remember { mutableStateOf("6") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registar Nova Ronda Bac Bo", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(
                    text = "Introdução manual autorizada de dados observados na casa de apostas.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(14.dp))

                Text("Vencedor:", fontSize = 12.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WinnerSelectButton("Dragon", winner == BacBoWinner.DRAGON, DragonRed, Modifier.weight(1f)) {
                        winner = BacBoWinner.DRAGON
                    }
                    WinnerSelectButton("Empate", winner == BacBoWinner.TIE, TieGreen, Modifier.weight(1f)) {
                        winner = BacBoWinner.TIE
                    }
                    WinnerSelectButton("Tiger", winner == BacBoWinner.TIGER, TigerBlue, Modifier.weight(1f)) {
                        winner = BacBoWinner.TIGER
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = dragonTotal,
                    onValueChange = { dragonTotal = it },
                    label = { Text("Soma Dados Dragon (2-12)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = tigerTotal,
                    onValueChange = { tigerTotal = it },
                    label = { Text("Soma Dados Tiger (2-12)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val d = dragonTotal.toIntOrNull() ?: 7
                    val t = tigerTotal.toIntOrNull() ?: 7
                    onConfirm(winner, d, t)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Gravar Ronda", color = Color.White)
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

@Composable
private fun WinnerSelectButton(
    label: String,
    isSelected: Boolean,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) color else CardSurfaceElevated)
            .border(1.dp, if (isSelected) color else CardBorder, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else TextSecondary
        )
    }
}
