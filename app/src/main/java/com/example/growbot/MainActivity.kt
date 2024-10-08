package com.example.growbot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
            fontFamily = FontFamily(Font(R.font.atop)),
            color = DarkGreen,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
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
                Surface(
                    modifier = Modifier
                        .size(100.dp)
                        .clickable {
                            navController.navigate("plant_detail/${plant.name}")
                        },
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(2.dp, Color.White),
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(5.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        val imageResId = plant.icon?.let { icon ->
                            val context = LocalContext.current
                            context.resources.getIdentifier(icon, "drawable", context.packageName)
                        } ?: R.drawable.plant

                        Image(
                            painter = painterResource(id = imageResId),
                            contentDescription = plant.name,
                            modifier = Modifier
                                .size(60.dp)
                                .align(Alignment.CenterHorizontally)
                        )


                        Text(
                            text = plant.name ?: "Kein Name",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(top = 8.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PlantDetailScreen(plantName: String?, navController: NavHostController) {
    val plants = readXmlFromAssets(LocalContext.current) ?: Plants(emptyList(), 0)
    val plant = plants.plants?.find { it.name == plantName }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = plant?.name ?: "Unbekannte Pflanze",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = DarkGreen,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        val imageResId = plant?.icon?.let { icon ->
            val context = LocalContext.current
            context.resources.getIdentifier(icon, "drawable", context.packageName)
        } ?: R.drawable.plant

        Image(
            painter = painterResource(id = imageResId),
            contentDescription = plant?.name,
            modifier = Modifier
                .size(60.dp)
                .align(Alignment.CenterHorizontally)
        )

        Text(
            text = "Weitere Informationen zur Pflanze.",
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.padding(top = 8.dp)
        )

        MaterialTheme {
            val temperatureData = listOf(15f, 20f, 22f, 18f, 25f, 30f, 27f)
            TemperatureLineChart(temperatureData, Modifier.size(300.dp, 200.dp))
        }
    }
}

@Composable
fun TemperatureLineChart(
    temperatureData: List<Float>,
    modifier: Modifier = Modifier
) {
    // Max- und Min-Werte für die Skalierung
    val maxY = temperatureData.maxOrNull() ?: 0f
    val minY = temperatureData.minOrNull() ?: 0f

    // Farben und Pinsel
    val lineColor = Color.Blue
    val pointColor = Color.Red
    val axisColor = Color.Gray

    // Anzahl der Labels auf der Y-Achse
    val yAxisLabelsCount = 5
    val yAxisStep = (maxY - minY) / yAxisLabelsCount

    // Platz für die Y-Achse (Skala links)
    Row(modifier = modifier) {
        // Y-Achsen-Beschriftungen
        Column(
            modifier = Modifier.fillMaxHeight().padding(end = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            for (i in 0..yAxisLabelsCount) {
                val label = (minY + i * yAxisStep).toInt().toString()
                Text(
                    text = label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        // Das eigentliche Diagramm
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val chartHeight = size.height
                val chartWidth = size.width

                if (temperatureData.size < 2) return@Canvas

                // Schrittweite auf der X-Achse berechnen
                val stepX = chartWidth / (temperatureData.size - 1)

                // Y-Werte skalieren basierend auf minY und maxY
                fun scaleY(value: Float): Float {
                    return chartHeight - ((value - minY) / (maxY - minY) * chartHeight)
                }

                // Linien zwischen den Punkten zeichnen
                for (i in 0 until temperatureData.size - 1) {
                    val startX = i * stepX
                    val startY = scaleY(temperatureData[i])
                    val endX = (i + 1) * stepX
                    val endY = scaleY(temperatureData[i + 1])

                    // Linie zwischen zwei Punkten
                    drawLine(
                        color = lineColor,
                        start = androidx.compose.ui.geometry.Offset(startX, startY),
                        end = androidx.compose.ui.geometry.Offset(endX, endY),
                        strokeWidth = 4f
                    )
                }

                // Punkte auf der Linie zeichnen
                for (i in temperatureData.indices) {
                    val x = i * stepX
                    val y = scaleY(temperatureData[i])
                    drawCircle(pointColor, radius = 8f, center = androidx.compose.ui.geometry.Offset(x, y))
                }

                // Y-Achse zeichnen
                drawLine(
                    color = axisColor,
                    start = androidx.compose.ui.geometry.Offset(0f, 0f),
                    end = androidx.compose.ui.geometry.Offset(0f, chartHeight),
                    strokeWidth = 5f
                )

                // X-Achse zeichnen
                drawLine(
                    color = axisColor,
                    start = androidx.compose.ui.geometry.Offset(0f, chartHeight),
                    end = androidx.compose.ui.geometry.Offset(chartWidth, chartHeight),
                    strokeWidth = 5f
                )
            }

            // X-Achsen-Beschriftungen
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (i in temperatureData.indices) {
                    Text(
                        text = "T${i + 1}",  // Du kannst hier deine eigenen Beschriftungen verwenden
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}