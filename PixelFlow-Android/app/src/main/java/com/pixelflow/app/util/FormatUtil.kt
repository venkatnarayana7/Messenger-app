package com.pixelflow.app.util

import java.util.Locale

object FormatUtil {

    fun formatBytes(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        var value = bytes.toDouble()
        var unit = 0
        while (value >= 1024 && unit < units.lastIndex) {
            value /= 1024
            unit++
        }
        return if (value >= 100 || unit == 0) String.format(Locale.US, "%.0f %s", value, units[unit])
        else String.format(Locale.US, "%.1f %s", value, units[unit])
    }

    fun percentSaved(original: Long, output: Long): Int {
        if (original <= 0) return 0
        val saved = ((original - output).toDouble() / original) * 100.0
        return saved.toInt().coerceIn(0, 99)
    }

    fun timestampFileName(prefix: String, ext: String): String {
        val ts = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(java.util.Date())
        return "${prefix}_${ts}.${ext}"
    }
}
