package com.pixelflow.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.pixelflow.app.ui.theme.OnSurface
import com.pixelflow.app.ui.theme.PrimaryLight
import com.pixelflow.app.ui.theme.PrimarySecondary
import com.pixelflow.app.ui.theme.SurfaceContainerHigh

/**
 * Pill / chip used for presets, format options, methodology, filters.
 * Selection changes color/border only (per DEVELOPMENT_CRITICAL_RULES).
 */
@Composable
fun PixelChip(
    text: String,
    selected: Boolean,
    testId: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (selected) PrimaryLight else SurfaceContainerHigh)
            .clickable(onClick = onClick)
            .height(36.dp)
            .widthIn(min = 72.dp)
            .padding(horizontal = 16.dp)
            .testTag(testId),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) PrimarySecondary else OnSurface
        )
    }
}
