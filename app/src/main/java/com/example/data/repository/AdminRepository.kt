package com.example.data.repository

import com.example.data.db.AdminAuditDao
import com.example.data.db.AnalysisDao
import com.example.data.db.SystemConfigDao
import com.example.data.db.UserDao
import com.example.data.model.AdminAuditLogEntity
import com.example.data.model.PlanType
import com.example.data.model.SystemConfigEntity
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.util.SecurityUtils

data class AdminDashboardMetrics(
    val totalUsers: Int,
    val freeUsers: Int,
    val activePremiumUsers: Int,
    val blockedUsers: Int,
    val totalAnalyses: Int
)

enum class PremiumDurationOption(val days: Int?, val label: String) {
    DAYS_7(7, "7 Dias"),
    DAYS_30(30, "30 Dias (1 Mês)"),
    DAYS_90(90, "90 Dias (3 Meses)"),
    DAYS_365(365, "365 Dias (1 Ano)"),
    PERMANENT(null, "Permanente (Sem Expiração)")
}

class AdminRepository(
    private val userDao: UserDao,
    private val analysisDao: AnalysisDao,
    private val adminAuditDao: AdminAuditDao,
    private val systemConfigDao: SystemConfigDao
) {
    suspend fun getMetrics(): AdminDashboardMetrics {
        val total = userDao.getTotalUserCount()
        val blocked = userDao.getBlockedUserCount()
        val activePrem = userDao.getActivePremiumUserCount()
        val free = userDao.getFreeUserCount()
        val analyses = analysisDao.getTotalAnalysesCount()
        return AdminDashboardMetrics(
            totalUsers = total,
            freeUsers = free,
            activePremiumUsers = activePrem,
            blockedUsers = blocked,
            totalAnalyses = analyses
        )
    }

    suspend fun getAllUsers(): List<UserEntity> {
        return userDao.getAllUsers()
    }

    suspend fun searchUsers(query: String): List<UserEntity> {
        return if (query.isBlank()) {
            userDao.getAllUsers()
        } else {
            userDao.searchUsers(query.trim())
        }
    }

    suspend fun grantPremium(
        targetUserId: Long,
        option: PremiumDurationOption,
        adminUser: UserEntity
    ): Result<Unit> {
        if (adminUser.role != UserRole.ADMIN.name) {
            return Result.failure(SecurityException("Operação não autorizada. Apenas administradores podem conceder Premium."))
        }

        val target = userDao.getById(targetUserId)
            ?: return Result.failure(IllegalArgumentException("Utilizador não encontrado."))

        val now = System.currentTimeMillis()
        val (plan, expiresAt) = if (option == PremiumDurationOption.PERMANENT) {
            Pair(PlanType.PREMIUM_PERMANENT.name, null)
        } else {
            val daysMs = (option.days ?: 30).toLong() * 24L * 60L * 60L * 1000L
            Pair(PlanType.PREMIUM.name, now + daysMs)
        }

        userDao.updatePremiumStatus(
            userId = targetUserId,
            plan = plan,
            activatedAt = now,
            expiresAt = expiresAt,
            adminEmail = adminUser.email
        )

        adminAuditDao.insertLog(
            AdminAuditLogEntity(
                adminEmail = adminUser.email,
                action = "GRANT_PREMIUM",
                targetUserEmail = target.email,
                details = "Concedido plano $plan (${option.label}) pelo admin ${adminUser.email}"
            )
        )

        return Result.success(Unit)
    }

    suspend fun revokePremium(targetUserId: Long, adminUser: UserEntity): Result<Unit> {
        if (adminUser.role != UserRole.ADMIN.name) {
            return Result.failure(SecurityException("Operação não autorizada."))
        }

        val target = userDao.getById(targetUserId)
            ?: return Result.failure(IllegalArgumentException("Utilizador não encontrado."))

        userDao.updatePremiumStatus(
            userId = targetUserId,
            plan = PlanType.FREE.name,
            activatedAt = null,
            expiresAt = null,
            adminEmail = adminUser.email
        )

        adminAuditDao.insertLog(
            AdminAuditLogEntity(
                adminEmail = adminUser.email,
                action = "REVOKE_PREMIUM",
                targetUserEmail = target.email,
                details = "Revogado acesso Premium do utilizador ${target.email}"
            )
        )

        return Result.success(Unit)
    }

    suspend fun toggleBlockUser(targetUserId: Long, adminUser: UserEntity): Result<Boolean> {
        if (adminUser.role != UserRole.ADMIN.name) {
            return Result.failure(SecurityException("Operação não autorizada."))
        }

        val target = userDao.getById(targetUserId)
            ?: return Result.failure(IllegalArgumentException("Utilizador não encontrado."))

        if (target.role == UserRole.ADMIN.name) {
            return Result.failure(IllegalArgumentException("Não é permitido bloquear outro administrador."))
        }

        val newBlockedState = !target.isBlocked
        userDao.setBlockedStatus(targetUserId, newBlockedState)

        adminAuditDao.insertLog(
            AdminAuditLogEntity(
                adminEmail = adminUser.email,
                action = if (newBlockedState) "BLOCK_USER" else "UNBLOCK_USER",
                targetUserEmail = target.email,
                details = "Estado de bloqueio alterado para $newBlockedState"
            )
        )

        return Result.success(newBlockedState)
    }

    suspend fun createUserByAdmin(
        adminUser: UserEntity,
        fullName: String,
        email: String,
        password: String,
        role: UserRole,
        plan: PlanType,
        premiumDays: Int?
    ): Result<UserEntity> {
        if (adminUser.role != UserRole.ADMIN.name) {
            return Result.failure(SecurityException("Acesso restrito."))
        }

        val cleanEmail = email.trim().lowercase()
        if (userDao.getByEmail(cleanEmail) != null) {
            return Result.failure(IllegalArgumentException("E-mail já registado."))
        }

        val now = System.currentTimeMillis()
        val expires = if (plan == PlanType.PREMIUM && premiumDays != null) {
            now + (premiumDays.toLong() * 24L * 60L * 60L * 1000L)
        } else null

        val newUser = UserEntity(
            email = cleanEmail,
            passwordHash = SecurityUtils.hashPassword(password),
            fullName = fullName.trim(),
            role = role.name,
            plan = plan.name,
            createdAt = now,
            lastLoginAt = now,
            premiumActivatedAt = if (plan != PlanType.FREE) now else null,
            premiumExpiresAt = expires,
            activatedByAdminEmail = adminUser.email
        )

        val id = userDao.insertUser(newUser)
        val created = userDao.getById(id) ?: newUser

        adminAuditDao.insertLog(
            AdminAuditLogEntity(
                adminEmail = adminUser.email,
                action = "CREATE_USER",
                targetUserEmail = cleanEmail,
                details = "Criado utilizador ${cleanEmail} com função ${role.name} e plano ${plan.name}"
            )
        )

        return Result.success(created)
    }

    suspend fun updateConfig(key: String, value: String, adminUser: UserEntity): Result<Unit> {
        if (adminUser.role != UserRole.ADMIN.name) {
            return Result.failure(SecurityException("Acesso restrito."))
        }

        systemConfigDao.setConfig(
            SystemConfigEntity(
                key = key,
                value = value,
                description = "Atualizado pelo admin ${adminUser.email}",
                updatedAt = System.currentTimeMillis()
            )
        )

        adminAuditDao.insertLog(
            AdminAuditLogEntity(
                adminEmail = adminUser.email,
                action = "UPDATE_CONFIG",
                targetUserEmail = null,
                details = "Configuração $key alterada para $value"
            )
        )

        return Result.success(Unit)
    }

    suspend fun getConfigs(): List<SystemConfigEntity> {
        return systemConfigDao.getAllConfigs()
    }

    suspend fun getAuditLogs(limit: Int = 100): List<AdminAuditLogEntity> {
        return adminAuditDao.getRecentLogs(limit)
    }
}
