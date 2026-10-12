package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class BacBoWinner {
    DRAGON,
    TIGER,
    TIE
}

@Entity(
    tableName = "bac_bo_rounds",
    indices = [
        Index(value = ["bookmaker", "timestamp"]),
        Index(value = ["roundNumber"])
    ]
)
data class BacBoRoundEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bookmaker: String,
    val roundNumber: Long,
    val winner: String, // DRAGON, TIGER, TIE
    val dragonTotal: Int = 0,
    val tigerTotal: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val isVerified: Boolean = true,
    val addedByUserId: Long? = null
)

data class BacBoStatsSummary(
    val sampleSize: Int,
    val dragonCount: Int,
    val tigerCount: Int,
    val tieCount: Int,
    val dragonObservedPct: Double,
    val tigerObservedPct: Double,
    val tieObservedPct: Double,
    val dragonTheoreticalPct: Double = 44.62,
    val tigerTheoreticalPct: Double = 44.62,
    val tieTheoreticalPct: Double = 10.76,
    val currentStreakWinner: BacBoWinner?,
    val currentStreakLength: Int,
    val maxDragonStreak: Int,
    val maxTigerStreak: Int,
    val maxTieStreak: Int,
    val averageDragonScore: Double,
    val averageTigerScore: Double,
    val lastRounds: List<BacBoRoundEntity>
)
