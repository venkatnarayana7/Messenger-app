package com.pixelflow.app.ui.screens.convert

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.pixelflow.app.domain.model.OutputFormat
import com.pixelflow.app.ui.components.BottomNavBar
import com.pixelflow.app.ui.components.ImagePickerBox
import com.pixelflow.app.ui.components.PixelChip
import com.pixelflow.app.ui.components.PixelFlowTopBar
import com.pixelflow.app.ui.components.PrimaryButton
import com.pixelflow.app.ui.components.SecondaryButton
import com.pixelflow.app.ui.navigation.Routes
import com.pixelflow.app.ui.theme.ErrorContainer
import com.pixelflow.app.ui.theme.ErrorRed
import com.pixelflow.app.ui.theme.OnSurface
import com.pixelflow.app.ui.theme.OnSurfaceVariant
import com.pixelflow.app.ui.theme.PrimaryDeep
import com.pixelflow.app.ui.theme.PrimaryLight
import com.pixelflow.app.ui.theme.PrimarySecondary
import com.pixelflow.app.ui.theme.SurfaceContainer
import com.pixelflow.app.util.FormatUtil

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ConvertScreen(nav: NavHostController) {
    val vm: ConvertViewModel = viewModel()
    val s by vm.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
        .testTag("convert-screen")) {
        Column(modifier = Modifier.fillMaxSize().padding(bottom = 72.dp)) {
            PixelFlowTopBar(title = "Convert Format", onBack = { nav.popBackStack() })

            Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)) {

                ImagePickerBox(
                    selectedUri = s.sourceUri,
                    subtitle = if (s.sourceUri == null) "Drag and drop or click to browse"
                    else "Source: ${s.sourceFormat} · ${FormatUtil.formatBytes(s.originalSize)}",
                    onImagePicked = { vm.loadImage(it) },
                    testId = "convert-image-picker"
                )
                Spacer(Modifier.height(20.dp))

                // Output format grid
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceContainer)
                        .padding(16.dp)
                ) {
                    Text("OUTPUT FORMAT", style = MaterialTheme.typography.labelLarge, color = PrimarySecondary)
                    Spacer(Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutputFormat.entries.forEach { fmt ->
                            PixelChip(
                                text = fmt.displayName,
                                selected = s.target == fmt,
                                testId = "convert-fmt-${fmt.name.lowercase()}",
                                onClick = { vm.setTarget(fmt) }
                            )
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))

                // Quality (lossy targets only)
                if (s.target.lossy) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(SurfaceContainer)
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("QUALITY SETTINGS", style = MaterialTheme.typography.labelLarge,
                                color = PrimarySecondary, modifier = Modifier.weight(1f))
                            Text("${s.quality}%", style = MaterialTheme.typography.headlineSmall,
                                color = PrimarySecondary)
                        }
                        Slider(
                            value = s.quality.toFloat(),
                            valueRange = 0f..100f,
                            onValueChange = { vm.setQuality(it.toInt()) },
                            colors = SliderDefaults.colors(
                                thumbColor = PrimarySecondary,
                                activeTrackColor = PrimarySecondary,
                                inactiveTrackColor = PrimaryLight.copy(alpha = 0.35f)
                            ),
                            modifier = Modifier.testTag("convert-quality-slider")
                        )
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text("Smallest File", style = MaterialTheme.typography.labelSmall,
                                color = OnSurfaceVariant, modifier = Modifier.weight(1f))
                            Text("Highest Quality", style = MaterialTheme.typography.labelSmall,
                                color = OnSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.End,
                                modifier = Modifier.weight(1f))
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Preserve EXIF", style = MaterialTheme.typography.titleMedium, color = OnSurface)
                                Text("Metadata, Location, Date",
                                    style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                            }
                            Switch(
                                checked = s.preserveExif,
                                onCheckedChange = { vm.togglePreserveExif(it) },
                                colors = SwitchDefaults.colors(
                                    checkedTrackColor = PrimarySecondary,
                                    checkedThumbColor = Color.White,
                                ),
                                modifier = Modifier.testTag("convert-preserve-exif")
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }

                if (s.showAlphaWarning) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(ErrorContainer)
                            .padding(12.dp)
                            .testTag("convert-alpha-warning"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Warning, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.size(8.dp))
                        Text(
                            "This target doesn't support transparency. " +
                                "Transparent pixels will be composited onto white.",
                            style = MaterialTheme.typography.labelLarge, color = ErrorRed
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                }

                PrimaryButton(
                    text = if (s.processing) "Converting…" else "Convert",
                    onClick = { vm.convertAndSave() },
                    enabled = s.sourceUri != null && !s.processing,
                    leadingIcon = { Icon(Icons.Filled.AutoFixHigh, contentDescription = null, tint = Color.White) },
                    testId = "convert-primary-button"
                )
                Spacer(Modifier.height(12.dp))
                SecondaryButton(
                    text = if (s.savedUri != null) "Saved · ${s.target.displayName}" else "Save to Device",
                    onClick = {},
                    enabled = s.savedUri != null,
                    testId = "convert-save-button"
                )

                if (s.savedUri != null) {
                    Spacer(Modifier.height(20.dp))
                    Text("SAMPLE PREVIEW", style = MaterialTheme.typography.labelLarge,
                        color = PrimarySecondary)
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth().height(180.dp)
                            .clip(RoundedCornerShape(20.dp))
                    ) {
                        AsyncImage(
                            model = s.savedUri, contentDescription = "Converted preview",
                            modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Ready · ${s.target.displayName}",
                        style = MaterialTheme.typography.labelLarge, color = PrimaryDeep
                    )
                }

                if (s.error != null) {
                    Spacer(Modifier.height(12.dp))
                    Text(s.error ?: "", style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.error, modifier = Modifier.testTag("convert-error"))
                }
                Spacer(Modifier.height(24.dp))
            }
        }
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            BottomNavBar(nav = nav, currentRoute = Routes.CONVERT)
        }
    }
}
