package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "system_configs")
data class SystemConfigEntity(
    @PrimaryKey
    val key: String,
    val value: String,
    val description: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "admin_audit_logs")
data class AdminAuditLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val adminEmail: String,
    val action: String, // GRANT_PREMIUM, REVOKE_PREMIUM, BLOCK_USER, UNBLOCK_USER, CREATE_USER, UPDATE_CONFIG
    val targetUserEmail: String? = null,
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)
