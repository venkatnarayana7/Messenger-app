package com.pixelflow.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AspectRatio
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Compress
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.SwapVert
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.pixelflow.app.PixelFlowApp
import com.pixelflow.app.ui.components.BottomNavBar
import com.pixelflow.app.ui.components.PixelFlowTopBar
import com.pixelflow.app.ui.components.ToolCard
import com.pixelflow.app.ui.navigation.Routes
import com.pixelflow.app.ui.theme.OnSurface
import com.pixelflow.app.ui.theme.OnSurfaceVariant
import com.pixelflow.app.ui.theme.PrimarySecondary
import com.pixelflow.app.ui.theme.brandGradient
import java.util.Calendar

@Composable
fun HomeScreen(nav: NavHostController) {
    val greeting = remember { greetingForNow() }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("home-screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 72.dp) // bottom nav
        ) {
            PixelFlowTopBar(title = "PixelFlow", onBack = { nav.navigateUp() })

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
            ) {
                Text(greeting, style = MaterialTheme.typography.headlineLarge, color = OnSurface)
                Spacer(Modifier.height(4.dp))
                Text(
                    "Ready to perfect your images?",
                    style = MaterialTheme.typography.bodyLarge, color = OnSurfaceVariant
                )

                Spacer(Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ToolCard(
                        icon = Icons.Outlined.AspectRatio,
                        title = "Resize Image",
                        subtitle = "Scale precisely without quality loss.",
                        testId = "home-tool-resize",
                        modifier = Modifier.weight(1f),
                    ) { nav.navigate(Routes.RESIZE) }

                    ToolCard(
                        icon = Icons.Outlined.Compress,
                        title = "Compress Image",
                        subtitle = "Reduce size while keeping clarity.",
                        testId = "home-tool-compress",
                        modifier = Modifier.weight(1f),
                    ) { nav.navigate(Routes.COMPRESS) }
                }
                Spacer(Modifier.height(16.dp))

                // NOTE: Copy strictly avoids "AI" wording per §2.
                ToolCard(
                    icon = Icons.Outlined.AutoAwesome,
                    title = "Upscale Image",
                    subtitle = "Enlarge with classical interpolation — Lanczos, Bicubic, Bilinear or Nearest.",
                    testId = "home-tool-upscale",
                    modifier = Modifier.fillMaxWidth(),
                ) { nav.navigate(Routes.UPSCALE) }

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ToolCard(
                        icon = Icons.Outlined.SwapVert,
                        title = "Convert Format",
                        subtitle = "PNG, JPG, WEBP, and more.",
                        testId = "home-tool-convert",
                        modifier = Modifier.weight(1f),
                    ) { nav.navigate(Routes.CONVERT) }

                    ToolCard(
                        icon = Icons.Outlined.Layers,
                        title = "Batch Processing",
                        subtitle = "Apply edits across many images at once.",
                        testId = "home-tool-batch",
                        modifier = Modifier.weight(1f),
                    ) { nav.navigate(Routes.BATCH) }
                }

                Spacer(Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "Recent Files",
                        style = MaterialTheme.typography.titleLarge, color = OnSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        "View all",
                        style = MaterialTheme.typography.labelLarge,
                        color = PrimarySecondary,
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.small)
                            .clickable { nav.navigate(Routes.HISTORY) }
                            .padding(8.dp)
                            .testTag("home-view-all")
                    )
                }
                Spacer(Modifier.height(8.dp))
                RecentFilesRow(nav)
                Spacer(Modifier.height(32.dp))
            }
        }

        // Floating gradient FAB
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 88.dp)
                .size(60.dp)
                .clip(CircleShape)
                .background(brush = brandGradient())
                .clickable { nav.navigate(Routes.RESIZE) }
                .testTag("home-fab-select-images"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.Add, contentDescription = "Select Images",
                tint = Color.White, modifier = Modifier.size(28.dp)
            )
        }

        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            BottomNavBar(nav = nav, currentRoute = Routes.HOME)
        }
    }
}

@Composable
private fun RecentFilesRow(nav: NavHostController) {
    val ctx = LocalContext.current
    val app = ctx.applicationContext as PixelFlowApp
    val recent by app.historyRepo.observeRecent(8).collectAsState(initial = emptyList())

    if (recent.isEmpty()) {
        Text(
            text = "No recent files yet. Process an image to see it here.",
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceVariant,
            modifier = Modifier.testTag("home-recent-empty")
        )
        return
    }
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 0.dp),
    ) {
        items(recent) { entry ->
            Column(
                modifier = Modifier
                    .clickable { nav.navigate(Routes.HISTORY) }
                    .testTag("home-recent-${entry.id}")
            ) {
                AsyncImage(
                    model = entry.thumbnailPath ?: entry.outputUri,
                    contentDescription = entry.filename,
                    modifier = Modifier
                        .width(160.dp)
                        .height(120.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = entry.filename,
                    style = MaterialTheme.typography.labelLarge,
                    color = OnSurface,
                    maxLines = 1
                )
            }
        }
    }
}

private fun greetingForNow(): String {
    val h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        h < 5  -> "Good night"
        h < 12 -> "Good morning"
        h < 17 -> "Good afternoon"
        else   -> "Good evening"
    }
}
