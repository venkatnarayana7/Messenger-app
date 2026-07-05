package com.pixelflow.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pixelflow.app.ui.theme.OnSurfaceVariant
import com.pixelflow.app.ui.theme.PrimaryDeep
import com.pixelflow.app.ui.theme.PrimarySecondary

/**
 * PixelFlow top bar.
 * - With `onBack`: title is CENTERED (matches Resize/Compress/Upscale/Home in the mocks).
 * - Without `onBack`: title is LEFT-ALIGNED (matches Welcome screen).
 */
@Composable
fun PixelFlowTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    onMore: (() -> Unit)? = null,
    titleColor: Color = PrimaryDeep,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Leading slot (back arrow or spacer)
            Box(
                modifier = Modifier.size(56.dp)
                    .then(
                        if (onBack != null) Modifier.clickable(onClick = onBack).testTag("top-bar-back")
                        else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (onBack != null) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = PrimarySecondary
                    )
                }
            }

            // Title — centered if back present, left otherwise
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = titleColor,
                textAlign = if (onBack != null) TextAlign.Center else TextAlign.Start,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = if (onBack != null) 0.dp else 4.dp)
                    .testTag("top-bar-title")
            )

            // Trailing "more" slot
            Box(
                modifier = Modifier.size(56.dp)
                    .clickable(enabled = onMore != null) { onMore?.invoke() }
                    .testTag("top-bar-more"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "More",
                    tint = OnSurfaceVariant
                )
            }
        }
    }
}
