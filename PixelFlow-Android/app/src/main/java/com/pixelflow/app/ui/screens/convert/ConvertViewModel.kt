package com.pixelflow.app.ui.screens.convert

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pixelflow.app.PixelFlowApp
import com.pixelflow.app.data.entity.HistoryEntry
import com.pixelflow.app.domain.model.Operation
import com.pixelflow.app.domain.model.OutputFormat
import com.pixelflow.app.util.FormatUtil
import com.pixelflow.app.util.MediaSaveUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ConvertUiState(
    val sourceUri: Uri? = null,
    val sourceFormat: String = "",
    val originalSize: Long = 0,
    val target: OutputFormat = OutputFormat.JPG,
    val quality: Int = 85,
    val preserveExif: Boolean = true,
    val hasAlpha: Boolean = false,
    val showAlphaWarning: Boolean = false,
    val processing: Boolean = false,
    val savedUri: Uri? = null,
    val error: String? = null,
)

class ConvertViewModel(app: Application) : AndroidViewModel(app) {
    private val ctx = app.applicationContext
    private val container = app as PixelFlowApp

    private val _state = MutableStateFlow(ConvertUiState())
    val state = _state.asStateFlow()
    private var source: Bitmap? = null

    fun loadImage(uri: Uri) {
        viewModelScope.launch {
            runCatching {
                val bmp = MediaSaveUtil.decodeBitmap(ctx, uri)
                source = bmp
                val originalSize = MediaSaveUtil.getSize(ctx, uri).coerceAtLeast(0)
                val mime = ctx.contentResolver.getType(uri) ?: "image/*"
                _state.value = _state.value.copy(
                    sourceUri = uri,
                    sourceFormat = mime.substringAfter("/").uppercase(),
                    originalSize = originalSize,
                    hasAlpha = bmp.hasAlpha(),
                    showAlphaWarning = bmp.hasAlpha() && !_state.value.target.supportsAlpha,
                    savedUri = null,
                    error = null,
                )
            }.onFailure { _state.value = _state.value.copy(error = "This image can't be opened.") }
        }
    }

    fun setTarget(f: OutputFormat) {
        val warn = _state.value.hasAlpha && !f.supportsAlpha
        _state.value = _state.value.copy(target = f, showAlphaWarning = warn)
    }
    fun setQuality(q: Int) { _state.value = _state.value.copy(quality = q.coerceIn(0, 100)) }
    fun togglePreserveExif(v: Boolean) { _state.value = _state.value.copy(preserveExif = v) }

    fun convertAndSave() {
        val bmp = source ?: return
        viewModelScope.launch {
            _state.value = _state.value.copy(processing = true, error = null)
            runCatching {
                val fmt = _state.value.target
                val bytes = container.converter.convert(bmp, fmt, _state.value.quality)
                val name = FormatUtil.timestampFileName("pixelflow_convert", fmt.ext)
                val saved = MediaSaveUtil.saveToGallery(ctx, bytes, name, fmt.mime)
                    ?: error("Couldn't save to Pictures/PixelFlow.")
                container.historyRepo.add(
                    HistoryEntry(
                        filename = name,
                        outputUri = saved.toString(),
                        operation = Operation.CONVERT,
                        outputSizeBytes = bytes.size.toLong(),
                        originalSizeBytes = _state.value.originalSize,
                        details = "${_state.value.sourceFormat} → ${fmt.displayName}",
                    )
                )
                saved
            }.onSuccess { _state.value = _state.value.copy(processing = false, savedUri = it) }
                .onFailure { _state.value = _state.value.copy(processing = false, error = it.message ?: "Something went wrong.") }
        }
    }
}
