package com.example.memory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.memory.ui.screen.MemoryGameScreen
import com.example.memory.ui.theme.MemoryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Einstiegspunkt für das Compose-UI: Theme + Scaffold + Screen.
            MemoryTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MemoryGameScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
