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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.testTag
import com.pixelflow.app.ui.theme.OnSurface
import com.pixelflow.app.ui.theme.PrimaryLight
import com.pixelflow.app.ui.theme.SurfaceContainerHigh

/**
 * Secondary CTA — light-mint pill, dark label.
 * Matches "Download / Save to Device / Download All / Add Images" in the mocks.
 */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    testId: String,
    leadingIcon: (@Composable () -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(if (enabled) PrimaryLight else SurfaceContainerHigh)
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
                color = OnSurface,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
