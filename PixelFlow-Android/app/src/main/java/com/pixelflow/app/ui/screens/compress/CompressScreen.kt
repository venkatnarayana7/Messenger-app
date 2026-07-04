package com.pixelflow.app.ui.screens.compress

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import com.pixelflow.app.ui.components.BottomNavBar
import com.pixelflow.app.ui.components.ImagePickerBox
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
import com.pixelflow.app.util.FormatUtil

@Composable
fun CompressScreen(nav: NavHostController) {
    val vm: CompressViewModel = viewModel()
    val s by vm.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
        .testTag("compress-screen")) {
        Column(modifier = Modifier.fillMaxSize().padding(bottom = 72.dp)) {
            PixelFlowTopBar(title = "Compress Image", onBack = { nav.popBackStack() })

            Column(
                modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                if (s.sourceUri == null) {
                    ImagePickerBox(
                        selectedUri = null,
                        subtitle = "Drag and drop or click to browse",
                        onImagePicked = { vm.loadImage(it) },
                        testId = "compress-image-picker"
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(220.dp)
                            .clip(RoundedCornerShape(20.dp))
                    ) {
                        AsyncImage(
                            model = s.sourceUri,
                            contentDescription = "Source",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(12.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(PrimaryDeep)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                "Original: ${FormatUtil.formatBytes(s.originalSize)}",
                                style = MaterialTheme.typography.labelLarge, color = Color.White
                            )
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))

                Column(
                    modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceContainer)
                        .padding(16.dp)
                        .testTag("compress-slider-panel")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Compression Quality",
                            style = MaterialTheme.typography.titleMedium, color = OnSurface,
                            modifier = Modifier.weight(1f))
                        Text("${s.quality}%",
                            style = MaterialTheme.typography.headlineSmall, color = PrimarySecondary)
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
                        modifier = Modifier.testTag("compress-quality-slider")
                    )
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Estimated Size", style = MaterialTheme.typography.labelLarge, color = OnSurfaceVariant)
                            Text(FormatUtil.formatBytes(s.estimatedSize),
                                style = MaterialTheme.typography.headlineSmall, color = PrimarySecondary)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Saving", style = MaterialTheme.typography.labelLarge, color = OnSurfaceVariant)
                            Row {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(999.dp))
                                        .background(PrimaryLight)
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "${FormatUtil.percentSaved(s.originalSize, s.estimatedSize)}%",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = PrimaryDeep
                                    )
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text("Quality: ${vm.qualityLabel()}",
                        style = MaterialTheme.typography.labelLarge, color = OnSurfaceVariant)
                }
                Spacer(Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(Mint)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Shield, contentDescription = null, tint = PrimaryDeep, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.size(6.dp))
                        Text("Privacy-First (On-device)", style = MaterialTheme.typography.labelLarge, color = OnSurface)
                    }
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(Mint)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Bolt, contentDescription = null, tint = PrimaryDeep, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.size(6.dp))
                        Text("Real Compression", style = MaterialTheme.typography.labelLarge, color = OnSurface)
                    }
                }

                Spacer(Modifier.height(20.dp))

                PrimaryButton(
                    text = if (s.processing) "Compressing…" else "Compress & Save",
                    onClick = { vm.compressAndSave() },
                    enabled = s.sourceUri != null && !s.processing,
                    leadingIcon = {
                        Icon(Icons.Filled.Compress, contentDescription = null,
                            tint = androidx.compose.ui.graphics.Color.White)
                    },
                    testId = "compress-primary-button"
                )
                Spacer(Modifier.height(12.dp))
                SecondaryButton(
                    text = if (s.savedUri != null) "Saved to Gallery" else "Waiting…",
                    onClick = {},
                    enabled = s.savedUri != null,
                    testId = "compress-download-button"
                )

                if (s.error != null) {
                    Spacer(Modifier.height(12.dp))
                    Text(s.error ?: "", style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.error, modifier = Modifier.testTag("compress-error"))
                }
                Spacer(Modifier.height(24.dp))
            }
        }
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            BottomNavBar(nav = nav, currentRoute = Routes.COMPRESS)
        }
    }
}
