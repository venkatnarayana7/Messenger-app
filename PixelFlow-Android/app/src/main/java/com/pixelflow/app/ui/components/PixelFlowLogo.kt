package com.pixelflow.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import com.pixelflow.app.ui.theme.brandGradient

/**
 * The gradient tile with a white sparkle mark used on Splash + launcher.
 */
@Composable
fun PixelFlowLogo(sizeDp: Int = 96) {
    Box(
        modifier = Modifier
            .size(sizeDp.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(brush = brandGradient()),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size((sizeDp * 0.55f).dp)) {
            val w = size.width
            val h = size.height
            // Big 4-point sparkle (star)
            val star1 = Path().apply {
                moveTo(w * 0.35f, h * 0.15f)
                lineTo(w * 0.5f,  h * 0.44f)
                lineTo(w * 0.85f, h * 0.5f)
                lineTo(w * 0.5f,  h * 0.56f)
                lineTo(w * 0.35f, h * 0.85f)
                lineTo(w * 0.2f,  h * 0.56f)
                lineTo(w * 0.05f, h * 0.5f)
                lineTo(w * 0.2f,  h * 0.44f)
                close()
            }
            drawPath(star1, color = Color.White)
            // Small secondary sparkle
            val star2 = Path().apply {
                moveTo(w * 0.75f, h * 0.05f)
                lineTo(w * 0.83f, h * 0.22f)
                lineTo(w * 1.00f, h * 0.28f)
                lineTo(w * 0.83f, h * 0.34f)
                lineTo(w * 0.75f, h * 0.5f)
                lineTo(w * 0.67f, h * 0.34f)
                lineTo(w * 0.5f,  h * 0.28f)
                lineTo(w * 0.67f, h * 0.22f)
                close()
            }
            drawPath(star2, color = Color.White.copy(alpha = 0.9f))
        }
    }
}
