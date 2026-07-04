package com.pixelflow.app.data.repository

import com.pixelflow.app.data.db.SettingsDao
import com.pixelflow.app.data.entity.AppSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository(private val dao: SettingsDao) {
    fun observe(): Flow<AppSettings> = dao.observe().map { it ?: AppSettings() }
    suspend fun save(settings: AppSettings) = dao.upsert(settings)
}
