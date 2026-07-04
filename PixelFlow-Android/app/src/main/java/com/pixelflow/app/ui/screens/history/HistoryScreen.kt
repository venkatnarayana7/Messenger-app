package com.pixelflow.app.ui.screens.history

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.pixelflow.app.PixelFlowApp
import com.pixelflow.app.data.entity.HistoryEntry
import com.pixelflow.app.ui.components.BottomNavBar
import com.pixelflow.app.ui.components.PixelFlowTopBar
import com.pixelflow.app.ui.navigation.Routes
import com.pixelflow.app.ui.theme.ErrorContainer
import com.pixelflow.app.ui.theme.ErrorRed
import com.pixelflow.app.ui.theme.OnSurface
import com.pixelflow.app.ui.theme.OnSurfaceVariant
import com.pixelflow.app.ui.theme.PrimaryDeep
import com.pixelflow.app.ui.theme.PrimarySecondary
import com.pixelflow.app.ui.theme.SurfaceContainer
import com.pixelflow.app.util.FormatUtil
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(nav: NavHostController) {
    val ctx = LocalContext.current
    val app = ctx.applicationContext as PixelFlowApp
    val scope = rememberCoroutineScope()

    val entries by app.historyRepo.observeAll().collectAsState(initial = emptyList())
    val count by app.historyRepo.observeCount().collectAsState(initial = 0)
    val storageSaved by app.historyRepo.observeStorageSaved().collectAsState(initial = 0L)
    var confirmClearAll by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
        .testTag("history-screen")) {
        Column(modifier = Modifier.fillMaxSize().padding(bottom = 72.dp)) {
            PixelFlowTopBar(title = "History", onBack = { nav.popBackStack() })

            LazyColumn(
                modifier = Modifier.weight(1f).padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard("Storage Saved",
                            FormatUtil.formatBytes(storageSaved.coerceAtLeast(0)),
                            "history-stat-storage",
                            Modifier.weight(1f))
                        StatCard("Files Processed",
                            "$count",
                            "history-stat-count",
                            Modifier.weight(1f))
                    }
                }
                if (entries.isEmpty()) {
                    item {
                        Text(
                            "No history yet. Files you process will appear here.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = OnSurfaceVariant,
                            modifier = Modifier.testTag("history-empty").padding(top = 24.dp)
                        )
                    }
                } else {
                    item {
                        Text("Recent Files", style = MaterialTheme.typography.titleLarge,
                            color = OnSurface)
                    }
                    items(entries, key = { it.id }) { entry ->
                        HistoryRow(
                            entry = entry,
                            onOpen = { openFile(ctx, entry.outputUri) },
                            onDelete = { scope.launch { app.historyRepo.delete(entry.id) } }
                        )
                    }
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(ErrorContainer)
                                .clickable { confirmClearAll = true }
                                .padding(vertical = 16.dp)
                                .testTag("history-clear-all"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Filled.DeleteSweep, contentDescription = null, tint = ErrorRed)
                            Spacer(Modifier.size(8.dp))
                            Text("Delete History", style = MaterialTheme.typography.titleMedium,
                                color = ErrorRed)
                        }
                    }
                }

                item { Spacer(Modifier.height(24.dp)) }
            }
        }

        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            BottomNavBar(nav = nav, currentRoute = Routes.HISTORY)
        }

        if (confirmClearAll) {
            AlertDialog(
                onDismissRequest = { confirmClearAll = false },
                title = { Text("Delete all history?") },
                text = {
                    Text("This only removes local records — your saved image files stay in your gallery.")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            scope.launch { app.historyRepo.clear() }
                            confirmClearAll = false
                        },
                        modifier = Modifier.testTag("history-confirm-clear")
                    ) { Text("Delete", color = ErrorRed) }
                },
                dismissButton = {
                    TextButton(onClick = { confirmClearAll = false }) { Text("Cancel") }
                }
            )
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, testId: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.testTag(testId),
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.labelLarge, color = OnSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineMedium, color = PrimaryDeep)
        }
    }
}

@Composable
private fun HistoryRow(entry: HistoryEntry, onOpen: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceContainer)
            .clickable(onClick = onOpen)
            .padding(10.dp)
            .testTag("history-row-${entry.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = entry.outputUri,
            contentDescription = entry.filename,
            modifier = Modifier.size(64.dp).clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.size(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(entry.filename, style = MaterialTheme.typography.titleMedium,
                color = OnSurface, maxLines = 1)
            Row {
                Text(FormatUtil.formatBytes(entry.outputSizeBytes),
                    style = MaterialTheme.typography.labelLarge, color = OnSurfaceVariant)
                Text("  ·  ", style = MaterialTheme.typography.labelLarge, color = OnSurfaceVariant)
                Text(dateFormat.format(Date(entry.createdAt)),
                    style = MaterialTheme.typography.labelLarge, color = OnSurfaceVariant)
            }
        }
        Icon(Icons.Filled.Download, contentDescription = "Open",
            tint = PrimarySecondary,
            modifier = Modifier.size(28.dp).padding(6.dp).clickable(onClick = onOpen))
        Icon(Icons.Filled.Delete, contentDescription = "Delete",
            tint = ErrorRed,
            modifier = Modifier
                .size(28.dp).padding(6.dp)
                .clickable(onClick = onDelete)
                .testTag("history-row-delete-${entry.id}"))
    }
}

private val dateFormat = SimpleDateFormat("MMM dd, hh:mm a", Locale.US)

private fun openFile(ctx: android.content.Context, uriStr: String) {
    val uri = Uri.parse(uriStr)
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "image/*")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    runCatching { ctx.startActivity(Intent.createChooser(intent, "Open")) }
}
