package com.pixelflow.app.ui.screens.batch

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pixelflow.app.PixelFlowApp
import com.pixelflow.app.data.entity.HistoryEntry
import com.pixelflow.app.domain.model.FreeTierLimits
import com.pixelflow.app.domain.model.InterpolationMethod
import com.pixelflow.app.domain.model.Operation
import com.pixelflow.app.util.FormatUtil
import com.pixelflow.app.util.MediaSaveUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

enum class BatchItemStatus { QUEUED, PROCESSING, DONE, FAILED }

data class BatchItem(
    val uri: Uri,
    val name: String,
    val status: BatchItemStatus = BatchItemStatus.QUEUED,
    val progress: Float = 0f,
    val error: String? = null,
    val outputUri: Uri? = null,
    val outputBytes: Long = 0,
)

data class BatchUiState(
    val items: List<BatchItem> = emptyList(),
    val overallProgress: Float = 0f,
    val running: Boolean = false,
    val applySameSettings: Boolean = true,
    val quality: Int = 85,
    val isPremium: Boolean = false,
    val error: String? = null,
) {
    val completedCount: Int get() = items.count { it.status == BatchItemStatus.DONE }
    val remainingCount: Int get() = items.count { it.status != BatchItemStatus.DONE && it.status != BatchItemStatus.FAILED }
}

class BatchViewModel(app: Application) : AndroidViewModel(app) {
    private val ctx = app.applicationContext
    private val container = app as PixelFlowApp

    private val _state = MutableStateFlow(BatchUiState())
    val state = _state.asStateFlow()

    fun addImages(uris: List<Uri>) {
        val cap = if (_state.value.isPremium) Int.MAX_VALUE else FreeTierLimits.BATCH_MAX_FILES
        val allowed = uris.take((cap - _state.value.items.size).coerceAtLeast(0))
        val newItems = allowed.map { BatchItem(it, extractName(it)) }
        _state.value = _state.value.copy(items = _state.value.items + newItems)
    }

    fun removeItem(uri: Uri) {
        _state.value = _state.value.copy(items = _state.value.items.filterNot { it.uri == uri })
    }

    fun toggleApplySameSettings(v: Boolean) { _state.value = _state.value.copy(applySameSettings = v) }
    fun setQuality(q: Int) { _state.value = _state.value.copy(quality = q.coerceIn(0, 100)) }

    /** Sequential processing per Section 6 (Runtime Performance): controlled pool, no memory spike. */
    fun runBatchCompress() {
        if (_state.value.running || _state.value.items.isEmpty()) return
        viewModelScope.launch {
            _state.value = _state.value.copy(running = true, error = null)
            val list = _state.value.items.toMutableList()
            for (i in list.indices) {
                list[i] = list[i].copy(status = BatchItemStatus.PROCESSING, progress = 0f)
                _state.value = _state.value.copy(items = list.toList())
                runCatching {
                    val bmp = MediaSaveUtil.decodeBitmap(ctx, list[i].uri)
                    val bytes = container.compressor.compressJpeg(bmp, _state.value.quality)
                    val name = FormatUtil.timestampFileName("pixelflow_batch_${i + 1}", "jpg")
                    val saved = MediaSaveUtil.saveToGallery(ctx, bytes, name, "image/jpeg")
                        ?: error("Couldn't save.")
                    container.historyRepo.add(
                        HistoryEntry(
                            filename = name,
                            outputUri = saved.toString(),
                            operation = Operation.BATCH,
                            outputSizeBytes = bytes.size.toLong(),
                            originalSizeBytes = MediaSaveUtil.getSize(ctx, list[i].uri).coerceAtLeast(0),
                            details = "Batch compress · Q=${_state.value.quality}"
                        )
                    )
                    saved to bytes.size.toLong()
                }.onSuccess { (uri, size) ->
                    list[i] = list[i].copy(
                        status = BatchItemStatus.DONE, progress = 1f,
                        outputUri = uri, outputBytes = size
                    )
                }.onFailure { e ->
                    list[i] = list[i].copy(status = BatchItemStatus.FAILED, error = e.message)
                }
                val doneOrFailed = list.count {
                    it.status == BatchItemStatus.DONE || it.status == BatchItemStatus.FAILED
                }
                _state.value = _state.value.copy(
                    items = list.toList(),
                    overallProgress = doneOrFailed.toFloat() / list.size
                )
            }
            _state.value = _state.value.copy(running = false)
        }
    }

    private fun extractName(uri: Uri): String =
        runCatching {
            ctx.contentResolver.query(uri, arrayOf(android.provider.OpenableColumns.DISPLAY_NAME), null, null, null)
                ?.use { c ->
                    if (c.moveToFirst()) c.getString(0) else uri.lastPathSegment ?: "image"
                }
        }.getOrNull() ?: (uri.lastPathSegment ?: "image")
}

/** Unused byte-writer kept as a hook for future in-memory zip export (Section 5: "Download all"). */
@Suppress("unused")
private fun ByteArray.copyOut(out: ByteArrayOutputStream) { out.write(this) }
