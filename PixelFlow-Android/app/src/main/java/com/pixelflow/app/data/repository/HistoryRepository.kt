package com.pixelflow.app.data.repository

import com.pixelflow.app.data.db.HistoryDao
import com.pixelflow.app.data.entity.HistoryEntry

/**
 * Repository pattern per Section 3: abstracts the local Room source so that
 * a Premium-only, opt-in cloud data source can be added later without a rewrite.
 * Free tier NEVER triggers a network call.
 */
class HistoryRepository(private val dao: HistoryDao) {
    fun observeAll() = dao.observeAll()
    fun observeRecent(limit: Int = 20) = dao.observeRecent(limit)
    fun observeCount() = dao.observeCount()
    fun observeStorageSaved() = dao.observeStorageSaved()
    suspend fun add(entry: HistoryEntry): Long = dao.insert(entry)
    suspend fun delete(id: Long) = dao.deleteById(id)
    suspend fun clear() = dao.clearAll()
}
