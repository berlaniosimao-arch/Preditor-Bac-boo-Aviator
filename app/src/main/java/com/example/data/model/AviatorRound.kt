package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class AviatorBracket(val label: String, val rangeDesc: String, val colorHex: Long) {
    BELOW_1_50("Crash Rápido", "< 1,50x", 0xFFEF4444),
    BETWEEN_1_50_AND_2_00("Moderado", "1,50x - 2,00x", 0xFFF59E0B),
    BETWEEN_2_00_AND_5_00("Médio", "2,00x - 5,00x", 0xFF3B82F6),
    ABOVE_5_00("Vela Alta", "> 5,00x", 0xFF10B981)
}

@Entity(
    tableName = "aviator_rounds",
    indices = [
        Index(value = ["bookmaker", "timestamp"]),
        Index(value = ["roundNumber"])
    ]
)
data class AviatorRoundEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bookmaker: String,
    val roundNumber: Long,
    val multiplier: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val isVerified: Boolean = true,
    val addedByUserId: Long? = null
) {
    fun bracket(): AviatorBracket {
        return when {
            multiplier < 1.50 -> AviatorBracket.BELOW_1_50
            multiplier < 2.00 -> AviatorBracket.BETWEEN_1_50_AND_2_00
            multiplier <= 5.00 -> AviatorBracket.BETWEEN_2_00_AND_5_00
            else -> AviatorBracket.ABOVE_5_00
        }
    }
}

data class AviatorStatsSummary(
    val sampleSize: Int,
    val countBelow150: Int,
    val pctBelow150: Double,
    val count150To200: Int,
    val pct150To200: Double,
    val count200To500: Int,
    val pct200To500: Double,
    val countAbove500: Int,
    val pctAbove500: Double,
    val averageMultiplier: Double,
    val medianMultiplier: Double,
    val maxMultiplier: Double,
    val currentCrashStreak: Int, // consecutive < 1.50x
    val maxCrashStreak: Int,
    val roundsSinceHighCandle: Int, // rounds since >= 10.00x
    val lastRounds: List<AviatorRoundEntity>
)
