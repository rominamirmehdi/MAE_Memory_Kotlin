package com.example.memory.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.memory.game.createDeck
import com.example.memory.model.Difficulty
import com.example.memory.model.MemoryCard
import com.example.memory.model.gridConfigFor
import com.example.memory.ui.components.DifficultySelection
import com.example.memory.ui.components.MemoryCardView
import com.example.memory.ui.components.WinOverlay
import com.example.memory.ui.theme.MemoryTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MemoryGameScreen(modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()

    // UI-State des Spiels.
    var difficulty by remember { mutableStateOf<Difficulty?>(null) }
    var cards by remember { mutableStateOf(emptyList<MemoryCard>()) }
    var attempts by remember { mutableIntStateOf(0) }
    var matchedPairs by remember { mutableIntStateOf(0) }
    var isChecking by remember { mutableStateOf(false) }
    var startTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var elapsedSeconds by remember { mutableLongStateOf(0) }
    var wobbleTokens by remember { mutableStateOf<Map<Int, Int>>(emptyMap()) }

    val totalPairs = difficulty?.pairCount ?: 0
    val isGameOver = totalPairs > 0 && matchedPairs == totalPairs

    // Zentraler Reset: setzt Spielzustand für eine Schwierigkeit.
    fun resetGame(selected: Difficulty) {
        cards = createDeck(selected)
        attempts = 0
        matchedPairs = 0
        isChecking = false
        startTime = System.currentTimeMillis()
        elapsedSeconds = 0
        wobbleTokens = emptyMap()
    }

    // Einfacher Timer: zählt Sekunden, bis das Spiel gewonnen ist.
    LaunchedEffect(startTime, isGameOver) {
        if (!isGameOver) {
            while (true) {
                delay(1000)
                elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000
                if (matchedPairs == totalPairs) break
            }
        }
    }

    // Setzt den aktuellen Spielstand mit derselben Schwierigkeit zurück.
    fun restart() {
        val selected = difficulty ?: return
        resetGame(selected)
    }

    // Startet ein neues Spiel mit ausgewählter Schwierigkeit.
    fun chooseDifficulty(selected: Difficulty) {
        difficulty = selected
        resetGame(selected)
    }

    // Dreht eine Karte um und prüft ggf. ein Paar.
    fun flipCard(index: Int) {
        if (isChecking) return
        val current = cards[index]
        if (current.isFaceUp || current.isMatched) return

        // Karte aufdecken.
        cards = cards.toMutableList().also { list ->
            list[index] = current.copy(isFaceUp = true)
        }

        val faceUp = cards.withIndex()
            .filter { it.value.isFaceUp && !it.value.isMatched }

        if (faceUp.size == 2) {
            attempts++
            isChecking = true
            scope.launch {
                // Kleine Pause, damit der Spieler die Karte sieht.
                delay(800)
                val first = faceUp[0]
                val second = faceUp[1]
                // Bei Übereinstimmung markieren, sonst wieder umdrehen.
                if (first.value.imageResId == second.value.imageResId) {
                    cards = cards.toMutableList().also { list ->
                        list[first.index] = list[first.index].copy(isMatched = true)
                        list[second.index] = list[second.index].copy(isMatched = true)
                    }
                    matchedPairs++
                } else {
                    // Fehlversuch: Wobble-Animation auf die beiden Karten triggern.
                    wobbleTokens = wobbleTokens.toMutableMap().also { map ->
                        val firstId = first.value.id
                        val secondId = second.value.id
                        map[firstId] = (map[firstId] ?: 0) + 1
                        map[secondId] = (map[secondId] ?: 0) + 1
                    }
                    cards = cards.toMutableList().also { list ->
                        list[first.index] = list[first.index].copy(isFaceUp = false)
                        list[second.index] = list[second.index].copy(isFaceUp = false)
                    }
                }
                isChecking = false
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Titelbereich.
            Text(
                text = "Memory",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )

            if (difficulty == null) {
                // Startseite mit Schwierigkeitsauswahl.
                DifficultySelection(
                    onSelect = { chooseDifficulty(it) }
                )
            } else {
                // Status- und Steuerungsleiste während des Spiels.
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Versuche",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "$attempts",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Paare",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "$matchedPairs/$totalPairs",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Zeit",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${elapsedSeconds}s",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Button(onClick = { restart() }) {
                                Text("Neustart")
                            }
                            Button(onClick = { difficulty = null }) {
                                Text("Schwierigkeit")
                            }
                        }
                    }
                }

                val config = gridConfigFor(difficulty)

                // Spielfeld als Grid.
                LazyVerticalGrid(
                    columns = GridCells.Fixed(config.columns),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 4.dp)
                ) {
                    itemsIndexed(cards, key = { _, card -> card.id }) { index, card ->
                        MemoryCardView(
                            card = card,
                            onClick = { flipCard(index) },
                            size = config.cardSize,
                            backgroundImageResId = difficulty?.backgroundImage,
                            wobbleToken = wobbleTokens[card.id] ?: 0
                        )
                    }
                }
            }
        }

        // Gewinnanzeige als Overlay (inkl. Glow-Animation).
        WinOverlay(
            isGameOver = isGameOver,
            attempts = attempts,
            elapsedSeconds = elapsedSeconds,
            onRestart = { restart() },
            onChangeDifficulty = { difficulty = null }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MemoryTheme {
        MemoryGameScreen()
    }
}
