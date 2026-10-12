package com.example.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidade Room que armazena os resultados do jogo Bac Bo.
 * Campos definidos conforme os requisitos:
 * - id: Identificador único autogerado
 * - round_id: Identificador/número da ronda
 * - winner: Resultado vencedor ('DRAGON', 'TIGER', 'TIE')
 * - timestamp: Data/hora do registo em milissegundos
 * - casa_apostas: Casa de apostas em Angola (ex.: 'Premier Bet Angola', 'Elephant Bet')
 */
@Entity(
    tableName = "bac_bo_results",
    indices = [
        Index(value = ["casa_apostas", "timestamp"]),
        Index(value = ["round_id"])
    ]
)
data class BacBoResult(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "round_id")
    val round_id: Long,

    @ColumnInfo(name = "winner")
    val winner: String,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "casa_apostas")
    val casa_apostas: String
)
