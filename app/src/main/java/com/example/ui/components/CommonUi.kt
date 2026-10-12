package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AnalysisTrafficLight
import com.example.data.model.RiskIndicator
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.ui.theme.BackgroundSurface
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

@Composable
fun AppTopBar(
    title: String,
    currentUser: UserEntity? = null,
    onBackClick: (() -> Unit)? = null,
    onAdminClick: (() -> Unit)? = null,
    onPremiumClick: (() -> Unit)? = null,
    onLogoutClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        color = BackgroundSurface,
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                if (onBackClick != null) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("btn_back")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (currentUser != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val isPrem = currentUser.isPremiumActive()
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isPrem) GoldPrimary.copy(alpha = 0.2f) else ElectricBlue.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = currentUser.effectivePlanName(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isPrem) GoldLight else ElectricBlue
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = currentUser.fullName,
                                fontSize = 11.sp,
                                color = TextMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (currentUser != null && currentUser.role == UserRole.ADMIN.name && onAdminClick != null) {
                    IconButton(
                        onClick = onAdminClick,
                        modifier = Modifier.testTag("btn_top_admin")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Painel Admin",
                            tint = GoldLight
                        )
                    }
                }

                if (currentUser != null && !currentUser.isPremiumActive() && onPremiumClick != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(GoldGradient)
                            .clickable { onPremiumClick() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("btn_top_upgrade_premium")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "VIP",
                                tint = Color(0xFF1E1000),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "PREMIUM",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1E1000)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                if (currentUser != null && onLogoutClick != null) {
                    IconButton(
                        onClick = onLogoutClick,
                        modifier = Modifier.testTag("btn_top_logout")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Terminar Sessão",
                            tint = TextMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TrafficLightBadge(
    status: AnalysisTrafficLight,
    processingTimeMs: Long? = null,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (status) {
        AnalysisTrafficLight.GREEN -> Triple(StatusGreen, Color.White, "VERDE — DADOS SUFICIENTES")
        AnalysisTrafficLight.YELLOW -> Triple(StatusYellow, Color(0xFF1F1200), "AMARELO — INCERTEZA ELEVADA")
        AnalysisTrafficLight.RED -> Triple(StatusRed, Color.White, "VERMELHO — DADOS INSUFICIENTES")
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor.copy(alpha = 0.15f))
            .border(1.dp, bgColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(bgColor)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = label,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = if (status == AnalysisTrafficLight.YELLOW) GoldLight else bgColor
                )
            }
            if (processingTimeMs != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(CardSurfaceElevated)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "⏱ ${processingTimeMs}ms (Meta <5s)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = ElectricBlue
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = status.description,
            fontSize = 12.sp,
            color = TextSecondary,
            lineHeight = 16.sp
        )
    }
}

@Composable
fun RiskChip(risk: RiskIndicator, modifier: Modifier = Modifier) {
    val color = Color(risk.colorHex)
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = risk.label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    borderColor: Color = CardBorder,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        modifier = modifier.then(
            if (onClick != null) Modifier.clickable { onClick() } else Modifier
        )
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

@Composable
fun ResponsibleGamingDisclaimer(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardSurfaceElevated)
            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Aviso",
                tint = GoldLight,
                modifier = Modifier
                    .size(18.dp)
                    .padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Aviso de Transparência Estatística & Jogo Responsável",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Esta ferramenta baseia-se unicamente em frequências observadas e modelos probabilísticos. Cada ronda de Bac Bo e Aviator é estritamente independente. O sistema NÃO garante resultados futuros nem elimina o risco financeiro. Jogue com moderação (+18).",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
fun MetricStatBox(
    title: String,
    value: String,
    subtitle: String? = null,
    color: Color = ElectricBlue,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(CardSurfaceElevated)
            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(text = title, fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = subtitle, fontSize = 10.sp, color = TextSecondary)
            }
        }
    }
}
