package com.example.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidade Room que armazena os resultados do jogo Aviator.
 * Campos definidos conforme os requisitos:
 * - id: Identificador único autogerado
 * - round_id: Identificador/número da ronda
 * - multiplier: Multiplicador onde ocorreu o crash (ex.: 1.45, 2.80, 15.20)
 * - timestamp: Data/hora do registo em milissegundos
 * - casa_apostas: Casa de apostas em Angola (ex.: 'Premier Bet Angola', 'Elephant Bet')
 */
@Entity(
    tableName = "aviator_results",
    indices = [
        Index(value = ["casa_apostas", "timestamp"]),
        Index(value = ["round_id"])
    ]
)
data class AviatorResult(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "round_id")
    val round_id: Long,

    @ColumnInfo(name = "multiplier")
    val multiplier: Double,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "casa_apostas")
    val casa_apostas: String
)
