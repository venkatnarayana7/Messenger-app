package com.pixelflow.app

import android.app.Application
import com.pixelflow.app.data.db.PixelFlowDatabase
import com.pixelflow.app.data.prefs.OnboardingPrefs
import com.pixelflow.app.data.repository.HistoryRepository
import com.pixelflow.app.data.repository.SettingsRepository
import com.pixelflow.app.domain.processing.ImageCompressor
import com.pixelflow.app.domain.processing.ImageConverter
import com.pixelflow.app.domain.processing.ImageResizer
import com.pixelflow.app.domain.processing.ImageUpscaler

/**
 * Manual DI container. We deliberately avoid Hilt/Dagger to keep the APK small
 * and startup fast — per Section 6 (App Size & Startup) of the spec.
 */
class PixelFlowApp : Application() {

    val database: PixelFlowDatabase by lazy { PixelFlowDatabase.get(this) }
    val historyRepo: HistoryRepository by lazy { HistoryRepository(database.historyDao()) }
    val settingsRepo: SettingsRepository by lazy { SettingsRepository(database.settingsDao()) }
    val onboardingPrefs: OnboardingPrefs by lazy { OnboardingPrefs(this) }

    val resizer: ImageResizer by lazy { ImageResizer() }
    val compressor: ImageCompressor by lazy { ImageCompressor() }
    val converter: ImageConverter by lazy { ImageConverter() }
    val upscaler: ImageUpscaler by lazy { ImageUpscaler() }
}
