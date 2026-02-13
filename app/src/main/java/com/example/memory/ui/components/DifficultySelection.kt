package com.example.memory.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.memory.model.Difficulty
import com.example.memory.model.DifficultyChoice

@Composable
fun DifficultySelection(onSelect: (Difficulty) -> Unit) {
    // Strukturierte Daten für die drei Schwierigkeitskarten.
    val choices = listOf(
        DifficultyChoice(
            title = "Leicht",
            subtitle = "6 Paare · Coding",
            difficulty = Difficulty.EASY
        ),
        DifficultyChoice(
            title = "Mittel",
            subtitle = "10 Paare · Fruit",
            difficulty = Difficulty.MEDIUM
        ),
        DifficultyChoice(
            title = "Schwer",
            subtitle = "15 Paare · Monster",
            difficulty = Difficulty.HARD
        )
    )

    // Auswahl der Schwierigkeit vor Spielstart.
    // Hero-Card: moderner Einstieg mit kurzer Erklärung.
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.secondaryContainer
                        )
                    )
                )
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Willkommen!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Trainiere dein Gedächtnis in 60 Sekunden. Wähle deine Schwierigkeit.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }

    // Auswahlkarten mit großen Touch-Flächen.
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        choices.forEach { choice ->
            // Jede Karte startet das Spiel mit der gewählten Schwierigkeit.
            DifficultyOptionCard(
                title = choice.title,
                subtitle = choice.subtitle,
                onClick = { onSelect(choice.difficulty) }
            )
        }
    }
}

@Composable
private fun DifficultyOptionCard(title: String, subtitle: String, onClick: () -> Unit) {
    // Große, klickbare Karte für gute Bedienbarkeit (Touch-Target).
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "Start",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
