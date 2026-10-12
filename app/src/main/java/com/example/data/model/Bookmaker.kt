package com.example.data.model

enum class BookmakerId(
    val displayName: String,
    val subtitle: String,
    val brandHex: Long,
    val isVerifiedInAngola: Boolean,
    val supportedGames: List<String>
) {
    PREMIER_BET(
        displayName = "Premier Bet Angola",
        subtitle = "Operador Licenciado em Angola",
        brandHex = 0xFF16A34A,
        isVerifiedInAngola = true,
        supportedGames = listOf("BAC_BO", "AVIATOR")
    ),
    ELEPHANT_BET(
        displayName = "Elephant Bet",
        subtitle = "AngoBet / Elephant Bet Moç & Ang",
        brandHex = 0xFFEA580C,
        isVerifiedInAngola = true,
        supportedGames = listOf("BAC_BO", "AVIATOR")
    ),
    BANTU_BET(
        displayName = "Bantu Bet",
        subtitle = "Plataforma Nacional Angolana",
        brandHex = 0xFF2563EB,
        isVerifiedInAngola = true,
        supportedGames = listOf("BAC_BO", "AVIATOR")
    ),
    KWANZA_BET(
        displayName = "Kwanza Bet",
        subtitle = "Operador Angolano Autorizado",
        brandHex = 0xFF9333EA,
        isVerifiedInAngola = true,
        supportedGames = listOf("BAC_BO", "AVIATOR")
    ),
    EBET(
        displayName = "Ebet Angola",
        subtitle = "Jogos Virtuais e Crash",
        brandHex = 0xFF0D9488,
        isVerifiedInAngola = true,
        supportedGames = listOf("BAC_BO", "AVIATOR")
    )
}

data class BookmakerStatusInfo(
    val bookmakerId: BookmakerId,
    val connectionType: String = "Entrada Manual Autorizada & Amostras Oficiais",
    val statusText: String = "Operacional e Compatível",
    val totalRoundsBacBo: Int = 0,
    val totalRoundsAviator: Int = 0,
    val isAvailable: Boolean = true
)
