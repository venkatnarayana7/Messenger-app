package com.pixelflow.app.domain.processing

import android.graphics.Bitmap
import com.pixelflow.app.domain.model.InterpolationMethod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ImageResizer {

    /**
     * Resize with the chosen interpolation method. Runs on Dispatchers.Default.
     * Absolutely NO watermark stamping — no code path may add one.
     */
    suspend fun resize(
        source: Bitmap,
        targetWidth: Int,
        targetHeight: Int,
        method: InterpolationMethod = InterpolationMethod.BILINEAR
    ): Bitmap = withContext(Dispatchers.Default) {
        Interpolation.resample(source, targetWidth, targetHeight, method)
    }
}
