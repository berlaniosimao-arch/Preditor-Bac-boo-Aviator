package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.BacBoRoundEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object para persistência local do histórico de rondas de Bac Bo via Room.
 * Suporta consultas parametrizadas para as últimas 10, 25, 50, 100 e 500 rondas,
 * contagens de Dragon, Tiger e Tie, agregações e fluxos reativos em tempo real.
 */
@Dao
interface BacBoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRound(round: BacBoRoundEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRounds(rounds: List<BacBoRoundEntity>)

    @Query("SELECT * FROM bac_bo_rounds WHERE bookmaker = :bookmaker ORDER BY roundNumber DESC LIMIT :limit")
    suspend fun getRounds(bookmaker: String, limit: Int): List<BacBoRoundEntity>

    @Query("SELECT * FROM bac_bo_rounds WHERE bookmaker = :bookmaker ORDER BY roundNumber DESC LIMIT :limit")
    fun getRoundsFlow(bookmaker: String, limit: Int): Flow<List<BacBoRoundEntity>>

    @Query("SELECT COUNT(*) FROM bac_bo_rounds WHERE bookmaker = :bookmaker")
    suspend fun countRounds(bookmaker: String): Int

    @Query("SELECT COUNT(*) FROM bac_bo_rounds WHERE bookmaker = :bookmaker AND winner = :winner")
    suspend fun countByWinner(bookmaker: String, winner: String): Int

    @Query("SELECT MAX(roundNumber) FROM bac_bo_rounds WHERE bookmaker = :bookmaker")
    suspend fun getMaxRoundNumber(bookmaker: String): Long?

    @Query("SELECT AVG(dragonTotal) FROM (SELECT dragonTotal FROM bac_bo_rounds WHERE bookmaker = :bookmaker ORDER BY roundNumber DESC LIMIT :limit)")
    suspend fun getAverageDragonScore(bookmaker: String, limit: Int): Double?

    @Query("SELECT AVG(tigerTotal) FROM (SELECT tigerTotal FROM bac_bo_rounds WHERE bookmaker = :bookmaker ORDER BY roundNumber DESC LIMIT :limit)")
    suspend fun getAverageTigerScore(bookmaker: String, limit: Int): Double?

    @Query("DELETE FROM bac_bo_rounds WHERE id = :id")
    suspend fun deleteRound(id: Long)

    @Query("DELETE FROM bac_bo_rounds WHERE bookmaker = :bookmaker")
    suspend fun deleteAllForBookmaker(bookmaker: String)

    @Query("SELECT COUNT(*) FROM bac_bo_rounds")
    suspend fun countAll(): Int
}
