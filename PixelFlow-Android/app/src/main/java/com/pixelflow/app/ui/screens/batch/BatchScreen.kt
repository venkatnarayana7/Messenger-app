package com.pixelflow.app.ui.screens.batch

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.width
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.pixelflow.app.domain.model.FreeTierLimits
import com.pixelflow.app.ui.components.BottomNavBar
import com.pixelflow.app.ui.components.PixelFlowTopBar
import com.pixelflow.app.ui.components.PrimaryButton
import com.pixelflow.app.ui.components.SecondaryButton
import com.pixelflow.app.ui.navigation.Routes
import com.pixelflow.app.ui.theme.Mint
import com.pixelflow.app.ui.theme.OnSurface
import com.pixelflow.app.ui.theme.OnSurfaceVariant
import com.pixelflow.app.ui.theme.PrimaryDeep
import com.pixelflow.app.ui.theme.PrimaryLight
import com.pixelflow.app.ui.theme.PrimarySecondary
import com.pixelflow.app.ui.theme.SurfaceContainer

@Composable
fun BatchScreen(nav: NavHostController) {
    val vm: BatchViewModel = viewModel()
    val s by vm.state.collectAsState()

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(
            maxItems = FreeTierLimits.BATCH_MAX_FILES
        )
    ) { uris -> if (uris.isNotEmpty()) vm.addImages(uris) }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
        .testTag("batch-screen")) {
        Column(modifier = Modifier.fillMaxSize().padding(bottom = 72.dp)) {
            PixelFlowTopBar(title = "Batch Processing", onBack = { nav.popBackStack() })

            LazyColumn(
                modifier = Modifier.weight(1f).padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.background)
                            .padding(16.dp)
                            .testTag("batch-overall-card")
                    ) {
                        Text("OVERALL PROGRESS", style = MaterialTheme.typography.labelLarge,
                            color = OnSurfaceVariant)
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text("${(s.overallProgress * 100).toInt()}%",
                                style = MaterialTheme.typography.headlineLarge, color = PrimaryDeep,
                                modifier = Modifier.weight(1f))
                            Text(
                                if (s.items.isEmpty()) "0 / 0"
                                else "Processing ${s.completedCount} / ${s.items.size}",
                                style = MaterialTheme.typography.titleMedium, color = PrimarySecondary
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { s.overallProgress },
                            color = PrimarySecondary,
                            trackColor = PrimaryLight.copy(alpha = 0.35f),
                            modifier = Modifier.fillMaxWidth().height(10.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .testTag("batch-overall-progress")
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Switch(
                                checked = s.applySameSettings,
                                onCheckedChange = { vm.toggleApplySameSettings(it) },
                                colors = SwitchDefaults.colors(
                                    checkedTrackColor = PrimarySecondary,
                                    checkedThumbColor = Color.White
                                )
                            )
                            Spacer(Modifier.size(8.dp))
                            Text("Apply same settings",
                                style = MaterialTheme.typography.titleMedium, color = OnSurface,
                                modifier = Modifier.weight(1f))
                            SecondaryButton(
                                text = "Run Batch",
                                onClick = { vm.runBatchCompress() },
                                enabled = s.items.isNotEmpty() && !s.running,
                                testId = "batch-run-button",
                                modifier = Modifier.width(140.dp)
                            )
                        }
                    }
                }

                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Active Queue",
                            style = MaterialTheme.typography.titleLarge, color = OnSurface,
                            modifier = Modifier.weight(1f))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(Mint)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("${s.remainingCount} Remaining",
                                style = MaterialTheme.typography.labelLarge, color = PrimaryDeep)
                        }
                    }
                }

                items(s.items, key = { it.uri.toString() }) { item ->
                    BatchRow(item = item, onRemove = { vm.removeItem(item.uri) })
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        SecondaryButton(
                            text = "Add Images",
                            onClick = {
                                picker.launch(PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                ))
                            },
                            testId = "batch-add-images",
                            modifier = Modifier.weight(1f)
                        )
                        SecondaryButton(
                            text = "Global Settings",
                            onClick = { /* no-op — quality currently uses default */ },
                            testId = "batch-global-settings",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                if (!s.isPremium) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Mint.copy(alpha = 0.5f))
                                .padding(12.dp)
                                .testTag("batch-premium-hint"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.Tune, contentDescription = null, tint = PrimaryDeep, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.size(8.dp))
                            Text(
                                "Free tier: up to ${FreeTierLimits.BATCH_MAX_FILES} images per batch. Premium removes the cap.",
                                style = MaterialTheme.typography.labelLarge, color = PrimaryDeep
                            )
                        }
                    }
                }

                item { Spacer(Modifier.height(24.dp)) }
            }
        }
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            BottomNavBar(nav = nav, currentRoute = Routes.BATCH)
        }
    }
}

@Composable
private fun BatchRow(item: BatchItem, onRemove: () -> Unit) {
    val processing = item.status == BatchItemStatus.PROCESSING
    Row(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (processing) Mint.copy(alpha = 0.5f) else SurfaceContainer
            )
            .padding(10.dp)
            .testTag("batch-row-${item.name}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(56.dp).clip(RoundedCornerShape(12.dp))
        ) {
            AsyncImage(
                model = item.uri, contentDescription = item.name,
                modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop
            )
            if (item.status == BatchItemStatus.DONE) {
                Icon(
                    Icons.Filled.CheckCircle, contentDescription = "Done",
                    tint = PrimarySecondary,
                    modifier = Modifier.align(Alignment.Center).size(28.dp)
                )
            }
        }
        Spacer(Modifier.size(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.name, style = MaterialTheme.typography.titleMedium, color = OnSurface, maxLines = 1)
            when (item.status) {
                BatchItemStatus.QUEUED -> Text("Waiting in queue",
                    style = MaterialTheme.typography.labelLarge, color = OnSurfaceVariant)
                BatchItemStatus.PROCESSING -> LinearProgressIndicator(
                    progress = { item.progress },
                    color = PrimarySecondary,
                    trackColor = PrimaryLight.copy(alpha = 0.35f),
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(999.dp))
                )
                BatchItemStatus.DONE -> Text("Done · ${item.outputBytes / 1024} KB",
                    style = MaterialTheme.typography.labelLarge, color = PrimarySecondary)
                BatchItemStatus.FAILED -> Text("Failed · ${item.error ?: "unknown"}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.error)
            }
        }
        Box(
            modifier = Modifier.size(36.dp)
                .clip(RoundedCornerShape(999.dp))
                .clickable(onClick = onRemove)
                .testTag("batch-row-remove-${item.name}"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                if (item.status == BatchItemStatus.QUEUED) Icons.Filled.DragHandle else Icons.Filled.Close,
                contentDescription = "Remove",
                tint = if (item.status == BatchItemStatus.PROCESSING) MaterialTheme.colorScheme.error else OnSurfaceVariant
            )
        }
    }
}

// End of BatchScreen.kt
