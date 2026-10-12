package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): UserEntity?

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsersFlow(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    suspend fun getAllUsers(): List<UserEntity>

    @Query("""
        SELECT * FROM users 
        WHERE email LIKE '%' || :query || '%' 
           OR fullName LIKE '%' || :query || '%' 
           OR CAST(id AS TEXT) LIKE '%' || :query || '%'
        ORDER BY createdAt DESC
    """)
    suspend fun searchUsers(query: String): List<UserEntity>

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getTotalUserCount(): Int

    @Query("SELECT COUNT(*) FROM users WHERE isBlocked = 1")
    suspend fun getBlockedUserCount(): Int

    @Query("""
        SELECT COUNT(*) FROM users 
        WHERE role = 'USER' AND (plan = 'PREMIUM_PERMANENT' OR (plan = 'PREMIUM' AND premiumExpiresAt > :now))
    """)
    suspend fun getActivePremiumUserCount(now: Long = System.currentTimeMillis()): Int

    @Query("""
        SELECT COUNT(*) FROM users 
        WHERE role = 'USER' AND (plan = 'FREE' OR (plan = 'PREMIUM' AND (premiumExpiresAt IS NULL OR premiumExpiresAt <= :now)))
    """)
    suspend fun getFreeUserCount(now: Long = System.currentTimeMillis()): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET isBlocked = :blocked WHERE id = :userId")
    suspend fun setBlockedStatus(userId: Long, blocked: Boolean)

    @Query("""
        UPDATE users 
        SET plan = :plan, premiumActivatedAt = :activatedAt, premiumExpiresAt = :expiresAt, activatedByAdminEmail = :adminEmail 
        WHERE id = :userId
    """)
    suspend fun updatePremiumStatus(
        userId: Long,
        plan: String,
        activatedAt: Long?,
        expiresAt: Long?,
        adminEmail: String?
    )

    @Query("UPDATE users SET failedLoginAttempts = :attempts, lockedUntil = :lockedUntil WHERE id = :userId")
    suspend fun updateFailedAttempts(userId: Long, attempts: Int, lockedUntil: Long?)

    @Query("UPDATE users SET lastLoginAt = :timestamp, failedLoginAttempts = 0, lockedUntil = NULL WHERE id = :userId")
    suspend fun recordSuccessfulLogin(userId: Long, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE users SET dailyAnalysesUsed = :used, dailyAnalysesDate = :dateStr WHERE id = :userId")
    suspend fun updateDailyAnalyses(userId: Long, used: Int, dateStr: String)
}
