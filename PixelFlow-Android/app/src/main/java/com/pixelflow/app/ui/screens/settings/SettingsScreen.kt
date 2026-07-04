package com.pixelflow.app.ui.screens.settings

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.pixelflow.app.PixelFlowApp
import com.pixelflow.app.ui.components.BottomNavBar
import com.pixelflow.app.ui.components.PixelFlowTopBar
import com.pixelflow.app.ui.navigation.Routes
import com.pixelflow.app.ui.theme.Mint
import com.pixelflow.app.ui.theme.OnSurface
import com.pixelflow.app.ui.theme.OnSurfaceVariant
import com.pixelflow.app.ui.theme.PrimaryDeep
import com.pixelflow.app.ui.theme.PrimarySecondary
import com.pixelflow.app.ui.theme.SurfaceContainer
import com.pixelflow.app.ui.theme.SurfaceContainerHigh

@Composable
fun SettingsScreen(nav: NavHostController) {
    val ctx = LocalContext.current
    val app = ctx.applicationContext as PixelFlowApp
    val settings by app.settingsRepo.observe().collectAsState(initial = com.pixelflow.app.data.entity.AppSettings())

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
        .testTag("settings-screen")) {
        Column(modifier = Modifier.fillMaxSize().padding(bottom = 72.dp)) {
            PixelFlowTopBar(title = "Settings", onBack = { nav.popBackStack() })

            Column(
                modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                // Account block (no login required — free-tier default)
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceContainerHigh)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(52.dp).clip(CircleShape).background(Mint),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("PF", style = MaterialTheme.typography.titleLarge, color = PrimaryDeep)
                    }
                    Spacer(Modifier.size(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Guest", style = MaterialTheme.typography.titleLarge, color = OnSurface)
                        Text(if (settings.isPremium) "Premium account" else "Free account · Local only",
                            style = MaterialTheme.typography.labelLarge, color = OnSurfaceVariant)
                    }
                    Text("Edit", style = MaterialTheme.typography.labelLarge, color = PrimarySecondary,
                        modifier = Modifier.testTag("settings-edit"))
                }

                SectionHeader("APPEARANCE")
                SettingsCard {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SettingIcon(Icons.Filled.Palette)
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Theme", style = MaterialTheme.typography.titleMedium, color = OnSurface)
                            Text("Light mode only",
                                style = MaterialTheme.typography.labelLarge, color = OnSurfaceVariant)
                        }
                        Switch(checked = false, onCheckedChange = {},
                            enabled = false,
                            colors = SwitchDefaults.colors(checkedTrackColor = PrimarySecondary))
                    }
                    Divider()
                    SettingsRow(Icons.Filled.Language, "Language", settings.language,
                        testId = "settings-language") {}
                }

                SectionHeader("PREFERENCES")
                SettingsCard {
                    SettingsRow(Icons.Filled.FolderOpen, "Default output folder",
                        if (settings.defaultOutputFolder.isEmpty()) "Pictures/PixelFlow"
                        else settings.defaultOutputFolder,
                        testId = "settings-output-folder") {}
                    Divider()
                    SettingsRow(Icons.Filled.HighQuality, "Image quality",
                        "${settings.defaultQuality}%",
                        testId = "settings-quality") {}
                }

                SectionHeader("PRIVACY")
                SettingsCard {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).testTag("settings-privacy"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SettingIcon(Icons.Filled.Lock)
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Privacy",
                                style = MaterialTheme.typography.titleMedium, color = OnSurface)
                            Text(
                                "No data leaves your device for processing.",
                                style = MaterialTheme.typography.labelLarge, color = OnSurfaceVariant
                            )
                        }
                    }
                }

                SectionHeader("APP INFO")
                SettingsCard {
                    SettingsRow(Icons.Filled.Info,     "About PixelFlow", null, testId = "settings-about") {}
                    Divider()
                    SettingsRow(Icons.Filled.Help,  "Contact Support", null, testId = "settings-support") {}
                    Divider()
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).testTag("settings-rate"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SettingIcon(Icons.Filled.Star)
                        Text("Rate App", style = MaterialTheme.typography.titleMedium,
                            color = OnSurface, modifier = Modifier.weight(1f))
                        Text("5.0 ★", style = MaterialTheme.typography.labelLarge,
                            color = PrimarySecondary)
                    }
                }

                Spacer(Modifier.height(20.dp))
                Text("PixelFlow v1.0.0", style = MaterialTheme.typography.labelLarge,
                    color = OnSurfaceVariant, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(24.dp))
            }
        }

        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            BottomNavBar(nav = nav, currentRoute = Routes.SETTINGS)
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Spacer(Modifier.height(20.dp))
    Text(text, style = MaterialTheme.typography.labelLarge, color = PrimarySecondary)
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceContainerHigh)
            .padding(16.dp)
    ) { content() }
}

@Composable
private fun SettingsRow(icon: ImageVector, label: String, value: String?, testId: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).clickable(onClick = onClick).testTag(testId),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingIcon(icon)
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.titleMedium, color = OnSurface)
            if (value != null) Text(value, style = MaterialTheme.typography.labelLarge, color = OnSurfaceVariant)
        }
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = OnSurfaceVariant)
    }
}

@Composable
private fun SettingIcon(icon: ImageVector) {
    Box(
        modifier = Modifier.size(36.dp).clip(CircleShape).background(SurfaceContainer),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = PrimaryDeep, modifier = Modifier.size(20.dp))
    }
    Spacer(Modifier.size(12.dp))
}

@Composable
private fun Divider() {
    Box(
        modifier = Modifier.fillMaxWidth()
            .height(1.dp)
            .padding(vertical = 2.dp)
            .background(com.pixelflow.app.ui.theme.OutlineSoft)
    )
}
