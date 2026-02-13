package com.example.memory.model

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Modell für eine einzelne Karte im Memory-Spiel.
data class MemoryCard(
    val id: Int,
    val imageResId: Int,
    val isFaceUp: Boolean,
    val isMatched: Boolean
)

// Schwierigkeitsstufen inklusive Kartenzahl, Bild-Set und Hintergrund.
enum class Difficulty(
    val label: String,
    val pairCount: Int,
    val images: List<Int>,
    val backgroundImage: Int
) {
    EASY(
        label = "Leicht",
        pairCount = 6,
        images = listOf(
            com.example.memory.R.drawable.codeing_1,
            com.example.memory.R.drawable.codeing_2,
            com.example.memory.R.drawable.codeing_3,
            com.example.memory.R.drawable.codeing_4,
            com.example.memory.R.drawable.codeing_5,
            com.example.memory.R.drawable.codeing_6,
            com.example.memory.R.drawable.codeing_7,
            com.example.memory.R.drawable.codeing_8,
            com.example.memory.R.drawable.codeing_9
        ),
        backgroundImage = com.example.memory.R.drawable.easybackground
    ),
    MEDIUM(
        label = "Mittel",
        pairCount = 10,
        images = listOf(
            com.example.memory.R.drawable.fruit_1,
            com.example.memory.R.drawable.fruit_2,
            com.example.memory.R.drawable.fruit_3,
            com.example.memory.R.drawable.fruit_4,
            com.example.memory.R.drawable.fruit_5,
            com.example.memory.R.drawable.fruit_6,
            com.example.memory.R.drawable.fruit_7,
            com.example.memory.R.drawable.fruit_8,
            com.example.memory.R.drawable.fruit_9,
            com.example.memory.R.drawable.fruit_10
        ),
        backgroundImage = com.example.memory.R.drawable.mediumbackground
    ),
    HARD(
        label = "Schwer",
        pairCount = 15,
        images = listOf(
            com.example.memory.R.drawable.monster_1,
            com.example.memory.R.drawable.monster_2,
            com.example.memory.R.drawable.monster_3,
            com.example.memory.R.drawable.monster_4,
            com.example.memory.R.drawable.monster_5,
            com.example.memory.R.drawable.monster_6,
            com.example.memory.R.drawable.monster_7,
            com.example.memory.R.drawable.monster_8,
            com.example.memory.R.drawable.monster_9,
            com.example.memory.R.drawable.monster_10,
            com.example.memory.R.drawable.monster_11,
            com.example.memory.R.drawable.monster_12,
            com.example.memory.R.drawable.monster_13,
            com.example.memory.R.drawable.monster_14,
            com.example.memory.R.drawable.monster_15
        ),
        backgroundImage = com.example.memory.R.drawable.hardbackground
    )
}

data class GridConfig(val columns: Int, val cardSize: Dp)

// Liefert Layout-Parameter (Spalten/Größe) abhängig von der Schwierigkeit.
fun gridConfigFor(difficulty: Difficulty?): GridConfig {
    return when (difficulty) {
        Difficulty.EASY -> GridConfig(columns = 3, cardSize = 140.dp)
        Difficulty.MEDIUM -> GridConfig(columns = 4, cardSize = 95.dp)
        Difficulty.HARD -> GridConfig(columns = 5, cardSize = 80.dp)
        null -> GridConfig(columns = 4, cardSize = 84.dp)
    }
}

data class DifficultyChoice(
    val title: String,
    val subtitle: String,
    val difficulty: Difficulty
)
