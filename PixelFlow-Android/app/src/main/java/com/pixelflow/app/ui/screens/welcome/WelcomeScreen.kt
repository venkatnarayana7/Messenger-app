package com.pixelflow.app.ui.screens.welcome

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.pixelflow.app.PixelFlowApp
import com.pixelflow.app.ui.components.PixelFlowTopBar
import com.pixelflow.app.ui.components.PrimaryButton
import com.pixelflow.app.ui.navigation.Routes
import com.pixelflow.app.ui.theme.Mint
import com.pixelflow.app.ui.theme.OnSurface
import com.pixelflow.app.ui.theme.OnSurfaceVariant
import com.pixelflow.app.ui.theme.PrimarySecondary
import com.pixelflow.app.ui.theme.brandGradient
import kotlinx.coroutines.launch

@Composable
fun WelcomeScreen(nav: NavHostController) {
    val ctx = LocalContext.current
    val app = ctx.applicationContext as PixelFlowApp
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("welcome-screen")
    ) {
        PixelFlowTopBar(title = "PixelFlow", titleColor = PrimarySecondary)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Decorative illustration block (soft gradient card)
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(brush = brandGradient()),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Your Toolkit",
                    style = MaterialTheme.typography.headlineMedium,
                    color = androidx.compose.ui.graphics.Color.White
                )
            }

            Spacer(Modifier.height(28.dp))

            Text(
                text = "Your Images.\nYour Control.",
                style = MaterialTheme.typography.headlineLarge,
                color = OnSurface
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Experience high-speed image processing designed for privacy and professional results.",
                style = MaterialTheme.typography.bodyLarge,
                color = OnSurfaceVariant
            )
            Spacer(Modifier.height(24.dp))

            listOf(
                "No Ads",
                "No Watermarks",
                "No Login Required",
                "Fast Processing"
            ).forEachIndexed { idx, label ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .testTag("welcome-benefit-$idx"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(28.dp).clip(CircleShape).background(Mint),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Check, contentDescription = null, tint = PrimarySecondary, modifier = Modifier.size(18.dp))
                    }
                    Spacer(Modifier.size(14.dp))
                    Text(label, style = MaterialTheme.typography.titleMedium, color = OnSurface)
                }
            }

            Spacer(Modifier.height(32.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            PrimaryButton(
                text = "Continue",
                onClick = {
                    scope.launch {
                        app.onboardingPrefs.markOnboarded()
                        nav.navigate(Routes.HOME) {
                            popUpTo(Routes.WELCOME) { inclusive = true }
                        }
                    }
                },
                trailingArrow = true,
                testId = "welcome-continue-button"
            )
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable {
                        scope.launch {
                            app.onboardingPrefs.markOnboarded()
                            nav.navigate(Routes.HOME) {
                                popUpTo(Routes.WELCOME) { inclusive = true }
                            }
                        }
                    }
                    .testTag("welcome-skip-button"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Skip",
                    style = MaterialTheme.typography.titleMedium,
                    color = OnSurfaceVariant,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}
