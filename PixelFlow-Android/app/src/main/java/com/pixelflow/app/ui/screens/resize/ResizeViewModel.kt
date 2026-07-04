package com.pixelflow.app.ui.screens.resize

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pixelflow.app.PixelFlowApp
import com.pixelflow.app.data.entity.HistoryEntry
import com.pixelflow.app.domain.model.InterpolationMethod
import com.pixelflow.app.domain.model.Operation
import com.pixelflow.app.util.FormatUtil
import com.pixelflow.app.util.MediaSaveUtil
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

data class ResizeUiState(
    val sourceUri: Uri? = null,
    val originalWidth: Int = 0,
    val originalHeight: Int = 0,
    val originalSize: Long = 0,
    val width: Int = 0,
    val height: Int = 0,
    val lockAspect: Boolean = true,
    val scalePercent: Int = 100,
    val method: InterpolationMethod = InterpolationMethod.BILINEAR,
    val processing: Boolean = false,
    val savedUri: Uri? = null,
    val error: String? = null,
)

class ResizeViewModel(app: Application) : AndroidViewModel(app) {
    private val appCtx = app.applicationContext
    private val container = app as PixelFlowApp

    private val _state = MutableStateFlow(ResizeUiState())
    val state = _state.asStateFlow()

    private var sourceBitmap: Bitmap? = null
    private var previewJob: Job? = null

    fun loadImage(uri: Uri) {
        viewModelScope.launch {
            runCatching {
                val bmp = MediaSaveUtil.decodeBitmap(appCtx, uri)
                sourceBitmap = bmp
                val size = MediaSaveUtil.getSize(appCtx, uri).coerceAtLeast(0)
                _state.value = _state.value.copy(
                    sourceUri = uri,
                    originalWidth = bmp.width,
                    originalHeight = bmp.height,
                    originalSize = size,
                    width = bmp.width,
                    height = bmp.height,
                    scalePercent = 100,
                    savedUri = null,
                    error = null,
                )
            }.onFailure {
                _state.value = _state.value.copy(error = "This image can't be opened.")
            }
        }
    }

    fun setWidth(newW: Int) {
        val s = _state.value
        val w = newW.coerceAtLeast(1)
        val h = if (s.lockAspect && s.originalWidth > 0)
            ((w.toDouble() / s.originalWidth) * s.originalHeight).toInt().coerceAtLeast(1)
        else s.height
        val pct = if (s.originalWidth > 0) ((w * 100.0) / s.originalWidth).toInt() else s.scalePercent
        _state.value = s.copy(width = w, height = h, scalePercent = pct)
    }

    fun setHeight(newH: Int) {
        val s = _state.value
        val h = newH.coerceAtLeast(1)
        val w = if (s.lockAspect && s.originalHeight > 0)
            ((h.toDouble() / s.originalHeight) * s.originalWidth).toInt().coerceAtLeast(1)
        else s.width
        val pct = if (s.originalHeight > 0) ((h * 100.0) / s.originalHeight).toInt() else s.scalePercent
        _state.value = s.copy(width = w, height = h, scalePercent = pct)
    }

    fun setPercent(pct: Int) {
        val s = _state.value
        val clamped = pct.coerceIn(1, 400)
        val w = ((s.originalWidth * clamped) / 100).coerceAtLeast(1)
        val h = ((s.originalHeight * clamped) / 100).coerceAtLeast(1)
        _state.value = s.copy(scalePercent = clamped, width = w, height = h)
    }

    fun toggleLock(locked: Boolean) {
        _state.value = _state.value.copy(lockAspect = locked)
    }

    fun applyPreset(w: Int, h: Int) {
        _state.value = _state.value.copy(width = w, height = h, lockAspect = false,
            scalePercent = if (_state.value.originalWidth > 0) (w * 100 / _state.value.originalWidth) else 100)
    }

    fun setMethod(m: InterpolationMethod) { _state.value = _state.value.copy(method = m) }

    fun processAndSave() {
        val s = _state.value
        val src = sourceBitmap ?: return
        previewJob?.cancel()
        previewJob = viewModelScope.launch {
            _state.value = s.copy(processing = true, error = null)
            runCatching {
                delay(50) // let UI show spinner
                val out = container.resizer.resize(src, s.width, s.height, s.method)
                val jpg = ByteArrayOutputStream().apply { out.compress(Bitmap.CompressFormat.PNG, 100, this) }.toByteArray()
                val name = FormatUtil.timestampFileName("pixelflow_resize", "png")
                val saved = MediaSaveUtil.saveToGallery(appCtx, jpg, name, "image/png")
                    ?: error("Couldn't save to Pictures/PixelFlow.")
                container.historyRepo.add(
                    HistoryEntry(
                        filename = name,
                        outputUri = saved.toString(),
                        operation = Operation.RESIZE,
                        outputSizeBytes = jpg.size.toLong(),
                        originalSizeBytes = s.originalSize,
                        details = "${s.originalWidth}×${s.originalHeight} → ${s.width}×${s.height} · ${s.method.displayName}",
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
