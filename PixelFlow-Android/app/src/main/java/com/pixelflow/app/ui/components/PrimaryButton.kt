package com.pixelflow.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.pixelflow.app.ui.theme.PrimaryDeep
import com.pixelflow.app.ui.theme.brandGradient

/**
 * Primary CTA — solid dark-emerald pill, white label.
 * Matches every "Resize / Compress & Save / Export Processed Image / Continue"
 * button in the Stitch reference screens.
 * Pass `useGradient = true` for the Welcome Continue button (softer L→R fill).
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    testId: String,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingArrow: Boolean = false,
    useGradient: Boolean = false,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(999.dp))
            .then(
                if (!enabled) Modifier.background(color = PrimaryDeep.copy(alpha = 0.4f))
                else if (useGradient) Modifier.background(brush = brandGradient())
                else Modifier.background(color = PrimaryDeep)
            )
            .clickable(enabled = enabled, onClick = onClick)
            .testTag(testId),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            if (leadingIcon != null) {
                Box(Modifier.padding(end = 12.dp).size(22.dp)) { leadingIcon() }
            }
            Text(
                text = text,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
            if (trailingArrow) {
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(start = 12.dp).size(22.dp)
                )
            }
        }
    }
}
