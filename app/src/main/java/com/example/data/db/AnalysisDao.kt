package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.data.model.AnalysisHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnalysisDao {
    @Insert
    suspend fun insertAnalysis(entity: AnalysisHistoryEntity): Long

    @Query("SELECT * FROM analysis_history WHERE userId = :userId ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getUserAnalyses(userId: Long, limit: Int): List<AnalysisHistoryEntity>

    @Query("SELECT * FROM analysis_history WHERE userId = :userId ORDER BY timestamp DESC LIMIT :limit")
    fun getUserAnalysesFlow(userId: Long, limit: Int): Flow<List<AnalysisHistoryEntity>>

    @Query("SELECT * FROM analysis_history ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getAllAnalyses(limit: Int): List<AnalysisHistoryEntity>

    @Query("SELECT COUNT(*) FROM analysis_history")
    suspend fun getTotalAnalysesCount(): Int

    @Query("UPDATE analysis_history SET evaluatedActualResult = :actualResult, evaluationNote = :note WHERE id = :id")
    suspend fun updateEvaluation(id: Long, actualResult: String, note: String)
}
