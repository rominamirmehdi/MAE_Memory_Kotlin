package com.example.memory.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.memory.model.MemoryCard

@Composable
fun MemoryCardView(
    card: MemoryCard,
    onClick: () -> Unit,
    size: Dp,
    backgroundImageResId: Int? = null,
    wobbleToken: Int = 0
) {
    // Farbwahl je nach Zustand (aufgedeckt / verdeckt).
    val faceUpColor = MaterialTheme.colorScheme.surface
    val faceDownColor = MaterialTheme.colorScheme.primaryContainer
    val background = if (card.isFaceUp || card.isMatched) faceUpColor else faceDownColor

    // Flip-Animation: 0° = Rückseite, 180° = Vorderseite.
    val rotation by animateFloatAsState(
        targetValue = if (card.isFaceUp || card.isMatched) 180f else 0f,
        animationSpec = tween(durationMillis = 350),
        label = "cardFlip"
    )
    val showFront = rotation > 90f

    val popScale = remember { Animatable(1f) }
    var hasPopped by remember { mutableStateOf(false) }
    val wobbleOffset = remember { Animatable(0f) }
    val amplitudePx = with(LocalDensity.current) { 6.dp.toPx() }

    // Pop-Animation, wenn ein Paar gefunden wurde.
    LaunchedEffect(card.isMatched) {
        if (!card.isMatched) {
            hasPopped = false
            popScale.snapTo(1f)
        } else if (!hasPopped) {
            hasPopped = true
            popScale.snapTo(1f)
            popScale.animateTo(1.06f, animationSpec = tween(120))
            popScale.animateTo(1f, animationSpec = tween(160))
        }
    }

    // Wobble-Animation bei Fehlversuch.
    LaunchedEffect(wobbleToken) {
        if (wobbleToken > 0) {
            wobbleOffset.snapTo(0f)
            wobbleOffset.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 320
                    0f at 0
                    -amplitudePx at 60
                    amplitudePx at 120
                    -amplitudePx * 0.7f at 190
                    amplitudePx * 0.7f at 250
                    0f at 320
                }
            )
        }
    }

    // Einzelne Karte: klickbar, je nach Zustand Bild oder Rückseite.
    Card(
        modifier = Modifier
            .size(size)
            .aspectRatio(1f)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
            .clickable(enabled = !card.isMatched) { onClick() }
            .graphicsLayer {
                // 3D-Drehung + Pop/Wobble-Effekt.
                rotationY = rotation
                cameraDistance = 12 * density
                translationX = wobbleOffset.value
                scaleX = popScale.value
                scaleY = popScale.value
            },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = background),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (showFront) {
                Image(
                    painter = painterResource(id = card.imageResId),
                    contentDescription = "Memory card",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp)
                        // Inhalt wieder zurückdrehen, damit das Bild nicht gespiegelt ist.
                        .graphicsLayer { rotationY = 180f },
                    contentScale = ContentScale.Crop
                )
            } else {
                // Rückseite mit Muster/Illustration + leichter Farbverlauf.
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    MaterialTheme.colorScheme.secondaryContainer
                                )
                            )
                        )
                ) {
                    if (backgroundImageResId != null) {
                        Image(
                            painter = painterResource(id = backgroundImageResId),
                            contentDescription = "Card back",
                            modifier = Modifier
                                .fillMaxSize()
                                .alpha(0.9f),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}
