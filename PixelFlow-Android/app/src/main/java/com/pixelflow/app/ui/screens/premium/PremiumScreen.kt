package com.pixelflow.app.ui.screens.premium

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.pixelflow.app.ui.components.BottomNavBar
import com.pixelflow.app.ui.components.PixelFlowTopBar
import com.pixelflow.app.ui.components.PrimaryButton
import com.pixelflow.app.ui.navigation.Routes
import com.pixelflow.app.ui.theme.Mint
import com.pixelflow.app.ui.theme.OnSurface
import com.pixelflow.app.ui.theme.OnSurfaceVariant
import com.pixelflow.app.ui.theme.PrimaryDeep
import com.pixelflow.app.ui.theme.PrimarySecondary
import com.pixelflow.app.ui.theme.SurfaceContainerHigh
import com.pixelflow.app.ui.theme.brandGradient

@Composable
fun PremiumScreen(nav: NavHostController) {
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
        .testTag("premium-screen")) {
        Column(modifier = Modifier.fillMaxSize().padding(bottom = 72.dp)) {
            PixelFlowTopBar(title = "PixelFlow", onBack = { nav.popBackStack() },
                titleColor = PrimarySecondary)

            Column(
                modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                // Premium gradient card
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(28.dp))
                        .background(brush = brandGradient())
                        .padding(24.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(Color.White.copy(alpha = 0.22f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Filled.WorkspacePremium, contentDescription = null,
                            tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.size(6.dp))
                        Text("ULTIMATE TIER", style = MaterialTheme.typography.labelLarge,
                            color = Color.White)
                    }
                    Spacer(Modifier.height(16.dp))
                    Text("PixelFlow\nPremium",
                        style = MaterialTheme.typography.displayLarge, color = Color.White)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Remove all limits. Same tools. Same privacy — nothing extra, no tracking.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(Modifier.height(20.dp))
                    PerkRow(Icons.Filled.Layers, "Unlimited batch processing",
                        "Process hundreds of images at once.")
                    PerkRow(Icons.Filled.Storage, "Unlimited file size",
                        "No restrictions on your heavy raw files.")
                    PerkRow(Icons.Filled.CloudOff, "More export formats",
                        "Unlock every supported destination format.")
                    PerkRow(Icons.Filled.Bolt, "Priority local processing",
                        "Batch jobs use more cores in parallel.")
                    PerkRow(Icons.Filled.Refresh, "Lifetime updates",
                        "All future features included.")
                }

                Spacer(Modifier.height(20.dp))

                PrimaryButton(
                    text = "Upgrade to Premium",
                    onClick = { /* wire to Play Billing */ },
                    trailingArrow = true,
                    testId = "premium-upgrade-button"
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Account required only for Premium.",
                    style = MaterialTheme.typography.labelLarge,
                    color = OnSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(20.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SmallInfoCard("Secure Checkout", Icons.Filled.GppGood, Modifier.weight(1f))
                    SmallInfoCard("Privacy First",   Icons.Filled.Shield, Modifier.weight(1f))
                }

                Spacer(Modifier.height(24.dp))
            }
        }

        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            BottomNavBar(nav = nav, currentRoute = Routes.PREMIUM)
        }
    }
}

@Composable
private fun PerkRow(icon: ImageVector, title: String, subtitle: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color.White)
        }
        Spacer(Modifier.size(12.dp))
        Column {
            Text(title, style = MaterialTheme.typography.titleMedium, color = Color.White)
            Text(subtitle, style = MaterialTheme.typography.labelLarge,
                color = Color.White.copy(alpha = 0.85f))
        }
    }
}

@Composable
private fun SmallInfoCard(label: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceContainerHigh)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = PrimaryDeep, modifier = Modifier.size(22.dp))
        Spacer(Modifier.height(6.dp))
        Text(label, style = MaterialTheme.typography.titleMedium, color = OnSurface)
    }
}
