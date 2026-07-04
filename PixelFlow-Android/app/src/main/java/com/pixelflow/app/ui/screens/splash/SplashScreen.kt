package com.pixelflow.app.ui.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.pixelflow.app.ui.components.PixelFlowLogo
import com.pixelflow.app.ui.navigation.Routes
import com.pixelflow.app.ui.theme.AppBackground
import com.pixelflow.app.ui.theme.Mint
import com.pixelflow.app.ui.theme.OnSurface
import com.pixelflow.app.ui.theme.OnSurfaceVariant
import com.pixelflow.app.ui.theme.PrimarySecondary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(nav: NavHostController, hasOnboarded: Boolean) {
    LaunchedEffect(hasOnboarded) {
        delay(1200)
        nav.navigate(if (hasOnboarded) Routes.HOME else Routes.WELCOME) {
            popUpTo(Routes.SPLASH) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Mint.copy(alpha = 0.35f), AppBackground, Mint.copy(alpha = 0.35f))
                )
            )
            .testTag("splash-screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth().padding(20.dp)
        ) {
            PixelFlowLogo(sizeDp = 104)
            Spacer(Modifier.height(28.dp))
            Text(
                text = "PixelFlow",
                style = MaterialTheme.typography.headlineMedium,
                color = OnSurface
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "FAST · PRIVATE · NO WATERMARKS",
                style = MaterialTheme.typography.labelLarge,
                color = OnSurfaceVariant
            )
        }
        // Bottom progress hairline
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.fillMaxSize().padding(bottom = 32.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(64.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(PrimarySecondary)
            )
            Spacer(Modifier.height(10.dp))
            Text(
                "Secure Connection Established",
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariant
            )
        }
        Spacer(Modifier.size(0.dp))
    }
}
