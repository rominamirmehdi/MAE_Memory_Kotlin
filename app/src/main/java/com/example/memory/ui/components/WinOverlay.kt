package com.example.memory.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun WinOverlay(
    isGameOver: Boolean,
    attempts: Int,
    elapsedSeconds: Long,
    onRestart: () -> Unit,
    onChangeDifficulty: () -> Unit
) {
    val winGlow = remember { Animatable(0f) }

    // Kurzer Glow, wenn das Spiel gewonnen wurde.
    LaunchedEffect(isGameOver) {
        if (isGameOver) {
            winGlow.snapTo(0f)
            winGlow.animateTo(1f, animationSpec = tween(250))
            winGlow.animateTo(0f, animationSpec = tween(700))
        } else {
            winGlow.snapTo(0f)
        }
    }

    AnimatedVisibility(
        visible = isGameOver,
        enter = fadeIn(animationSpec = tween(250)) + scaleIn(
            initialScale = 0.9f,
            animationSpec = tween(350)
        ),
        exit = fadeOut(animationSpec = tween(200)) + scaleOut(
            targetScale = 0.95f,
            animationSpec = tween(200)
        )
    ) {
        // Deutliche Gewinnanzeige als Overlay: dimmt den Hintergrund und fokussiert das Ergebnis.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x88000000)),
            contentAlignment = Alignment.Center
        ) {
            // Glow-Schicht + Ergebnis-Karte mit klarer CTA (nochmal spielen / Schwierigkeit ändern).
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .graphicsLayer {
                            // Glow wird über alpha/scale animiert.
                            alpha = winGlow.value * 0.35f
                            scaleX = 1.03f
                            scaleY = 1.03f
                        }
                        .background(
                            MaterialTheme.colorScheme.primary,
                            RoundedCornerShape(18.dp)
                        )
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Gewonnen!",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Versuche: $attempts · Zeit: ${elapsedSeconds}s",
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Button(onClick = onRestart) {
                            Text("Noch eine Runde")
                        }
                        Button(onClick = onChangeDifficulty) {
                            Text("Schwierigkeit ändern")
                        }
                    }
                }
            }
        }
    }
}
