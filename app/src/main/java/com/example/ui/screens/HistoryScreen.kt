package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AnalysisHistoryEntity
import com.example.data.model.UserEntity
import com.example.ui.components.AppTopBar
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CardSurfaceElevated
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.GoldGradient
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.StatusYellow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    currentUser: UserEntity,
    analyses: List<AnalysisHistoryEntity>,
    onBackClick: () -> Unit,
    onNavigateToPremium: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
    val isPrem = currentUser.isPremiumActive()

    Surface(color = BackgroundDark, modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            AppTopBar(
                title = "Histórico de Análises & Avaliação",
                currentUser = currentUser,
                onBackClick = onBackClick
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    // Export & Overview Banner
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
                                        text = "REGISTOS DE ESTIMATIVAS",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldLight
                                    )
                                    Text(
                                        text = "Total: ${analyses.size} análises gravadas",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }

                                if (isPrem) {
                                    Button(
                                        onClick = {
                                            val reportText = buildExportReport(analyses)
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = ClipData.newPlainText("Relatório Angola Predict AI", reportText)
                                            clipboard.setPrimaryClip(clip)
                                            Toast.makeText(context, "Relatório exportado e copiado para a área de transferência!", Toast.LENGTH_LONG).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.testTag("btn_export_history")
                                    ) {
                                        Icon(Icons.Default.Download, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Exportar CSV", fontSize = 11.sp, color = Color.White)
                                    }
                                } else {
                                    Button(
                                        onClick = onNavigateToPremium,
                                        colors = ButtonDefaults.buttonColors(containerColor = CardSurfaceElevated),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.Lock, contentDescription = null, tint = GoldLight, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Exportar (VIP)", fontSize = 11.sp, color = GoldLight)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Todas as estimativas são auditáveis e associadas a cada ronda para medição contínua da incerteza.",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }

                if (analyses.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.History, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Ainda não executou nenhuma análise.", color = TextMuted, fontSize = 14.sp)
                                Text("Execute uma análise no Bac Bo ou Aviator para ver o histórico.", color = TextMuted, fontSize = 12.sp)
                            }
                        }
                    }
                }

                items(analyses) { item ->
                    HistoryItemCard(item = item, dateFormat = dateFormat)
                }
            }
        }
    }
}

@Composable
private fun HistoryItemCard(
    item: AnalysisHistoryEntity,
    dateFormat: SimpleDateFormat
) {
    val statusColor = when (item.trafficStatus) {
        "GREEN" -> StatusGreen
        "YELLOW" -> StatusYellow
        else -> StatusRed
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(statusColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${item.gameType} • ${item.bookmakerName}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                }
                Text(
                    text = "${item.processingTimeMs}ms",
                    fontSize = 11.sp,
                    color = ElectricBlue,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = dateFormat.format(Date(item.timestamp)),
                fontSize = 10.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.statisticalSummary,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(CardSurfaceElevated)
                    .padding(8.dp)
            ) {
                Column {
                    Text(text = "Estimativas Gravadas:", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                    Text(text = item.probableEstimates, fontSize = 11.sp, color = ElectricBlue)
                }
            }
        }
    }
}

private fun buildExportReport(list: List<AnalysisHistoryEntity>): String {
    val sb = StringBuilder()
    sb.append("DATA_HORA,JOGO,CASA_APOSTAS,ESTADO,TEMPO_MS,CONFIANCA,RISCO,ESTIMATIVAS\n")
    val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
    for (i in list) {
        sb.append("${df.format(Date(i.timestamp))},")
        sb.append("${i.gameType},")
        sb.append("${i.bookmakerName},")
        sb.append("${i.trafficStatus},")
        sb.append("${i.processingTimeMs},")
        sb.append("${i.confidenceLevelPct}%,")
        sb.append("${i.riskIndicator},")
        sb.append("\"${i.probableEstimates.replace("\"", "'")}\"\n")
    }
    return sb.toString()
}
