package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.BacBoResult
import kotlinx.coroutines.flow.Flow

@Dao
interface BacBoResultDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(result: BacBoResult): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(results: List<BacBoResult>)

    @Query("SELECT * FROM bac_bo_results WHERE casa_apostas = :casaApostas ORDER BY round_id DESC LIMIT :limit")
    suspend fun getRecentResults(casaApostas: String, limit: Int): List<BacBoResult>

    @Query("SELECT * FROM bac_bo_results WHERE casa_apostas = :casaApostas ORDER BY round_id DESC LIMIT :limit")
    fun getRecentResultsFlow(casaApostas: String, limit: Int): Flow<List<BacBoResult>>

    @Query("SELECT COUNT(*) FROM bac_bo_results WHERE casa_apostas = :casaApostas")
    suspend fun countResults(casaApostas: String): Int

    @Query("SELECT COUNT(*) FROM bac_bo_results WHERE casa_apostas = :casaApostas AND winner = :winner")
    suspend fun countByWinner(casaApostas: String, winner: String): Int

    @Query("DELETE FROM bac_bo_results WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM bac_bo_results WHERE casa_apostas = :casaApostas")
    suspend fun deleteAllForCasa(casaApostas: String)
}
