package com.pixelflow.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
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
import com.pixelflow.app.ui.theme.SurfaceContainerHigh

/**
 * Large pill button used on the Upscale screen for Scaling Factor (2x/4x/8x)
 * and Methodology (Nearest Neighbor / Bilinear / Bicubic / Lanczos).
 * Fills width of its slot (use with Row/`Modifier.weight`).
 */
@Composable
fun FactorPill(
    label: String,
    selected: Boolean,
    testId: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) PrimaryLight else SurfaceContainerHigh)
            .clickable(onClick = onClick)
            .testTag(testId),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = OnSurface
        )
    }
}
