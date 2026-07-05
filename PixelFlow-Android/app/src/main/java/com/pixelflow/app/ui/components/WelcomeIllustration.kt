package com.pixelflow.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pixelflow.app.ui.theme.Mint
import com.pixelflow.app.ui.theme.PrimaryLight
import com.pixelflow.app.ui.theme.PrimarySecondary

/**
 * Welcome-screen illustration: two overlapping card thumbnails.
 * Left card is desaturated/gray (represents "before"), right card is a rich
 * green painterly tile (represents "after") with a small green check badge.
 * This matches the Stitch reference exactly in composition.
 */
@Composable
fun WelcomeIllustration(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(320.dp)
            .background(
                brush = Brush.verticalGradient(
                    listOf(Mint.copy(alpha = 0.55f), Color.White)
                )
            )
    ) {
        // LEFT card (behind, slightly offset up-left) — desaturated
        Surface(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = (-70).dp, y = 0.dp)
                .width(190.dp)
                .height(240.dp),
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            listOf(Color(0xFFEFEFEF), Color(0xFFD8D8D8), Color(0xFFB9B9B9))
                        )
                    )
            ) {
                // Tiny "processing" ring
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color.Transparent)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(Color.Transparent)
                            )
                        }
                    }
                }
            }
        }

        // RIGHT card (front, offset right) — rich green painterly
        Surface(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = 70.dp, y = 0.dp)
                .width(210.dp)
                .height(270.dp),
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 14.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            listOf(
                                Color(0xFF14532D),
                                Color(0xFF166534),
                                Color(0xFF15803D),
                                Color(0xFF16A34A),
                            )
                        )
                    )
            ) {
                // Leaf-vein-like accent stripes
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 24.dp)
                        .width(4.dp)
                        .height(180.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color.White.copy(alpha = 0.12f))
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .width(3.dp)
                        .height(140.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                )

                // Green check badge (top-right)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(PrimaryLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = PrimarySecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(Modifier.size(0.dp))
        // Keep imports live
        @Suppress("unused") val _t = MaterialTheme.colorScheme
    }
}
