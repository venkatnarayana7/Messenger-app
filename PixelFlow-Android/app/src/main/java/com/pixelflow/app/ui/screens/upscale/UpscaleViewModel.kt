package com.pixelflow.app.ui.screens.upscale

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

data class UpscaleUiState(
    val sourceUri: Uri? = null,
    val originalSize: Long = 0,
    val factor: Int = 2,
    val method: InterpolationMethod = InterpolationMethod.LANCZOS,
    val progress: Float = 0f,
    val processing: Boolean = false,
    val savedUri: Uri? = null,
    val error: String? = null,
)

class UpscaleViewModel(app: Application) : AndroidViewModel(app) {
    private val ctx = app.applicationContext
    private val container = app as PixelFlowApp

    private val _state = MutableStateFlow(UpscaleUiState())
    val state = _state.asStateFlow()
    private var source: Bitmap? = null

    fun loadImage(uri: Uri) {
        viewModelScope.launch {
            runCatching {
                val bmp = MediaSaveUtil.decodeBitmap(ctx, uri)
                source = bmp
                val originalSize = MediaSaveUtil.getSize(ctx, uri).coerceAtLeast(0)
                _state.value = _state.value.copy(sourceUri = uri, originalSize = originalSize,
                    savedUri = null, error = null, progress = 0f)
            }.onFailure { _state.value = _state.value.copy(error = "This image can't be opened.") }
        }
    }

    fun setFactor(f: Int) { _state.value = _state.value.copy(factor = f) }
    fun setMethod(m: InterpolationMethod) { _state.value = _state.value.copy(method = m) }

    fun runUpscale() {
        val bmp = source ?: return
        viewModelScope.launch {
            _state.value = _state.value.copy(processing = true, error = null, progress = 0f)
            runCatching {
                val out = container.upscaler.upscale(bmp, _state.value.factor, _state.value.method) { p ->
                    _state.value = _state.value.copy(progress = p)
                }
                val bytes = ByteArrayOutputStream().apply {
                    out.compress(Bitmap.CompressFormat.PNG, 100, this)
                }.toByteArray()
                val name = FormatUtil.timestampFileName("pixelflow_upscale_${_state.value.factor}x", "png")
                val saved = MediaSaveUtil.saveToGallery(ctx, bytes, name, "image/png")
                    ?: error("Couldn't save to Pictures/PixelFlow.")
                container.historyRepo.add(
                    HistoryEntry(
                        filename = name,
                        outputUri = saved.toString(),
                        operation = Operation.UPSCALE,
                        outputSizeBytes = bytes.size.toLong(),
                        originalSizeBytes = _state.value.originalSize,
                        details = "${_state.value.factor}× · ${_state.value.method.displayName}",
                    )
                )
                saved
            }.onSuccess {
                _state.value = _state.value.copy(processing = false, savedUri = it, progress = 1f)
            }.onFailure {
                _state.value = _state.value.copy(processing = false, error = it.message ?: "Something went wrong.")
            }
        }
    }
}
