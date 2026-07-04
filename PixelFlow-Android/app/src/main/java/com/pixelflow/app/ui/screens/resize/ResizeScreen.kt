package com.pixelflow.app.ui.screens.resize

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.pixelflow.app.domain.model.InterpolationMethod
import com.pixelflow.app.domain.model.ResizePreset
import com.pixelflow.app.ui.components.BottomNavBar
import com.pixelflow.app.ui.components.ImagePickerBox
import com.pixelflow.app.ui.components.PixelChip
import com.pixelflow.app.ui.components.PixelFlowTopBar
import com.pixelflow.app.ui.components.PrimaryButton
import com.pixelflow.app.ui.components.SecondaryButton
import com.pixelflow.app.ui.navigation.Routes
import com.pixelflow.app.ui.theme.Mint
import com.pixelflow.app.ui.theme.OnSurface
import com.pixelflow.app.ui.theme.OnSurfaceVariant
import com.pixelflow.app.ui.theme.PrimaryLight
import com.pixelflow.app.ui.theme.PrimarySecondary
import com.pixelflow.app.ui.theme.SurfaceContainer
import com.pixelflow.app.util.FormatUtil

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ResizeScreen(nav: NavHostController) {
    val vm: ResizeViewModel = viewModel()
    val s by vm.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
        .testTag("resize-screen")) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 72.dp)
        ) {
            PixelFlowTopBar(title = "Resize Image", onBack = { nav.popBackStack() })

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                ImagePickerBox(
                    selectedUri = s.sourceUri,
                    subtitle = if (s.sourceUri == null) "Drag and drop or click to browse"
                    else "${s.originalWidth}×${s.originalHeight} · ${FormatUtil.formatBytes(s.originalSize)}",
                    onImagePicked = { vm.loadImage(it) },
                    testId = "resize-image-picker"
                )
                Spacer(Modifier.height(24.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Dimensions",
                        style = MaterialTheme.typography.headlineSmall, color = OnSurface,
                        modifier = Modifier.weight(1f))
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(Mint)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("resize-lock-aspect"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Lock Aspect Ratio",
                            style = MaterialTheme.typography.labelLarge, color = OnSurface,
                            modifier = Modifier.padding(end = 8.dp))
                        Switch(
                            checked = s.lockAspect,
                            onCheckedChange = { vm.toggleLock(it) },
                            colors = SwitchDefaults.colors(
                                checkedTrackColor = PrimarySecondary,
                                checkedThumbColor = androidx.compose.ui.graphics.Color.White,
                            )
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Width (px)", style = MaterialTheme.typography.labelLarge, color = OnSurfaceVariant)
                        Spacer(Modifier.height(4.dp))
                        OutlinedTextField(
                            value = s.width.toString(),
                            onValueChange = { txt -> txt.toIntOrNull()?.let { vm.setWidth(it) } },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth().testTag("resize-input-width")
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Height (px)", style = MaterialTheme.typography.labelLarge, color = OnSurfaceVariant)
                        Spacer(Modifier.height(4.dp))
                        OutlinedTextField(
                            value = s.height.toString(),
                            onValueChange = { txt -> txt.toIntOrNull()?.let { vm.setHeight(it) } },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth().testTag("resize-input-height")
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceContainer)
                        .padding(16.dp)
                        .testTag("resize-scale-panel")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Scale Percentage",
                            style = MaterialTheme.typography.titleMedium, color = OnSurface,
                            modifier = Modifier.weight(1f))
                        Text("${s.scalePercent}%",
                            style = MaterialTheme.typography.headlineSmall, color = PrimarySecondary)
                    }
                    Slider(
                        value = s.scalePercent.toFloat(),
                        valueRange = 1f..400f,
                        onValueChange = { vm.setPercent(it.toInt()) },
                        colors = SliderDefaults.colors(
                            thumbColor = PrimarySecondary,
                            activeTrackColor = PrimarySecondary,
                            inactiveTrackColor = PrimaryLight.copy(alpha = 0.35f)
                        ),
                        modifier = Modifier.testTag("resize-scale-slider")
                    )
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text("1%", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant,
                            modifier = Modifier.weight(1f))
                        Text("100%", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant,
                            modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        Text("400%", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant,
                            modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.End)
                    }
                }

                Spacer(Modifier.height(20.dp))

                Text("Presets", style = MaterialTheme.typography.titleMedium, color = OnSurface)
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ResizePreset.ALL.forEach { p ->
                        PixelChip(
                            text = p.label,
                            selected = s.width == p.width && s.height == p.height,
                            testId = "resize-preset-${p.label.lowercase()}",
                            onClick = { vm.applyPreset(p.width, p.height) }
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                Text("Interpolation", style = MaterialTheme.typography.titleMedium, color = OnSurface)
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    InterpolationMethod.entries.forEach { m ->
                        PixelChip(
                            text = m.displayName,
                            selected = s.method == m,
                            testId = "resize-method-${m.name.lowercase()}",
                            onClick = { vm.setMethod(m) }
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                PrimaryButton(
                    text = if (s.processing) "Resizing…" else "Resize",
                    onClick = { vm.processAndSave() },
                    enabled = s.sourceUri != null && !s.processing,
                    leadingIcon = {
                        Icon(Icons.Filled.Image, contentDescription = null,
                            tint = androidx.compose.ui.graphics.Color.White)
                    },
                    testId = "resize-primary-button"
                )
                Spacer(Modifier.height(12.dp))
                SecondaryButton(
                    text = if (s.savedUri != null) "Saved to Gallery" else "Download",
                    onClick = { /* saved automatically to gallery on Resize */ },
                    enabled = s.savedUri != null,
                    testId = "resize-download-button"
                )

                Spacer(Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth().testTag("resize-no-watermark-note")
                ) {
                    Icon(Icons.Filled.Shield, contentDescription = null,
                        tint = OnSurfaceVariant, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.size(6.dp))
                    Text("No watermarks will be added.",
                        style = MaterialTheme.typography.labelLarge, color = OnSurfaceVariant)
                }

                if (s.error != null) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = s.error ?: "",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.testTag("resize-error")
                    )
                }
                Spacer(Modifier.height(24.dp))
            }
        }

        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            BottomNavBar(nav = nav, currentRoute = Routes.RESIZE)
        }
    }
}
