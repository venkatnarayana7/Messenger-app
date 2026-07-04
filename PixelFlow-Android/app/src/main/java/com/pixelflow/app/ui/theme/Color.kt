package com.pixelflow.app.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Section-4 tokens. Do not add off-white substitutions.
val PrimaryLight = Color(0xFF6FCF97)   // Primary
val PrimarySecondary = Color(0xFF27AE60) // Emerald
val PrimaryDeep = Color(0xFF006D41)     // Deep emerald from DESIGN.md (headings)
val Mint = Color(0xFFDFF7E8)            // Tertiary/tint
val AppBackground = Color(0xFFFFFFFF)
val SurfaceContainer = Color(0xFFF9F9F9)
val SurfaceContainerHigh = Color(0xFFF3F3F4)
val OnSurface = Color(0xFF0F1712)
val OnSurfaceVariant = Color(0xFF3E4941)
val OutlineSoft = Color(0xFFE5E7EB)
val ErrorRed = Color(0xFFBA1A1A)
val ErrorContainer = Color(0xFFFFDAD6)

fun brandGradient(): Brush = Brush.linearGradient(
    colors = listOf(PrimaryLight, PrimarySecondary)
)
