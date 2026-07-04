package com.pixelflow.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Light-only per Section 4. No dynamic-color, no dark theme in v1.
private val LightScheme = lightColorScheme(
    primary = PrimarySecondary,
    onPrimary = AppBackground,
    primaryContainer = PrimaryLight,
    onPrimaryContainer = PrimaryDeep,
    secondary = PrimaryDeep,
    onSecondary = AppBackground,
    secondaryContainer = Mint,
    onSecondaryContainer = PrimaryDeep,
    tertiary = PrimaryDeep,
    background = AppBackground,
    onBackground = OnSurface,
    surface = AppBackground,
    onSurface = OnSurface,
    surfaceVariant = SurfaceContainerHigh,
    onSurfaceVariant = OnSurfaceVariant,
    outline = OutlineSoft,
    outlineVariant = OutlineSoft,
    error = ErrorRed,
    onError = AppBackground,
    errorContainer = ErrorContainer,
    onErrorContainer = ErrorRed,
)

@Composable
fun PixelFlowTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightScheme,
        typography = PixelFlowTypography,
        shapes = PixelFlowShapes,
        content = content
    )
}
