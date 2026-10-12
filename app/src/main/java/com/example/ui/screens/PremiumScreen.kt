package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Verified
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserEntity
import com.example.ui.components.AppTopBar
import com.example.ui.components.ResponsibleGamingDisclaimer
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
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun PremiumScreen(
    currentUser: UserEntity?,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(color = BackgroundDark, modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            AppTopBar(
                title = "Plano VIP Premium",
                currentUser = currentUser,
                onBackClick = onBackClick
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Gold Header Emblem
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(GoldGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFF1E1000),
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "ANGOLA PREDICT AI VIP",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = GoldLight
                )

                Text(
                    text = "Acesso Ilimitado • Janelas até 500 Rondas • Exportação CSV",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Plan Comparison Table Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldPrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "COMPARATIVO DE PLANOS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldLight
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        ComparisonRow("Análises Diárias", "5 por dia", "Ilimitadas (Sem Teto)")
                        ComparisonRow("Janela Amostral", "Até 50 rondas", "Até 500 rondas")
                        ComparisonRow("Módulo Bac Bo", "Básico", "Avançado + Roadmaps")
                        ComparisonRow("Módulo Aviator", "Histograma Básico", "Radar + Sparkline Trajetória")
                        ComparisonRow("Exportação de Dados", "Bloqueada", "Exportação CSV/Texto")
                        ComparisonRow("Controlo de Servidor", "Sim", "Validação Rigorosa")
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // How to Activate Card (Angola payment methods & Admin contact)
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurfaceElevated),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.SupportAgent, contentDescription = null, tint = ElectricBlue)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Como Ativar o Plano VIP em Angola",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "A ativação é concedida pelo Administrador Master após confirmação ou via credenciais autorizadas. O pagamento pode ser processado localmente via Multicaixa Express, Transferência Bancária (IBAN Angolano) ou acordos comerciais.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(CardSurface)
                                .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(text = "Contacto da Administração:", fontSize = 11.sp, color = TextMuted)
                                Text(text = "admin@angolapredict.ao • Suporte Angola", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ElectricBlue)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                ResponsibleGamingDisclaimer()
            }
        }
    }
}

@Composable
private fun ComparisonRow(feature: String, freeVal: String, premVal: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = feature, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text(text = premVal, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldLight)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Versão Gratuita:", fontSize = 10.sp, color = TextMuted)
            Text(text = freeVal, fontSize = 10.sp, color = TextSecondary)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(CardBorder))
    }
}
