package com.example.growbot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.growbot.ui.theme.DarkGreen
import com.example.growbot.ui.theme.GrowbotTheme
import com.example.growbot.ui.theme.LightGreen
import com.google.accompanist.flowlayout.FlowRow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GrowbotTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Lese die XML-Datei und erhalte die Pflanzen-Daten
                    val plants = readXmlFromAssets(this) ?: Plants(emptyList(), 0)
                    PlantTable(plants)
                }
            }
        }
    }
}

@Composable
fun PlantTable(plant_collection: Plants) {
    val selectedPlant = remember { mutableStateOf<Plant?>(null) } // Zustand für die ausgewählte Pflanze

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 5.dp, vertical = 10.dp)
    ) {
        Text(
            text = "Growbot",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = DarkGreen,
        )

        Text(
            text = "Pflanzenübersicht",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = DarkGreen,
        )

        // Prüfen, ob eine Pflanze ausgewählt wurde
        if (selectedPlant.value != null) {
            PlantDetail(selectedPlant.value!!)
        } else {
            // Pflanzen-Datenzeilen mit FlowRow für flexibles Layout
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LightGreen)
                    .padding(5.dp),
                mainAxisSpacing = 16.dp,
                crossAxisSpacing = 16.dp
            ) {
                for (plant in plant_collection.plants ?: emptyList()) {
                    // Container für Bild und Text
                    Surface(
                        modifier = Modifier
                            .size(100.dp) // Feste Größe für quadratische Kästchen
                            .clickable {
                                selectedPlant.value = plant // Pflanze setzen, wenn auf sie geklickt wird
                            },
                        shape = RoundedCornerShape(16.dp), // Abgerundete Ecken
                        border = BorderStroke(2.dp, Color.White), // Weißer Rand
                        color = Color.White // Hintergrundfarbe
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize() // Füllt den gesamten Platz im Container
                                .padding(5.dp), // Padding innerhalb des Containers
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center // Zentriert den Inhalt vertikal
                        ) {
                            val imageResId = plant.icon?.let { icon ->
                                val context = LocalContext.current
                                context.resources.getIdentifier(icon, "drawable", context.packageName)
                            } ?: R.drawable.plant // Fallback-Bild

                            Image(
                                painter = painterResource(id = imageResId),
                                contentDescription = plant.name,
                                modifier = Modifier
                                    .size(60.dp) // Größe der Bilder einstellen
                                    .align(Alignment.CenterHorizontally) // Zentriert das Bild
                            )

                            // Dynamische Textanzeige
                            Text(
                                text = plant.name ?: "Kein Name",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(top = 8.dp)
                                    .align(Alignment.CenterHorizontally) // Zentriert den Text
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlantDetail(plant: Plant) {
    // Hier kannst du den Detailinhalt für die Pflanze anzeigen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Details für: ${plant.name}",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = DarkGreen,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Hier kannst du weitere Informationen zur Pflanze anzeigen
        // Zum Beispiel:
        Image(
            painter = painterResource(id = R.drawable.plant), // Beispielbild
            contentDescription = plant.name,
            modifier = Modifier.size(100.dp)
        )

        Text(
            text = "Weitere Informationen zur Pflanze.",
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
