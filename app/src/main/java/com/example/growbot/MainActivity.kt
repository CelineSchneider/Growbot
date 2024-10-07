package com.example.growbot

import Texts
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.growbot.ui.theme.GrowbotTheme
import readXmlFromAssets

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GrowbotTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Lese die XML-Datei und erhalte die Pflanzen-Daten
                    val plants = readXmlFromAssets(this) ?: Texts(emptyList(), 0)
                    PlantTable(plants)
                }
            }
        }
    }
}

@Composable
fun PlantTable(texts: Texts) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 5.dp, vertical = 10.dp)
    ) {
        // Titel
        Text(
            text = "Growbot - Pflanzenübersicht",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF005B5B) // Dunkelgrün
        )

        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF51B435))
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(modifier = Modifier.weight(1f), text = "ID")
            Text(modifier = Modifier.weight(2f), text = "Name")
            Text(modifier = Modifier.weight(2f), text = "Letzte Bewässerung")
            Text(modifier = Modifier.weight(1f), text = "Wasserstand")
        }

        // Pflanzen-Datenzeilen
        for (plant in texts.plants ?: emptyList()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF0F7F7))
                    .padding(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val lastWatering = plant.watering?.lastOrNull()?.date ?: "Keine Daten"
                Text(modifier = Modifier.weight(1f), text = plant.id?.toString() ?: "Unbekannt")
                Text(modifier = Modifier.weight(2f), text = plant.name ?: "Kein Name")
                Text(modifier = Modifier.weight(2f), text = lastWatering)
                Text(modifier = Modifier.weight(1f), text = texts.waterLevel?.toString() ?: "Unbekannt")
            }
        }
    }
}

