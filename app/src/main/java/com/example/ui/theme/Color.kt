package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Core Deep Dark Theme for Angola Predict AI
val BackgroundDark = Color(0xFF090D16)
val BackgroundSurface = Color(0xFF0F172A)
val CardSurface = Color(0xFF162032)
val CardSurfaceElevated = Color(0xFF1E293B)
val CardBorder = Color(0xFF334155)

// Brand Accents
val PrimaryBlue = Color(0xFF2563EB)
val ElectricBlue = Color(0xFF38BDF8)
val DarkBlueAccent = Color(0xFF1D4ED8)

// Gold / Premium Palette
val GoldPrimary = Color(0xFFF59E0B)
val GoldLight = Color(0xFFFBBF24)
val GoldAccent = Color(0xFFFFD700)
val GoldDark = Color(0xFFB45309)

// Game & Status Colors
val DragonRed = Color(0xFFEF4444)
val TigerBlue = Color(0xFF3B82F6)
val TieGreen = Color(0xFF10B981)

val StatusGreen = Color(0xFF10B981)
val StatusYellow = Color(0xFFF59E0B)
val StatusRed = Color(0xFFEF4444)

// Text Colors
val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFF94A3B8)
val TextMuted = Color(0xFF64748B)

// Gradients
val GoldGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFFFFD700), Color(0xFFF59E0B), Color(0xFFD97706))
)

val BlueGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF38BDF8), Color(0xFF2563EB), Color(0xFF1D4ED8))
)

val DarkSurfaceGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF162032), Color(0xFF0F172A))
)
