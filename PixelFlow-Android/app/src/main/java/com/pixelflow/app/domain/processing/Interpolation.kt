package com.pixelflow.app.domain.processing

import android.graphics.Bitmap
import android.graphics.Color
import com.pixelflow.app.domain.model.InterpolationMethod
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

/**
 * Pure-Kotlin implementations of the 4 permitted interpolation methods.
 *
 * ⚠️ NO AI / NO ML — Section 2 non-negotiable rule.
 * ⚠️ All routines operate on ARGB int arrays and are safe to call from
 *    Dispatchers.Default (they are CPU bound but never touch the main thread).
 */
object Interpolation {

    fun resample(src: Bitmap, dstW: Int, dstH: Int, method: InterpolationMethod): Bitmap {
        require(dstW > 0 && dstH > 0) { "Target dimensions must be positive." }
        if (dstW == src.width && dstH == src.height) return src.copy(Bitmap.Config.ARGB_8888, false)

        val srcW = src.width
        val srcH = src.height
        val srcPixels = IntArray(srcW * srcH)
        src.getPixels(srcPixels, 0, srcW, 0, 0, srcW, srcH)

        val dstPixels = when (method) {
            InterpolationMethod.NEAREST_NEIGHBOR -> nearest(srcPixels, srcW, srcH, dstW, dstH)
            InterpolationMethod.BILINEAR         -> bilinear(srcPixels, srcW, srcH, dstW, dstH)
            InterpolationMethod.BICUBIC          -> bicubic(srcPixels, srcW, srcH, dstW, dstH)
            InterpolationMethod.LANCZOS          -> lanczos(srcPixels, srcW, srcH, dstW, dstH, a = 3)
        }
        val out = Bitmap.createBitmap(dstW, dstH, Bitmap.Config.ARGB_8888)
        out.setPixels(dstPixels, 0, dstW, 0, 0, dstW, dstH)
        return out
    }

    // ---------- Nearest Neighbor ----------
    private fun nearest(src: IntArray, sw: Int, sh: Int, dw: Int, dh: Int): IntArray {
        val out = IntArray(dw * dh)
        val xRatio = sw.toDouble() / dw
        val yRatio = sh.toDouble() / dh
        for (y in 0 until dh) {
            val sy = (y * yRatio).toInt().coerceIn(0, sh - 1)
            val srcRow = sy * sw
            val dstRow = y * dw
            for (x in 0 until dw) {
                val sx = (x * xRatio).toInt().coerceIn(0, sw - 1)
                out[dstRow + x] = src[srcRow + sx]
            }
        }
        return out
    }

    // ---------- Bilinear ----------
    private fun bilinear(src: IntArray, sw: Int, sh: Int, dw: Int, dh: Int): IntArray {
        val out = IntArray(dw * dh)
        val xRatio = if (dw > 1) (sw - 1).toDouble() / (dw - 1) else 0.0
        val yRatio = if (dh > 1) (sh - 1).toDouble() / (dh - 1) else 0.0
        for (y in 0 until dh) {
            val gy = y * yRatio
            val y1 = floor(gy).toInt().coerceAtMost(sh - 1)
            val y2 = (y1 + 1).coerceAtMost(sh - 1)
            val fy = gy - y1
            for (x in 0 until dw) {
                val gx = x * xRatio
                val x1 = floor(gx).toInt().coerceAtMost(sw - 1)
                val x2 = (x1 + 1).coerceAtMost(sw - 1)
                val fx = gx - x1
                val p11 = src[y1 * sw + x1]; val p21 = src[y1 * sw + x2]
                val p12 = src[y2 * sw + x1]; val p22 = src[y2 * sw + x2]
                out[y * dw + x] = blend4(p11, p21, p12, p22, fx, fy)
            }
        }
        return out
    }

    private fun blend4(p11: Int, p21: Int, p12: Int, p22: Int, fx: Double, fy: Double): Int {
        val a = lerpChannel(p11 ushr 24 and 0xFF, p21 ushr 24 and 0xFF, p12 ushr 24 and 0xFF, p22 ushr 24 and 0xFF, fx, fy)
        val r = lerpChannel(p11 shr 16 and 0xFF, p21 shr 16 and 0xFF, p12 shr 16 and 0xFF, p22 shr 16 and 0xFF, fx, fy)
        val g = lerpChannel(p11 shr 8 and 0xFF,  p21 shr 8 and 0xFF,  p12 shr 8 and 0xFF,  p22 shr 8 and 0xFF,  fx, fy)
        val b = lerpChannel(p11 and 0xFF,        p21 and 0xFF,        p12 and 0xFF,        p22 and 0xFF,        fx, fy)
        return (a shl 24) or (r shl 16) or (g shl 8) or b
    }

