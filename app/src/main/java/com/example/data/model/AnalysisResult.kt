package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class AnalysisTrafficLight(val label: String, val description: String, val colorHex: Long) {
    GREEN(
        label = "DADOS SUFICIENTES",
        description = "Existem dados adequados para apresentar a análise estatística, sem garantia de acerto.",
        colorHex = 0xFF10B981
    ),
    YELLOW(
        label = "INCERTEZA ELEVADA",
        description = "Os dados são limitados ou as estimativas apresentam elevada dispersão e volatilidade.",
        colorHex = 0xFFF59E0B
    ),
    RED(
        label = "ANÁLISE INDISPONÍVEL",
        description = "Dados insuficientes (< 10 rondas), desatualizados ou inconsistentes.",
        colorHex = 0xFFEF4444
    )
}

enum class RiskIndicator(val label: String, val colorHex: Long) {
    BAIXO("Risco Moderado/Padrão", 0xFF10B981),
    MODERADO("Risco Moderado", 0xFFF59E0B),
    ELEVADO("Risco Elevado", 0xFFF97316),
    CRITICO("Risco Crítico / Volátil", 0xFFEF4444)
}

@Entity(tableName = "analysis_history")
data class AnalysisHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val userEmail: String,
    val gameType: String, // "BAC_BO" or "AVIATOR"
    val bookmakerName: String,
    val trafficStatus: String, // "GREEN", "YELLOW", "RED"
    val statusMessage: String,
    val roundsAnalyzed: Int,
    val processingTimeMs: Long,
    val confidenceLevelPct: Double,
    val riskIndicator: String,
    val statisticalSummary: String,
    val probableEstimates: String,
    val timestamp: Long = System.currentTimeMillis(),
    val evaluatedActualResult: String? = null,
    val evaluationNote: String? = null
)

data class FastEngineOutput(
    val gameType: String,
    val bookmakerName: String,
    val timestamp: Long,
    val roundsAnalyzed: Int,
    val processingTimeMs: Long,
    val trafficLight: AnalysisTrafficLight,
    val confidencePct: Double,
    val riskIndicator: RiskIndicator,
    val primaryObservation: String,
    val probabilityEstimates: List<EstimateItem>,
    val limitsAndUncertainties: String,
    val statisticalDetails: Map<String, String>,
    val executionExceededTimeout: Boolean = false
)

data class EstimateItem(
    val label: String,
    val observedPct: Double,
    val theoreticalPct: Double?,
    val estimatedInterval: String,
    val badgeColorHex: Long
)
