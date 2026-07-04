package com.pixelflow.app.domain.processing

import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class ImageCompressor {

    /**
     * Compress to JPEG at the given quality (0..100). Returns encoded bytes.
     * True compression call (not a heuristic) so estimated size = actual size.
     */
    suspend fun compressJpeg(source: Bitmap, quality: Int): ByteArray = withContext(Dispatchers.Default) {
        val q = quality.coerceIn(0, 100)
        val out = ByteArrayOutputStream()
        // JPEG has no alpha — flatten if needed happens at save time via composite-on-white.
        source.compress(Bitmap.CompressFormat.JPEG, q, out)
        out.toByteArray()
    }

    /** Fast size estimate (compresses to a temp stream). Called from the ViewModel with debounce. */
    suspend fun estimateJpegSize(source: Bitmap, quality: Int): Long = withContext(Dispatchers.Default) {
        val q = quality.coerceIn(0, 100)
        // Downsample for estimation to keep the slider responsive.
        val estBitmap = if (source.width > 512 || source.height > 512) {
            val ratio = 512.0 / maxOf(source.width, source.height)
            Interpolation.resample(
                source,
                (source.width * ratio).toInt().coerceAtLeast(1),
                (source.height * ratio).toInt().coerceAtLeast(1),
                com.pixelflow.app.domain.model.InterpolationMethod.BILINEAR
            )
        } else source
        val out = ByteArrayOutputStream()
        estBitmap.compress(Bitmap.CompressFormat.JPEG, q, out)
        val estBytes = out.size().toLong()
        // Scale up estimate proportionally to full resolution
        val pxRatio = (source.width.toLong() * source.height) /
            maxOf(1L, (estBitmap.width.toLong() * estBitmap.height))
        estBytes * pxRatio
    }
}
