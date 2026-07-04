package com.pixelflow.app.domain.processing

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import com.pixelflow.app.domain.model.OutputFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class ImageConverter {

    /**
     * Convert bitmap to target format bytes.
     * - Lossy targets (JPG/WEBP): honors quality (0..100).
     * - Non-alpha targets (JPG/BMP): composites onto white BEFORE encoding
     *   so transparent PNGs don't produce a black background (spec §5 Convert).
     *
     * Supported natively by Android:  JPG (JPEG), PNG, WEBP.
     * Emulated via encoded PNG bytes:  BMP, TIFF, GIF, HEIC, ICO
     *   (Android's Bitmap encoder cannot produce these; the caller should
     *    treat this as a "best-effort" fallback and clearly label it in UI.)
     */
    suspend fun convert(source: Bitmap, format: OutputFormat, quality: Int): ByteArray =
        withContext(Dispatchers.Default) {
            val prepared = if (!format.supportsAlpha) compositeOnWhite(source) else source
            val q = quality.coerceIn(0, 100)
            val out = ByteArrayOutputStream()
            when (format) {
                OutputFormat.JPG -> prepared.compress(Bitmap.CompressFormat.JPEG, q, out)
                OutputFormat.PNG -> prepared.compress(Bitmap.CompressFormat.PNG, 100, out)
                OutputFormat.WEBP -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        prepared.compress(Bitmap.CompressFormat.WEBP_LOSSY, q, out)
                    } else {
                        @Suppress("DEPRECATION")
                        prepared.compress(Bitmap.CompressFormat.WEBP, q, out)
                    }
                }
                // Formats Android's Bitmap encoder can't emit directly.
                // Falls back to PNG bytes but keeps the requested extension in the file name.
                OutputFormat.BMP, OutputFormat.TIFF, OutputFormat.GIF,
                OutputFormat.HEIC, OutputFormat.ICO ->
                    prepared.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            out.toByteArray()
        }

    private fun compositeOnWhite(src: Bitmap): Bitmap {
        if (!src.hasAlpha()) return src
        val out = Bitmap.createBitmap(src.width, src.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(out)
        canvas.drawColor(Color.WHITE)
        canvas.drawBitmap(src, 0f, 0f, null)
        return out
    }
}
