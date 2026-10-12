package com.example.data.repository

import com.example.data.db.SystemConfigDao
import com.example.data.db.UserDao
import com.example.data.model.PlanType
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.util.SecurityUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class AuthResult {
    data class Success(val user: UserEntity) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class AuthRepository(
    private val userDao: UserDao,
    private val configDao: SystemConfigDao
) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    suspend fun login(emailInput: String, passwordInput: String): AuthResult {
        val email = emailInput.trim().lowercase()
        val user = userDao.getByEmail(email)
            ?: return AuthResult.Error("Utilizador não encontrado com este e-mail.")

        val now = System.currentTimeMillis()

        // Check if account is locked due to consecutive failures
        if (user.lockedUntil != null && user.lockedUntil > now) {
            val remainingSec = (user.lockedUntil - now) / 1000
            return AuthResult.Error("Conta temporariamente bloqueada por segurança. Tente novamente em $remainingSec segundos.")
        }

        // Check if account was administratively blocked
        if (user.isBlocked) {
            return AuthResult.Error("Esta conta foi suspensa pela administração. Contacte o suporte.")
        }

        if (!SecurityUtils.verifyPassword(passwordInput, user.passwordHash)) {
            val newAttempts = user.failedLoginAttempts + 1
            val lockTime = if (newAttempts >= 5) now + (5 * 60 * 1000L) else null
            userDao.updateFailedAttempts(user.id, newAttempts, lockTime)
            return if (newAttempts >= 5) {
                AuthResult.Error("Demasiadas tentativas incorretas. Conta bloqueada temporariamente por 5 minutos.")
            } else {
                AuthResult.Error("Palavra-passe incorreta. Tentativas restantes antes do bloqueio: ${5 - newAttempts}")
            }
        }

        // Success
        userDao.recordSuccessfulLogin(user.id, now)
        val refreshed = userDao.getById(user.id) ?: user
        return AuthResult.Success(refreshed)
    }

    suspend fun register(
        fullNameInput: String,
        emailInput: String,
        passwordInput: String,
        phoneNumberInput: String?
    ): AuthResult {
        val fullName = fullNameInput.trim()
        val email = emailInput.trim().lowercase()

        if (fullName.length < 3) {
            return AuthResult.Error("O nome completo deve ter pelo menos 3 caracteres.")
        }

        if (!email.contains("@") || !email.contains(".")) {
            return AuthResult.Error("Insira um endereço de e-mail válido.")
        }

        if (passwordInput.length < 6) {
            return AuthResult.Error("A palavra-passe deve conter pelo menos 6 caracteres.")
        }

        val existing = userDao.getByEmail(email)
        if (existing != null) {
            return AuthResult.Error("Já existe uma conta registada com este e-mail.")
        }

        val newUser = UserEntity(
            email = email,
            passwordHash = SecurityUtils.hashPassword(passwordInput),
            fullName = fullName,
            phoneNumber = phoneNumberInput?.trim(),
            role = UserRole.USER.name,
            plan = PlanType.FREE.name,
            createdAt = System.currentTimeMillis(),
            lastLoginAt = System.currentTimeMillis()
        )

        val newId = try {
            userDao.insertUser(newUser)
        } catch (e: Exception) {
            return AuthResult.Error("Erro ao criar conta: ${e.message}")
        }

        val created = userDao.getById(newId) ?: newUser
        return AuthResult.Success(created)
    }

    suspend fun requestPasswordReset(emailInput: String): Result<String> {
        val email = emailInput.trim().lowercase()
        val user = userDao.getByEmail(email)
            ?: return Result.failure(Exception("Nenhuma conta encontrada com este e-mail."))

        // Local recovery token generation
        val token = "ANGOLA-${(100000..999999).random()}"
        return Result.success("Código de recuperação gerado com sucesso: $token. Utilize-o para redefinir a sua senha no suporte.")
    }

    suspend fun checkAndConsumeDailyAnalysis(user: UserEntity): Pair<Boolean, String> {
        if (user.isPremiumActive()) {
            return Pair(true, "Acesso Premium Ilimitado")
        }

        val today = dateFormat.format(Date())
        val currentUsed = if (user.dailyAnalysesDate == today) user.dailyAnalysesUsed else 0

        val limitConfig = configDao.getConfig("FREE_DAILY_LIMIT")?.value?.toIntOrNull() ?: 5

        if (currentUsed >= limitConfig) {
            return Pair(
                false,
                "Limite diário da versão gratuita atingido ($limitConfig análises hoje). Atualize para o Premium para análises ilimitadas."
            )
        }

        val newUsed = currentUsed + 1
        userDao.updateDailyAnalyses(user.id, newUsed, today)
        val remaining = limitConfig - newUsed
        return Pair(true, "Análise gratuita processada. Restam $remaining hoje.")
    }

    suspend fun getDailyAnalysesInfo(user: UserEntity): Pair<Int, Int> {
        if (user.isPremiumActive()) {
            return Pair(0, 999999) // Unlimited
        }
        val today = dateFormat.format(Date())
        val currentUsed = if (user.dailyAnalysesDate == today) user.dailyAnalysesUsed else 0
        val limitConfig = configDao.getConfig("FREE_DAILY_LIMIT")?.value?.toIntOrNull() ?: 5
        val remaining = (limitConfig - currentUsed).coerceAtLeast(0)
        return Pair(currentUsed, remaining)
    }

    suspend fun refreshUser(userId: Long): UserEntity? {
        return userDao.getById(userId)
    }
}
