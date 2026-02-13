package com.example.memory.game

import com.example.memory.model.Difficulty
import com.example.memory.model.MemoryCard

// Erstellt das gemischte Kartendeck für die gewählte Schwierigkeit.
fun createDeck(difficulty: Difficulty): List<MemoryCard> {
    // Zufällige Bildauswahl in der benötigten Anzahl von Paaren.
    val picked = difficulty.images.shuffled().take(difficulty.pairCount)
    // Jedes Bild doppeln (Paare) und erneut mischen.
    val paired = (picked + picked).shuffled()
    return paired.mapIndexed { index, resId ->
        MemoryCard(
            id = index,
            imageResId = resId,
            isFaceUp = false,
            isMatched = false
        )
    }
}
