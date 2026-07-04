package com.pixelflow.app.ui.screens.compress

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pixelflow.app.PixelFlowApp
import com.pixelflow.app.data.entity.HistoryEntry
import com.pixelflow.app.domain.model.Operation
import com.pixelflow.app.util.FormatUtil
import com.pixelflow.app.util.MediaSaveUtil
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CompressUiState(
    val sourceUri: Uri? = null,
    val originalSize: Long = 0,
    val quality: Int = 85,
    val estimatedSize: Long = 0,
    val processing: Boolean = false,
    val savedUri: Uri? = null,
    val error: String? = null,
)

class CompressViewModel(app: Application) : AndroidViewModel(app) {
    private val appCtx = app.applicationContext
    private val container = app as PixelFlowApp

    private val _state = MutableStateFlow(CompressUiState())
    val state = _state.asStateFlow()
    private var source: Bitmap? = null
    private var estimateJob: Job? = null

    fun loadImage(uri: Uri) {
        viewModelScope.launch {
            runCatching {
                val bmp = MediaSaveUtil.decodeBitmap(appCtx, uri)
                source = bmp
                val originalSize = MediaSaveUtil.getSize(appCtx, uri).coerceAtLeast(0)
                _state.value = _state.value.copy(sourceUri = uri, originalSize = originalSize,
                    savedUri = null, error = null)
                recomputeEstimate()
            }.onFailure {
                _state.value = _state.value.copy(error = "This image can't be opened.")
            }
        }
    }

    fun setQuality(q: Int) {
        _state.value = _state.value.copy(quality = q.coerceIn(0, 100))
        estimateJob?.cancel()
        estimateJob = viewModelScope.launch {
            delay(120) // debounce (Section 6: no jank on every drag pixel)
            recomputeEstimate()
        }
    }

    private suspend fun recomputeEstimate() {
        val bmp = source ?: return
        val est = container.compressor.estimateJpegSize(bmp, _state.value.quality)
        _state.value = _state.value.copy(estimatedSize = est)
    }

    fun qualityLabel(): String = when (_state.value.quality) {
        in 0..30 -> "Low"
        in 31..60 -> "Medium"
        in 61..85 -> "High"
        else -> "Best"
    }

    fun compressAndSave() {
        val bmp = source ?: return
        viewModelScope.launch {
            _state.value = _state.value.copy(processing = true, error = null)
            runCatching {
                val bytes = container.compressor.compressJpeg(bmp, _state.value.quality)
                val name = FormatUtil.timestampFileName("pixelflow_compress", "jpg")
                val saved = MediaSaveUtil.saveToGallery(appCtx, bytes, name, "image/jpeg")
                    ?: error("Couldn't save to Pictures/PixelFlow.")
                container.historyRepo.add(
                    HistoryEntry(
                        filename = name,
                        outputUri = saved.toString(),
                        operation = Operation.COMPRESS,
                        outputSizeBytes = bytes.size.toLong(),
                        originalSizeBytes = _state.value.originalSize,
                        details = "Quality ${_state.value.quality}%",
                    )
                )
                saved
            }.onSuccess {
                _state.value = _state.value.copy(processing = false, savedUri = it)
            }.onFailure {
                _state.value = _state.value.copy(processing = false, error = it.message ?: "Something went wrong.")
            }
        }
    }
}
