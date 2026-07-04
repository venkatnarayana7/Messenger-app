package com.pixelflow.app.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.onboardingStore by preferencesDataStore("onboarding")

/** Small lightweight flag store, only used for the one-time onboarding wall. */
class OnboardingPrefs(private val context: Context) {
    private val KEY = booleanPreferencesKey("has_onboarded")

    val hasOnboarded: Flow<Boolean> = context.onboardingStore.data.map { it[KEY] ?: false }

    suspend fun markOnboarded() {
        context.onboardingStore.edit { it[KEY] = true }
    }
}
