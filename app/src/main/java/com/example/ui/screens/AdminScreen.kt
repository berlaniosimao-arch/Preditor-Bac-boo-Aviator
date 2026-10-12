package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.data.model.AdminAuditLogEntity
import com.example.data.model.PlanType
import com.example.data.model.SystemConfigEntity
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.data.repository.AdminDashboardMetrics
import com.example.data.repository.PremiumDurationOption
import com.example.ui.components.AppTopBar
import com.example.ui.components.MetricStatBox
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
fun AdminScreen(
    currentUser: UserEntity,
    metrics: AdminDashboardMetrics?,
    users: List<UserEntity>,
    auditLogs: List<AdminAuditLogEntity>,
    configs: List<SystemConfigEntity>,
    onSearchUsers: (String) -> Unit,
    onGrantPremium: (Long, PremiumDurationOption) -> Unit,
    onRevokePremium: (Long) -> Unit,
    onToggleBlockUser: (Long) -> Unit,
    onUpdateConfig: (String, String) -> Unit,
    onRefresh: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    var searchQuery by remember { mutableStateOf("") }
    var selectedUserForGrant by remember { mutableStateOf<UserEntity?>(null) }
    var showConfigDialog by remember { mutableStateOf(false) }

    Surface(color = BackgroundDark, modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            AppTopBar(
                title = "PAINEL ADMINISTRATIVO MASTER",
                currentUser = currentUser,
                onBackClick = onBackClick
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Metrics Overview
                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "MÉTRICAS DA PLATAFORMA (DADOS REAIS)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldLight
                            )
                            IconButton(onClick = onRefresh) {
                                Icon(Icons.Default.Refresh, contentDescription = "Atualizar", tint = ElectricBlue)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MetricStatBox(
                                title = "Total Contas",
                                value = "${metrics?.totalUsers ?: 0}",
                                color = TextPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            MetricStatBox(
                                title = "VIP Ativos",
                                value = "${metrics?.activePremiumUsers ?: 0}",
                                subtitle = "Sem limite máx.",
                                color = GoldLight,
                                modifier = Modifier.weight(1f)
                            )
                            MetricStatBox(
                                title = "Contas Free",
                                value = "${metrics?.freeUsers ?: 0}",
                                color = ElectricBlue,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MetricStatBox(
                                title = "Bloqueadas",
                                value = "${metrics?.blockedUsers ?: 0}",
                                color = StatusRed,
                                modifier = Modifier.weight(1f)
                            )
                            MetricStatBox(
                                title = "Total Análises",
                                value = "${metrics?.totalAnalyses ?: 0}",
                                color = StatusGreen,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Config Quick Access
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardSurface)
                            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val limitVal = configs.find { it.key == "FREE_DAILY_LIMIT" }?.value ?: "5"
                            Column {
                                Text("Limite Diário Versão Free", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("Atualmente configurado: $limitVal análises/dia", fontSize = 11.sp, color = TextSecondary)
                            }
                            Button(
                                onClick = { showConfigDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("btn_edit_configs")
                            ) {
                                Text("Alterar Limite", fontSize = 11.sp, color = Color.White)
                            }
                        }
                    }
                }

                // User Management Section
                item {
                    Column {
                        Text(
                            text = "GESTÃO DE UTILIZADORES",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldLight
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = {
                                searchQuery = it
                                onSearchUsers(it)
                            },
                            label = { Text("Pesquisar por Nome, E-mail ou ID") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = ElectricBlue) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_admin_search_users")
                        )
                    }
                }

                items(users) { user ->
                    AdminUserItemCard(
                        user = user,
                        dateFormat = dateFormat,
                        onGrantPremiumClick = { selectedUserForGrant = user },
                        onRevokePremiumClick = { onRevokePremium(user.id) },
                        onToggleBlockClick = { onToggleBlockUser(user.id) }
                    )
                }

                // Audit Logs Section
                item {
                    Column {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "REGISTOS DE AUDITORIA & SEGURANÇA",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldLight
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        if (auditLogs.isEmpty()) {
                            Text("Nenhuma ação administrativa registada recentemente.", fontSize = 12.sp, color = TextMuted)
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                for (log in auditLogs.take(10)) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(CardSurface)
                                            .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
                                            .padding(10.dp)
                                    ) {
                                        Column {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(text = log.action, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ElectricBlue)
                                                Text(text = dateFormat.format(Date(log.timestamp)), fontSize = 10.sp, color = TextMuted)
                                            }
                                            Text(text = log.details, fontSize = 11.sp, color = TextSecondary)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Grant Premium Dialog with 7, 30, 90, 365 days and Permanent
        if (selectedUserForGrant != null) {
            GrantPremiumDialog(
                targetUser = selectedUserForGrant!!,
                onDismiss = { selectedUserForGrant = null },
                onSelectDuration = { duration ->
                    onGrantPremium(selectedUserForGrant!!.id, duration)
                    selectedUserForGrant = null
                }
            )
        }

        // Config Dialog
        if (showConfigDialog) {
            var newLimit by remember {
                mutableStateOf(configs.find { it.key == "FREE_DAILY_LIMIT" }?.value ?: "5")
            }
            AlertDialog(
                onDismissRequest = { showConfigDialog = false },
                title = { Text("Configurar Limite Diário Free", color = TextPrimary, fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("Defina o número máximo de análises diárias permitidas para contas gratuitas:", fontSize = 12.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = newLimit,
                            onValueChange = { newLimit = it },
                            label = { Text("Análises por dia") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newLimit.isNotBlank()) {
                                onUpdateConfig("FREE_DAILY_LIMIT", newLimit)
                            }
                            showConfigDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Text("Guardar", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfigDialog = false }) {
                        Text("Cancelar", color = TextSecondary)
                    }
                },
                containerColor = CardSurface
            )
        }
    }
}

@Composable
private fun AdminUserItemCard(
    user: UserEntity,
    dateFormat: SimpleDateFormat,
    onGrantPremiumClick: () -> Unit,
    onRevokePremiumClick: () -> Unit,
    onToggleBlockClick: () -> Unit
) {
    val isPrem = user.isPremiumActive()
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (user.isBlocked) StatusRed else if (isPrem) GoldPrimary else CardBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_user_card_${user.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = user.fullName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = "${user.email} (ID: ${user.id})", fontSize = 11.sp, color = TextMuted)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (user.isBlocked) StatusRed.copy(alpha = 0.2f) else if (isPrem) GoldPrimary.copy(alpha = 0.2f) else ElectricBlue.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (user.isBlocked) "BLOQUEADO" else user.effectivePlanName(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (user.isBlocked) StatusRed else if (isPrem) GoldLight else ElectricBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Details
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Data de Registo: ${dateFormat.format(Date(user.createdAt))}", fontSize = 10.sp, color = TextSecondary)
                Text(text = "Último Acesso: ${dateFormat.format(Date(user.lastLoginAt))}", fontSize = 10.sp, color = TextSecondary)
                if (user.premiumExpiresAt != null) {
                    Text(
                        text = "Expiração VIP: ${dateFormat.format(Date(user.premiumExpiresAt))}",
                        fontSize = 10.sp,
                        color = GoldLight,
                        fontWeight = FontWeight.Medium
                    )
                } else if (user.plan == PlanType.PREMIUM_PERMANENT.name) {
                    Text(text = "Expiração VIP: Vitalício / Permanente", fontSize = 10.sp, color = GoldLight)
                }
                if (user.activatedByAdminEmail != null) {
                    Text(text = "Ativado pelo Admin: ${user.activatedByAdminEmail}", fontSize = 10.sp, color = TextMuted)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // CONCEDER PREMIUM button
                Button(
                    onClick = onGrantPremiumClick,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1.2f)
                        .testTag("btn_grant_premium_${user.id}")
                ) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "CONCEDER PREMIUM", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                if (isPrem && user.role != UserRole.ADMIN.name) {
                    Button(
                        onClick = onRevokePremiumClick,
                        colors = ButtonDefaults.buttonColors(containerColor = CardSurfaceElevated),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Retirar", fontSize = 10.sp, color = TextMuted)
                    }
                }

                if (user.role != UserRole.ADMIN.name) {
                    Button(
                        onClick = onToggleBlockClick,
                        colors = ButtonDefaults.buttonColors(containerColor = if (user.isBlocked) StatusGreen else StatusRed),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = if (user.isBlocked) Icons.Default.LockOpen else Icons.Default.Block,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = if (user.isBlocked) "Desbloquear" else "Bloquear", fontSize = 10.sp, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun GrantPremiumDialog(
    targetUser: UserEntity,
    onDismiss: () -> Unit,
    onSelectDuration: (PremiumDurationOption) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "CONCEDER PREMIUM",
                fontWeight = FontWeight.Bold,
                color = GoldLight
            )
        },
        text = {
            Column {
                Text(
                    text = "Selecione o período de acesso Premium para:",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Text(
                    text = "${targetUser.fullName} (${targetUser.email})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(14.dp))

                for (opt in PremiumDurationOption.values()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CardSurfaceElevated)
                            .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
                            .clickable { onSelectDuration(opt) }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                            .testTag("opt_duration_${opt.name}")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = opt.label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                            Icon(Icons.Default.Star, contentDescription = null, tint = GoldLight, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextSecondary)
            }
        },
        containerColor = CardSurface
    )
}
