package com.pixelflow.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** History entry — LOCAL ONLY. Never leaves the device (Section 2 / 3). */
@Entity(tableName = "history")
data class HistoryEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val filename: String,
    /** Absolute path OR content:// URI string of the saved output. */
    val outputUri: String,
    /** Small on-device thumbnail path (PNG). Nullable if not generated. */
    val thumbnailPath: String? = null,
    /** e.g. "RESIZE", "COMPRESS", "CONVERT", "UPSCALE", "BATCH". */
    val operation: String,
    val outputSizeBytes: Long,
    val originalSizeBytes: Long,
    val createdAt: Long = System.currentTimeMillis(),
    /** Free-form details, e.g. "1920x1080 → 1080x1080 · Bilinear". */
    val details: String = "",
)
