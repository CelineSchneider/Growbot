package com.example.growbot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.growbot.ui.theme.DarkGreen
import com.example.growbot.ui.theme.GrowbotTheme
import com.example.growbot.ui.theme.LightGreen

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

    // Bildschirmkonfiguration für Berechnung der Breiten und Schriftgrößen
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val paddingHorizontal = 0.015f * screenWidth.value
    val paddingVertical = 0.02f * screenWidth.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = paddingHorizontal.dp, vertical = paddingVertical.dp)
    ) {
        Text(
            text = "Growbot",
            fontSize = (0.15f * screenWidth.value).sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily(Font(R.font.atop)),
            color = DarkGreen,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Image(
            painter = painterResource(id = R.drawable.growbot_white),
            contentDescription = "Logo",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(0.8f)
                .aspectRatio(1f)
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f)
                .padding(horizontal = (0.05f * screenWidth.value).dp),
            shape = RoundedCornerShape(16.dp),
            color = LightGreen
        ) {
            Column(
                modifier = Modifier
                    .padding((0.04f * screenWidth.value).dp)
                    .fillMaxSize()
            ) {
                Text(
                    text = "Plants",
                    fontSize = (0.05f * screenWidth.value).sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkGreen,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(horizontal = paddingHorizontal.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .weight(0.05f * screenWidth.value)
                        .padding(
                            horizontal = paddingHorizontal.dp,
                            vertical = paddingVertical.dp
                        ),
                    horizontalArrangement = Arrangement.spacedBy(paddingHorizontal.dp),
                    verticalArrangement = Arrangement.spacedBy(paddingHorizontal.dp)
                ) {
                    items(plants.plants ?: emptyList()) { plant ->
                        PlantItem(plant = plant, navController = navController)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(0.05f * screenHeight))
    }
}

@Composable
fun PlantItem(plant: Plant, navController: NavHostController) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(1f / 3f) // Jedes Element nimmt 1/3 der Breite des Bildschirms ein
            .aspectRatio(1f)
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
                .padding((0.02f * LocalConfiguration.current.screenWidthDp).dp),
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
                    .fillMaxWidth(0.5f)
                    .aspectRatio(1f)
            )

            Text(
                text = plant.name ?: "Kein Name",
                fontWeight = FontWeight.Bold,
                fontSize = (0.04f * LocalConfiguration.current.screenWidthDp).sp,
                modifier = Modifier
                    .padding(top = (0.02f * LocalConfiguration.current.screenWidthDp).dp)
                    .align(Alignment.CenterHorizontally)
            )
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
    val maxY = temperatureData.maxOrNull() ?: 0f
    val minY = temperatureData.minOrNull() ?: 0f

    val lineColor = Color.Blue
    val pointColor = Color.Red
    val axisColor = Color.Gray

    val yAxisLabelsCount = 5
    val yAxisStep = (maxY - minY) / yAxisLabelsCount

    Row(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(end = 8.dp),
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

        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val chartHeight = size.height
                val chartWidth = size.width

                if (temperatureData.size < 2) return@Canvas

                val stepX = chartWidth / (temperatureData.size - 1)
                val stepY = chartHeight / (maxY - minY)

                for (i in 0 until temperatureData.size - 1) {
                    val x1 = i * stepX
                    val y1 = chartHeight - (temperatureData[i] - minY) * stepY
                    val x2 = (i + 1) * stepX
                    val y2 = chartHeight - (temperatureData[i + 1] - minY) * stepY

                    drawLine(
                        start = androidx.compose.ui.geometry.Offset(x1, y1),
                        end = androidx.compose.ui.geometry.Offset(x2, y2),
                        color = lineColor,
                        strokeWidth = 4f
                    )
                }

                temperatureData.forEachIndexed { index, value ->
                    val x = index * stepX
                    val y = chartHeight - (value - minY) * stepY
                    drawCircle(
                        color = pointColor,
                        radius = 8f,
                        center = androidx.compose.ui.geometry.Offset(x, y)
                    )
                }
            }
        }
    }
}
