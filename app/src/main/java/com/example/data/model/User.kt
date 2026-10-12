package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class UserRole {
    ADMIN,
    USER
}

enum class PlanType {
    FREE,
    PREMIUM,
    PREMIUM_PERMANENT
}

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val email: String,
    val passwordHash: String,
    val fullName: String,
    val phoneNumber: String? = null,
    val role: String = UserRole.USER.name,
    val plan: String = PlanType.FREE.name,
    val isBlocked: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = System.currentTimeMillis(),
    val premiumActivatedAt: Long? = null,
    val premiumExpiresAt: Long? = null,
    val activatedByAdminEmail: String? = null,
    val failedLoginAttempts: Int = 0,
    val lockedUntil: Long? = null,
    val dailyAnalysesUsed: Int = 0,
    val dailyAnalysesDate: String = ""
) {
    fun isPremiumActive(): Boolean {
        if (role == UserRole.ADMIN.name) return true
        if (plan == PlanType.PREMIUM_PERMANENT.name) return true
        if (plan == PlanType.PREMIUM.name) {
            val exp = premiumExpiresAt ?: return false
            return exp > System.currentTimeMillis()
        }
        return false
    }

    fun effectivePlanName(): String {
        return if (role == UserRole.ADMIN.name) {
            "ADMINISTRADOR MASTER"
        } else if (isPremiumActive()) {
            if (plan == PlanType.PREMIUM_PERMANENT.name) "PREMIUM PERMANENTE" else "PREMIUM"
        } else {
            "GRATUITO (FREE)"
        }
    }
}
