package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.SystemConfigEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SystemConfigDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setConfig(config: SystemConfigEntity)

    @Query("SELECT * FROM system_configs WHERE `key` = :key LIMIT 1")
    suspend fun getConfig(key: String): SystemConfigEntity?

    @Query("SELECT * FROM system_configs")
    fun getAllConfigsFlow(): Flow<List<SystemConfigEntity>>

    @Query("SELECT * FROM system_configs")
    suspend fun getAllConfigs(): List<SystemConfigEntity>
}