    private fun lerpChannel(c11: Int, c21: Int, c12: Int, c22: Int, fx: Double, fy: Double): Int {
        val top = c11 + (c21 - c11) * fx
        val bot = c12 + (c22 - c12) * fx
        return (top + (bot - top) * fy).toInt().coerceIn(0, 255)
    }

    // ---------- Bicubic (Catmull-Rom, a = -0.5) ----------
    private fun bicubic(src: IntArray, sw: Int, sh: Int, dw: Int, dh: Int): IntArray {
        val out = IntArray(dw * dh)
        val xRatio = if (dw > 1) (sw - 1).toDouble() / (dw - 1) else 0.0
        val yRatio = if (dh > 1) (sh - 1).toDouble() / (dh - 1) else 0.0
        for (y in 0 until dh) {
            val gy = y * yRatio
            val yInt = floor(gy).toInt()
            val yFrac = gy - yInt
            for (x in 0 until dw) {
                val gx = x * xRatio
                val xInt = floor(gx).toInt()
                val xFrac = gx - xInt

                var a = 0.0; var r = 0.0; var g = 0.0; var b = 0.0
                for (m in -1..2) {
                    val py = (yInt + m).coerceIn(0, sh - 1)
                    val wy = cubicWeight(m - yFrac)
                    for (n in -1..2) {
                        val px = (xInt + n).coerceIn(0, sw - 1)
                        val wx = cubicWeight(n - xFrac)
                        val w = wx * wy
                        val c = src[py * sw + px]
                        a += w * (c ushr 24 and 0xFF)
                        r += w * (c shr 16 and 0xFF)
                        g += w * (c shr 8 and 0xFF)
                        b += w * (c and 0xFF)
                    }
                }
                out[y * dw + x] = Color.argb(
                    a.toInt().coerceIn(0, 255),
                    r.toInt().coerceIn(0, 255),
                    g.toInt().coerceIn(0, 255),
                    b.toInt().coerceIn(0, 255)
                )
            }
        }
        return out
    }

    /** Catmull-Rom cubic kernel with a = -0.5. */
    private fun cubicWeight(x: Double): Double {
        val ax = abs(x)
        val a = -0.5
        return when {
            ax <= 1.0 -> (a + 2) * ax * ax * ax - (a + 3) * ax * ax + 1
            ax <  2.0 -> a * ax * ax * ax - 5 * a * ax * ax + 8 * a * ax - 4 * a
            else -> 0.0
        }
    }

    // ---------- Lanczos-a (a = 3 by default) ----------
    private fun lanczos(src: IntArray, sw: Int, sh: Int, dw: Int, dh: Int, a: Int): IntArray {
        val out = IntArray(dw * dh)
        val xRatio = sw.toDouble() / dw
        val yRatio = sh.toDouble() / dh
        for (y in 0 until dh) {
            val gy = (y + 0.5) * yRatio - 0.5
            val yInt = floor(gy).toInt()
            for (x in 0 until dw) {
                val gx = (x + 0.5) * xRatio - 0.5
                val xInt = floor(gx).toInt()

                var wSum = 0.0
                var aSum = 0.0; var rSum = 0.0; var gSum = 0.0; var bSum = 0.0
                for (m in (1 - a)..a) {
                    val py = (yInt + m).coerceIn(0, sh - 1)
                    val wy = lanczosKernel(gy - (yInt + m), a)
                    if (wy == 0.0) continue
                    for (n in (1 - a)..a) {
                        val px = (xInt + n).coerceIn(0, sw - 1)
                        val wx = lanczosKernel(gx - (xInt + n), a)
                        if (wx == 0.0) continue
                        val w = wx * wy
                        val c = src[py * sw + px]
                        aSum += w * (c ushr 24 and 0xFF)
                        rSum += w * (c shr 16 and 0xFF)
                        gSum += w * (c shr 8 and 0xFF)
                        bSum += w * (c and 0xFF)
                        wSum += w
                    }
                }
                val inv = if (wSum == 0.0) 0.0 else 1.0 / wSum
                out[y * dw + x] = Color.argb(
                    (aSum * inv).toInt().coerceIn(0, 255),
                    (rSum * inv).toInt().coerceIn(0, 255),
                    (gSum * inv).toInt().coerceIn(0, 255),
                    (bSum * inv).toInt().coerceIn(0, 255)
                )
            }
        }
        return out
    }

    private fun lanczosKernel(x: Double, a: Int): Double {
        if (x == 0.0) return 1.0
        if (abs(x) >= a) return 0.0
        val px = PI * x
        return (a * sin(px) * sin(px / a)) / (px * px)
    }
}
