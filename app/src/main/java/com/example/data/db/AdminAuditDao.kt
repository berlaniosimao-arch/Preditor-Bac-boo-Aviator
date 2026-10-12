package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.data.model.AdminAuditLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AdminAuditDao {
    @Insert
    suspend fun insertLog(log: AdminAuditLogEntity): Long

    @Query("SELECT * FROM admin_audit_logs ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentLogs(limit: Int): List<AdminAuditLogEntity>

    @Query("SELECT * FROM admin_audit_logs ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentLogsFlow(limit: Int): Flow<List<AdminAuditLogEntity>>
}
