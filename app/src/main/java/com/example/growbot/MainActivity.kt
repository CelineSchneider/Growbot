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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.growbot.ui.theme.DarkGreen
import com.example.growbot.ui.theme.GrowbotTheme
import com.example.growbot.ui.theme.LightGreen
import com.google.accompanist.flowlayout.FlowRow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GrowbotTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    NavigationGraph(navController)
                }
            }
        }
    }
}

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "plant_table") {
        composable("plant_table") { PlantTableScreen(navController) }
        composable("plant_detail/{plantName}") { backStackEntry ->
            val plantName = backStackEntry.arguments?.getString("plantName")
            PlantDetailScreen(plantName, navController)
        }
    }
}

@Composable
fun PlantTableScreen(navController: NavHostController) {
    val plants = readXmlFromAssets(LocalContext.current) ?: Plants(emptyList(), 0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 5.dp, vertical = 10.dp)
    ) {
        Text(
            text = "Growbot",
            fontSize = 50.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily(Font(R.font.atop)), // Benutzerdefinierte Schriftart
            color = DarkGreen,
            modifier = Modifier
                .align(Alignment.CenterHorizontally) // Zentrieren
        )

        Image(
            painter = painterResource(id = R.drawable.growbot_white),
            contentDescription = "Logo",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )

        Text(
            text = "Pflanzenübersicht",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = DarkGreen,
        )

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(LightGreen)
                .padding(5.dp),
            mainAxisSpacing = 16.dp,
            crossAxisSpacing = 16.dp
        ) {
            for (plant in plants.plants ?: emptyList()) {
                // Container für Bild und Text
                Surface(
                    modifier = Modifier
                        .size(100.dp) // Feste Größe für quadratische Kästchen
                        .clickable {
                            navController.navigate("plant_detail/${plant.name}") // Navigation zur Detailansicht
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

@Composable
fun PlantDetailScreen(plantName: String?, navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$plantName",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = DarkGreen,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Image(
            painter = painterResource(id = R.drawable.plant), // Beispielbild
            contentDescription = plantName,
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

