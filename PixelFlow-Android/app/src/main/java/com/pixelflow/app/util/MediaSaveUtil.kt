package com.pixelflow.app.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * All image IO helpers. Uses MediaStore (scoped storage) on Q+, falls back to
 * legacy public storage on older APIs. NEVER makes network calls.
 */
object MediaSaveUtil {

    /** Decode a Uri to a Bitmap, respecting EXIF orientation on modern APIs. */
    suspend fun decodeBitmap(context: Context, uri: Uri): Bitmap = withContext(Dispatchers.IO) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val src = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(src) { decoder, _, _ ->
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                decoder.isMutableRequired = true
            }
        } else {
            context.contentResolver.openInputStream(uri).use { input ->
                requireNotNull(input) { "Cannot open $uri" }
                BitmapFactory.decodeStream(input)
                    ?: error("Unsupported or corrupted image.")
            }
        }
    }

    /** Read only image dimensions without loading full pixels. */
    suspend fun readDimensions(context: Context, uri: Uri): Pair<Int, Int> = withContext(Dispatchers.IO) {
        val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri).use { s ->
            BitmapFactory.decodeStream(s, null, opts)
        }
        opts.outWidth to opts.outHeight
    }

    /** File size in bytes for a content Uri, or -1 if unknown. */
    suspend fun getSize(context: Context, uri: Uri): Long = withContext(Dispatchers.IO) {
        runCatching {
            context.contentResolver.openFileDescriptor(uri, "r")?.use { it.statSize } ?: -1L
        }.getOrDefault(-1L)
    }

    /**
     * Save encoded bytes as an image in Pictures/PixelFlow (public gallery folder).
     * Returns the content Uri of the saved file, or null on failure.
     */
    suspend fun saveToGallery(
        context: Context,
        bytes: ByteArray,
        displayName: String,
        mimeType: String
    ): Uri? = withContext(Dispatchers.IO) {
        val folder = "PixelFlow"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, displayName)
                put(MediaStore.Images.Media.MIME_TYPE, mimeType)
                put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/$folder")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
            val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            val uri = context.contentResolver.insert(collection, values) ?: return@withContext null
            context.contentResolver.openOutputStream(uri)?.use { it.write(bytes) }
            values.clear()
            values.put(MediaStore.Images.Media.IS_PENDING, 0)
            context.contentResolver.update(uri, values, null, null)
            uri
        } else {
            @Suppress("DEPRECATION")
            val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val outFolder = File(picturesDir, folder).apply { if (!exists()) mkdirs() }
            val file = File(outFolder, displayName)
            FileOutputStream(file).use { it.write(bytes) }
            Uri.fromFile(file)
        }
    }
}
