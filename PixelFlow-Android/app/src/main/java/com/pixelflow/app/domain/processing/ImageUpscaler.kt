package com.pixelflow.app.domain.processing

import android.graphics.Bitmap
import com.pixelflow.app.domain.model.InterpolationMethod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ImageUpscaler {

    /**
     * Upscale by an integer factor (2, 4, 8) using classical interpolation only.
     * Explicit reminder: NO AI, NO neural upscaling — Section 2 non-negotiable.
     *
     * Emits progress fractions [0.0..1.0] via [onProgress] for the UI's real
     * progress indicator (§5 Upscale: "show real progress, not a fake bar").
     */
    suspend fun upscale(
        source: Bitmap,
        factor: Int,
        method: InterpolationMethod,
        onProgress: (Float) -> Unit = {}
    ): Bitmap = withContext(Dispatchers.Default) {
        require(factor in intArrayOf(2, 4, 8)) { "Scale factor must be 2, 4 or 8." }
        onProgress(0.05f)
        val dstW = source.width * factor
        val dstH = source.height * factor
        // For very large targets and Lanczos, chunk into two 2x passes so we can
        // stream progress and keep peak memory lower.
        val result = if (factor == 8) {
            val mid = Interpolation.resample(source, source.width * 2, source.height * 2, method)
            onProgress(0.35f)
            val mid2 = Interpolation.resample(mid, source.width * 4, source.height * 4, method)
            onProgress(0.7f)
            val out = Interpolation.resample(mid2, dstW, dstH, method)
            onProgress(1f)
            out
        } else if (factor == 4) {
            val mid = Interpolation.resample(source, source.width * 2, source.height * 2, method)
            onProgress(0.5f)
            val out = Interpolation.resample(mid, dstW, dstH, method)
            onProgress(1f)
            out
        } else {
            onProgress(0.4f)
            val out = Interpolation.resample(source, dstW, dstH, method)
            onProgress(1f)
            out
        }
        result
    }
}
