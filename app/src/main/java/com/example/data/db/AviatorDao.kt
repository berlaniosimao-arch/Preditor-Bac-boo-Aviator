package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.AviatorRoundEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object para persistência local do histórico de multiplicadores do Aviator via Room.
 * Suporta consultas para as últimas 10, 25, 50, 100 e 500 rondas,
 * filtragem por faixas (<1.50x, 1.50x-2.00x, 2.00x-5.00x, >5.00x),
 * agregações estatísticas e fluxos reativos em tempo real.
 */
@Dao
interface AviatorDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRound(round: AviatorRoundEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRounds(rounds: List<AviatorRoundEntity>)

    @Query("SELECT * FROM aviator_rounds WHERE bookmaker = :bookmaker ORDER BY roundNumber DESC LIMIT :limit")
    suspend fun getRounds(bookmaker: String, limit: Int): List<AviatorRoundEntity>

    @Query("SELECT * FROM aviator_rounds WHERE bookmaker = :bookmaker ORDER BY roundNumber DESC LIMIT :limit")
    fun getRoundsFlow(bookmaker: String, limit: Int): Flow<List<AviatorRoundEntity>>

    @Query("SELECT COUNT(*) FROM aviator_rounds WHERE bookmaker = :bookmaker")
    suspend fun countRounds(bookmaker: String): Int

    @Query("SELECT MAX(roundNumber) FROM aviator_rounds WHERE bookmaker = :bookmaker")
    suspend fun getMaxRoundNumber(bookmaker: String): Long?

    @Query("SELECT AVG(multiplier) FROM (SELECT multiplier FROM aviator_rounds WHERE bookmaker = :bookmaker ORDER BY roundNumber DESC LIMIT :limit)")
    suspend fun getAverageMultiplier(bookmaker: String, limit: Int): Double?

    @Query("SELECT MAX(multiplier) FROM (SELECT multiplier FROM aviator_rounds WHERE bookmaker = :bookmaker ORDER BY roundNumber DESC LIMIT :limit)")
    suspend fun getMaxMultiplier(bookmaker: String, limit: Int): Double?

    @Query("""
        SELECT COUNT(*) FROM (
            SELECT multiplier FROM aviator_rounds 
            WHERE bookmaker = :bookmaker 
            ORDER BY roundNumber DESC 
            LIMIT :limit
        ) WHERE multiplier < :threshold
    """)
    suspend fun countBelowThreshold(bookmaker: String, threshold: Double, limit: Int): Int

    @Query("""
        SELECT COUNT(*) FROM (
            SELECT multiplier FROM aviator_rounds 
            WHERE bookmaker = :bookmaker 
            ORDER BY roundNumber DESC 
            LIMIT :limit
        ) WHERE multiplier >= :low AND multiplier < :high
    """)
    suspend fun countBetweenThresholds(bookmaker: String, low: Double, high: Double, limit: Int): Int

    @Query("""
        SELECT COUNT(*) FROM (
            SELECT multiplier FROM aviator_rounds 
            WHERE bookmaker = :bookmaker 
            ORDER BY roundNumber DESC 
            LIMIT :limit
        ) WHERE multiplier >= :threshold
    """)
    suspend fun countAboveThreshold(bookmaker: String, threshold: Double, limit: Int): Int

    @Query("DELETE FROM aviator_rounds WHERE id = :id")
    suspend fun deleteRound(id: Long)

    @Query("DELETE FROM aviator_rounds WHERE bookmaker = :bookmaker")
    suspend fun deleteAllForBookmaker(bookmaker: String)

    @Query("SELECT COUNT(*) FROM aviator_rounds")
    suspend fun countAll(): Int
}
