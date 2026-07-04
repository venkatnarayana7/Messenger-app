package com.pixelflow.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Single-row settings table. id is always 1. */
@Entity(tableName = "settings")
data class AppSettings(
    @PrimaryKey val id: Int = 1,
    val language: String = "en-US",
    /** Absolute path or content URI to the default output folder. Empty = system Pictures/PixelFlow. */
    val defaultOutputFolder: String = "",
    /** Default image quality (0..100) for lossy operations. */
    val defaultQuality: Int = 90,
    /** Premium entitlement flag — verified against Play Billing when that ships. */
    val isPremium: Boolean = false,
)
