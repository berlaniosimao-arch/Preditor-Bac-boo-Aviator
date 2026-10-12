package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.AviatorResult
import kotlinx.coroutines.flow.Flow

@Dao
interface AviatorResultDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(result: AviatorResult): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(results: List<AviatorResult>)

    @Query("SELECT * FROM aviator_results WHERE casa_apostas = :casaApostas ORDER BY round_id DESC LIMIT :limit")
    suspend fun getRecentResults(casaApostas: String, limit: Int): List<AviatorResult>

    @Query("SELECT * FROM aviator_results WHERE casa_apostas = :casaApostas ORDER BY round_id DESC LIMIT :limit")
    fun getRecentResultsFlow(casaApostas: String, limit: Int): Flow<List<AviatorResult>>

    @Query("SELECT COUNT(*) FROM aviator_results WHERE casa_apostas = :casaApostas")
    suspend fun countResults(casaApostas: String): Int

    @Query("""
        SELECT COUNT(*) FROM (
            SELECT multiplier FROM aviator_results 
            WHERE casa_apostas = :casaApostas 
            ORDER BY round_id DESC 
            LIMIT :limit
        ) WHERE multiplier < :threshold
    """)
    suspend fun countBelowThreshold(casaApostas: String, threshold: Double, limit: Int): Int

    @Query("DELETE FROM aviator_results WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM aviator_results WHERE casa_apostas = :casaApostas")
    suspend fun deleteAllForCasa(casaApostas: String)
}
