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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Store
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AviatorStatsSummary
import com.example.data.model.BacBoStatsSummary
import com.example.data.model.BookmakerId
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.ui.components.AppTopBar
import com.example.ui.components.MetricStatBox
import com.example.ui.components.ResponsibleGamingDisclaimer
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CardSurfaceElevated
import com.example.ui.theme.DragonRed
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.GoldGradient
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TigerBlue

@Composable
fun DashboardScreen(
    currentUser: UserEntity,
    selectedBookmaker: BookmakerId,
    onSelectBookmaker: (BookmakerId) -> Unit,
    bacBoStats: BacBoStatsSummary?,
    aviatorStats: AviatorStatsSummary?,
    dailyUsed: Int,
    dailyRemaining: Int,
    onNavigateToBacBo: () -> Unit,
    onNavigateToAviator: () -> Unit,
    onNavigateToBookmakers: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToPremium: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Surface(color = BackgroundDark, modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            AppTopBar(
                title = "ANGOLA PREDICT AI",
                currentUser = currentUser,
                onAdminClick = if (currentUser.role == UserRole.ADMIN.name) onNavigateToAdmin else null,
                onPremiumClick = onNavigateToPremium,
                onLogoutClick = onLogoutClick
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                // Account Plan & Limits Banner
                UserStatusCard(
                    user = currentUser,
                    dailyUsed = dailyUsed,
                    dailyRemaining = dailyRemaining,
                    onUpgradeClick = onNavigateToPremium
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Bookmaker Selector Row
                Text(
                    text = "CASA DE APOSTAS SELECIONADA",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (bm in BookmakerId.values()) {
                        val isSelected = bm == selectedBookmaker
                        val brandColor = Color(bm.brandHex)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) brandColor.copy(alpha = 0.25f) else CardSurface)
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) brandColor else CardBorder,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { onSelectBookmaker(bm) }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                                .testTag("chip_bm_${bm.name}")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(brandColor)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = bm.displayName,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) TextPrimary else TextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Module Cards (Bac Bo & Aviator)
                Text(
                    text = "MÓDULOS DE ANÁLISE DISPONÍVEIS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Bac Bo Card
                GameLaunchCard(
                    title = "BAC BO ANALYTICS",
                    subtitle = "Dados Dragon vs Tiger • Roadmaps & Probabilidades",
                    icon = Icons.Default.Casino,
                    accentColor = DragonRed,
                    stat1Label = "Rondas Analisadas",
                    stat1Value = "${bacBoStats?.sampleSize ?: 0}",
                    stat2Label = "Última Sequência",
                    stat2Value = if (bacBoStats?.currentStreakWinner != null) "${bacBoStats.currentStreakLength}x ${bacBoStats.currentStreakWinner.name}" else "Neutro",
                    stat3Label = "Dragon / Tiger",
                    stat3Value = "${"%.0f".format(bacBoStats?.dragonObservedPct ?: 0.0)}% / ${"%.0f".format(bacBoStats?.tigerObservedPct ?: 0.0)}%",
                    buttonText = "ABRIR ANÁLISE BAC BO",
                    onClick = onNavigateToBacBo,
                    testTag = "card_open_bac_bo"
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Aviator Card
                GameLaunchCard(
                    title = "AVIATOR RADAR & VELAS",
                    subtitle = "Histograma de Multiplicadores • Faixas de Risco",
                    icon = Icons.Default.FlightTakeoff,
                    accentColor = ElectricBlue,
                    stat1Label = "Multiplicadores",
                    stat1Value = "${aviatorStats?.sampleSize ?: 0}",
                    stat2Label = "Média Recente",
                    stat2Value = "${"%.2f".format(aviatorStats?.averageMultiplier ?: 0.0)}x",
                    stat3Label = "Crashes <1.50x",
                    stat3Value = "${"%.1f".format(aviatorStats?.pctBelow150 ?: 0.0)}%",
                    buttonText = "ABRIR ANÁLISE AVIATOR",
                    onClick = onNavigateToAviator,
                    testTag = "card_open_aviator"
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Quick Navigation Grid
                Text(
                    text = "NAVEGAÇÃO RÁPIDA",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickNavButton(
                        icon = Icons.Default.Store,
                        title = "Casas de Apostas",
                        subtitle = "Verificar Status",
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToBookmakers,
                        testTag = "btn_nav_bookmakers"
                    )
                    QuickNavButton(
                        icon = Icons.Default.History,
                        title = "Histórico & Desempenho",
                        subtitle = "Avaliar Previsões",
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToHistory,
                        testTag = "btn_nav_history"
                    )
                }

                if (currentUser.role == UserRole.ADMIN.name) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onNavigateToAdmin,
                        colors = ButtonDefaults.buttonColors(containerColor = CardSurfaceElevated),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, GoldPrimary, RoundedCornerShape(12.dp))
                            .testTag("btn_dash_admin_panel")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = GoldLight)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ACEDER AO PAINEL ADMINISTRATIVO MASTER",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldLight
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                ResponsibleGamingDisclaimer()
            }
        }
    }
}

@Composable
private fun UserStatusCard(
    user: UserEntity,
    dailyUsed: Int,
    dailyRemaining: Int,
    onUpgradeClick: () -> Unit
) {
    val isPrem = user.isPremiumActive()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isPrem) CardSurfaceElevated else CardSurface)
            .border(
                width = if (isPrem) 1.5.dp else 1.dp,
                color = if (isPrem) GoldPrimary else CardBorder,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "CONTA DE UTILIZADOR", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                    Text(text = user.fullName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .then(
                            if (isPrem) Modifier.background(GoldGradient)
                            else Modifier.background(ElectricBlue.copy(alpha = 0.2f))
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = user.effectivePlanName(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isPrem) Color(0xFF1E1000) else ElectricBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (!isPrem) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Limite Diário Free: $dailyUsed usadas • Restam $dailyRemaining análises",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "Reinicia diariamente às 00:00 (Fuso de Angola)",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }
                    Button(
                        onClick = onUpgradeClick,
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_upgrade_dash")
                    ) {
                        Text(text = "VIP PREMIUM", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            } else {
                Text(
                    text = "Benefício VIP Ativo: Análises ilimitadas, histórico completo de 500 rondas e exportação de relatórios autorizada.",
                    fontSize = 12.sp,
                    color = GoldLight,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun GameLaunchCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    stat1Label: String,
    stat1Value: String,
    stat2Label: String,
    stat2Value: String,
    stat3Label: String,
    stat3Value: String,
    buttonText: String,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(accentColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(text = subtitle, fontSize = 11.sp, color = TextMuted)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricStatBox(title = stat1Label, value = stat1Value, color = TextPrimary, modifier = Modifier.weight(1f))
                MetricStatBox(title = stat2Label, value = stat2Value, color = accentColor, modifier = Modifier.weight(1f))
                MetricStatBox(title = stat3Label, value = stat3Value, color = ElectricBlue, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag(testTag)
            ) {
                Text(text = buttonText, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
private fun QuickNavButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(CardSurface)
            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(14.dp)
            .testTag(testTag)
    ) {
        Column {
            Icon(imageVector = icon, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(text = subtitle, fontSize = 11.sp, color = TextMuted)
        }
    }
}
