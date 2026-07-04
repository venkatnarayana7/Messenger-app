package com.pixelflow.app.ui.screens.upscale

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
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import com.pixelflow.app.domain.model.InterpolationMethod
import com.pixelflow.app.ui.components.BottomNavBar
import com.pixelflow.app.ui.components.ImagePickerBox
import com.pixelflow.app.ui.components.PixelChip
import com.pixelflow.app.ui.components.PixelFlowTopBar
import com.pixelflow.app.ui.components.PrimaryButton
import com.pixelflow.app.ui.navigation.Routes
import com.pixelflow.app.ui.theme.Mint
import com.pixelflow.app.ui.theme.OnSurface
import com.pixelflow.app.ui.theme.OnSurfaceVariant
import com.pixelflow.app.ui.theme.PrimaryDeep
import com.pixelflow.app.ui.theme.PrimaryLight
import com.pixelflow.app.ui.theme.PrimarySecondary
import com.pixelflow.app.ui.theme.SurfaceContainer

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UpscaleScreen(nav: NavHostController) {
    val vm: UpscaleViewModel = viewModel()
    val s by vm.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
        .testTag("upscale-screen")) {
        Column(modifier = Modifier.fillMaxSize().padding(bottom = 72.dp)) {
            PixelFlowTopBar(title = "Upscale Image", onBack = { nav.popBackStack() })

            Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)) {

                // Before/After block (spec §5 side-by-side).
                Box(
                    modifier = Modifier.fillMaxWidth().height(260.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Mint.copy(alpha = 0.3f))
                ) {
                    if (s.sourceUri != null) {
                        Row(modifier = Modifier.fillMaxSize()) {
                            Box(modifier = Modifier.weight(1f)) {
                                AsyncImage(
                                    model = s.sourceUri, contentDescription = "Before",
                                    modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop
                                )
                                Box(
                                    modifier = Modifier.padding(12.dp)
                                        .clip(RoundedCornerShape(999.dp))
                                        .background(Color.Black.copy(alpha = 0.55f))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("BEFORE", style = MaterialTheme.typography.labelSmall, color = Color.White)
                                }
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                AsyncImage(
                                    model = s.savedUri ?: s.sourceUri, contentDescription = "After",
                                    modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop
                                )
                                Box(
                                    modifier = Modifier.padding(12.dp)
                                        .align(Alignment.TopEnd)
                                        .clip(RoundedCornerShape(999.dp))
                                        .background(PrimaryDeep)
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("AFTER", style = MaterialTheme.typography.labelSmall, color = Color.White)
                                }
                            }
                        }
                    } else {
                        ImagePickerBox(
                            selectedUri = null,
                            onImagePicked = { vm.loadImage(it) },
                            heightDp = 260,
                            testId = "upscale-image-picker"
                        )
                    }
                }
                Spacer(Modifier.height(20.dp))

                // Scaling Factor
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceContainer)
                        .padding(16.dp)
                ) {
                    Text("Scaling Factor", style = MaterialTheme.typography.titleMedium, color = OnSurface)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(2, 4, 8).forEach { f ->
                            PixelChip(
                                text = "${f}x",
                                selected = s.factor == f,
                                testId = "upscale-factor-$f",
                                onClick = { vm.setFactor(f) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Text("Upscaling to 8× may result in a significantly larger file.",
                        style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                }
                Spacer(Modifier.height(16.dp))

                // Methodology (4 classical methods — NO AI, per §2)
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceContainer)
                        .padding(16.dp)
                ) {
                    Text("Methodology", style = MaterialTheme.typography.titleMedium, color = OnSurface)
                    Spacer(Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        InterpolationMethod.entries.forEach { m ->
                            PixelChip(
                                text = m.displayName,
                                selected = s.method == m,
                                testId = "upscale-method-${m.name.lowercase()}",
                                onClick = { vm.setMethod(m) }
                            )
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = s.method.description,
                        style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant
                    )
                }

                Spacer(Modifier.height(20.dp))

                if (s.processing) {
                    LinearProgressIndicator(
                        progress = { s.progress },
                        color = PrimarySecondary,
                        trackColor = PrimaryLight.copy(alpha = 0.35f),
                        modifier = Modifier.fillMaxWidth().height(8.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .testTag("upscale-progress")
                    )
                    Spacer(Modifier.height(12.dp))
                }

                PrimaryButton(
                    text = if (s.processing) "Processing…" else "Export Processed Image",
                    onClick = { vm.runUpscale() },
                    enabled = s.sourceUri != null && !s.processing,
                    leadingIcon = { Icon(Icons.Filled.Download, contentDescription = null, tint = Color.White) },
                    testId = "upscale-primary-button"
                )

                Spacer(Modifier.height(16.dp))
                Text(
                    "Privacy guaranteed. All processing occurs locally on device.",
                    style = MaterialTheme.typography.labelLarge, color = OnSurfaceVariant,
                    modifier = Modifier.fillMaxWidth().testTag("upscale-privacy-note")
                )

                if (s.error != null) {
                    Spacer(Modifier.height(12.dp))
                    Text(s.error ?: "", style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.error, modifier = Modifier.testTag("upscale-error"))
                }
                Spacer(Modifier.height(24.dp))
            }
        }
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            BottomNavBar(nav = nav, currentRoute = Routes.UPSCALE)
        }
    }
}
