package com.pixelflow.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pixelflow.app.PixelFlowApp
import com.pixelflow.app.ui.screens.batch.BatchScreen
import com.pixelflow.app.ui.screens.compress.CompressScreen
import com.pixelflow.app.ui.screens.convert.ConvertScreen
import com.pixelflow.app.ui.screens.history.HistoryScreen
import com.pixelflow.app.ui.screens.home.HomeScreen
import com.pixelflow.app.ui.screens.premium.PremiumScreen
import com.pixelflow.app.ui.screens.resize.ResizeScreen
import com.pixelflow.app.ui.screens.settings.SettingsScreen
import com.pixelflow.app.ui.screens.splash.SplashScreen
import com.pixelflow.app.ui.screens.upscale.UpscaleScreen
import com.pixelflow.app.ui.screens.welcome.WelcomeScreen

@Composable
fun PixelFlowNavGraph() {
    val nav = rememberNavController()
    val app = LocalContext.current.applicationContext as PixelFlowApp
    val onboarded by app.onboardingPrefs.hasOnboarded.collectAsState(initial = null)

    // Wait until preference loads to avoid a Welcome flash on returning users.
    val start = when (onboarded) {
        null -> Routes.SPLASH
        true -> Routes.SPLASH   // Splash decides next based on flag
        false -> Routes.SPLASH
    }

    NavHost(navController = nav, startDestination = start) {
        composable(Routes.SPLASH)  { SplashScreen(nav, hasOnboarded = onboarded == true) }
        composable(Routes.WELCOME) { WelcomeScreen(nav) }
        composable(Routes.HOME)    { HomeScreen(nav) }
        composable(Routes.RESIZE)  { ResizeScreen(nav) }
        composable(Routes.COMPRESS){ CompressScreen(nav) }
        composable(Routes.UPSCALE) { UpscaleScreen(nav) }
        composable(Routes.CONVERT) { ConvertScreen(nav) }
        composable(Routes.BATCH)   { BatchScreen(nav) }
        composable(Routes.HISTORY) { HistoryScreen(nav) }
        composable(Routes.PREMIUM) { PremiumScreen(nav) }
        composable(Routes.SETTINGS){ SettingsScreen(nav) }
        composable(Routes.TOOLS)   { HomeScreen(nav) } // Tools tab -> Home dashboard (all 6 tools)
    }
}
