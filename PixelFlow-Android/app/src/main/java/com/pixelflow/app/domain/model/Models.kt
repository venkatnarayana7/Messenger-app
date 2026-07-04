package com.pixelflow.app.domain.model

/** The 4 permitted upscaling algorithms. NO AI, NO ML — Section 2 non-negotiable. */
enum class InterpolationMethod(val displayName: String, val description: String) {
    NEAREST_NEIGHBOR("Nearest Neighbor", "Fastest, blockier."),
    BILINEAR("Bilinear", "Fast, softer edges."),
    BICUBIC("Bicubic", "Balanced quality and speed."),
    LANCZOS("Lanczos", "Sharpest detail, slower."),
}

enum class OutputFormat(val displayName: String, val lossy: Boolean, val supportsAlpha: Boolean, val mime: String, val ext: String) {
    JPG("JPG",  true,  false, "image/jpeg", "jpg"),
    PNG("PNG",  false, true,  "image/png",  "png"),
    WEBP("WEBP", true, true,  "image/webp", "webp"),
    BMP("BMP",  false, false, "image/bmp",  "bmp"),
    TIFF("TIFF", false, true, "image/tiff", "tiff"),
    GIF("GIF",  false, true,  "image/gif",  "gif"),
    HEIC("HEIC", true, true,  "image/heic", "heic"),
    ICO("ICO",  false, true,  "image/vnd.microsoft.icon", "ico"),
}

/** Preset canvas sizes shown on the Resize screen. */
data class ResizePreset(val label: String, val width: Int, val height: Int) {
    companion object {
        val ALL = listOf(
            ResizePreset("Instagram", 1080, 1080),
            ResizePreset("Facebook", 1200, 630),
            ResizePreset("YouTube",  1280, 720),
            ResizePreset("WhatsApp", 500, 500),
            ResizePreset("HD",       1280, 720),
            ResizePreset("4K",       3840, 2160),
        )
    }
}

/** Operation labels used in History rows. */
object Operation {
    const val RESIZE = "RESIZE"
    const val COMPRESS = "COMPRESS"
    const val CONVERT = "CONVERT"
    const val UPSCALE = "UPSCALE"
    const val BATCH = "BATCH"
}

/** Free-tier caps. Premium removes these — never gates a core tool. */
object FreeTierLimits {
    const val BATCH_MAX_FILES = 10
    /** Max single-file size (bytes) allowed on free tier. 25MB. Premium: unlimited. */
    const val MAX_FILE_SIZE_BYTES: Long = 25L * 1024L * 1024L
}
