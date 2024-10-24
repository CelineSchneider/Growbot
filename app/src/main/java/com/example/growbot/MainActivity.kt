package com.example.growbot

import Plant
import Plants
import WateringEntry
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.google.gson.Gson
import java.io.InputStreamReader
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun readJsonFromAssets(context: Context): Plants? {
    val assetManager = context.assets
    val inputStream = assetManager.open("plants.json")
    val reader = InputStreamReader(inputStream)

    return try {
        val gson = Gson()
        gson.fromJson(reader, Plants::class.java)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}


// Entry point of the application
class MainActivity : ComponentActivity() {
    // Zustand für die Pflanzen-Daten
    private var plantsState by mutableStateOf<Plants?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        plantsState = readJsonFromAssets(applicationContext)

        setContent {
            GrowbotTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    NavigationGraph(navController, plantsState)
                }
            }
        }
    }
}

// Navigation setup
@Composable
fun NavigationGraph(navController: NavHostController, plantsState: Plants?) {
    NavHost(navController = navController, startDestination = "plant_table") {
        composable("plant_table") { PlantTableScreen(navController, plantsState) }
        composable("plant_detail/{plantName}") { backStackEntry ->
            val plantName = backStackEntry.arguments?.getString("plantName")
            PlantDetailScreen(plantName, navController, plantsState)
        }
        composable("add_plant") { AddPlantScreen(navController) }
    }
}



// PlantTableScreen showing the list of plants
@Composable
fun PlantTableScreen(navController: NavHostController, plantsState: Plants?) {
    val plants = plantsState ?: Plants(emptyList(), 0)
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = 0.03f * screenHeight,
                bottom = 0.03f * screenHeight,
                start = 0.03f * screenHeight,
                end = 0.03f * screenHeight
            )
    ) {
        TopBar(plantsState)
        Header()
        PlantList(plants.plants, navController)

        // Hier kannst du die geparsten Daten verwenden
        Log.d("Parsed Plants", "Water Level: ${plants.waterLevel}")
        plants.plants.forEach { plant ->
            Log.d("Plant", "Name: ${plant.name}, Watering Entries: ${plant.watering?.joinToString()}")
        }

    }
}

@Composable
fun TopBar(plantsState: Plants?) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val notificationsExist = plantsState?.notifications?.any { it.status == true } ?: false
    var showNotifications by remember { mutableStateOf(false) }  // Initial auf false setzen

    // Verwenden einer Column, um die Glocke und die Überschrift übereinander zu platzieren
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(0.03f * screenHeight),  // Die Höhe anpassen, je nach Bedarf
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Benachrichtigungsglocke anzeigen
        NotificationBell(hasNotification = notificationsExist) {
            showNotifications = true  // Zeigt den Dialog an, wenn die Glocke geklickt wird
        }
    }

    // Benachrichtigungen anzeigen, wenn showNotifications true ist
    if (showNotifications) {
        NotificationList(onDismiss = { showNotifications = false }, plantsState)  // Schließt den Dialog, wenn der Nutzer ihn schließt
    }
}


@Composable
fun NotificationBell(hasNotification: Boolean, onClick: () -> Unit) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .size(0.025f * screenHeight)
                .align(Alignment.TopEnd)
                .clickable(onClick = onClick)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.bell),
                contentDescription = "Notifications",
                modifier = Modifier
                    .size(0.025f * screenHeight) // Glocken-Icon in Bezug auf 8% der Bildschirmhöhe
                    .align(Alignment.TopEnd) // Glocken-Icon oben rechts
            )

            if (hasNotification) {
                Box(
                    modifier = Modifier
                        .size(0.01f * screenHeight)
                        .background(LightGreen, shape = CircleShape)
                        .align(Alignment.TopEnd)
                )
            }
        }
    }
}




@Composable
fun Header() {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            fontSize = (0.15f * screenWidth.value).sp,
            fontWeight = FontWeight.Normal,
            fontFamily = FontFamily(Font(R.font.atop)),
            color = DarkGreen
        )

        Spacer(modifier = Modifier.height((0.06f * screenWidth.value).dp))

        Image(
            painter = painterResource(id = R.drawable.growbot_transparent),
            contentDescription = "Logo",
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .aspectRatio(1f)
        )

        Spacer(modifier = Modifier.height((0.06f * screenWidth.value).dp))
    }
}

@Composable
fun PlantList(plants: List<Plant>, navController: NavHostController) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    Surface(
        modifier = Modifier
            .fillMaxWidth(),
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
                color = DarkGreen
            )

            Spacer(modifier = Modifier.height((0.05f * screenWidth.value).dp))

            // Box to contain the LazyVerticalGrid
            Box(modifier = Modifier.fillMaxSize()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(plants) { plant ->
                        PlantItem(plant, navController)
                    }

                    // Add Plant Button
                    item { AddPlantButton(navController) }
                }
            }
        }
    }
}

@Composable
fun AddPlantButton(navController: NavHostController) {
    var plantName by remember { mutableStateOf("") }
    val selectedIcon = remember { mutableStateOf("Plant") }
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Liste der Icons mit ihren Namen und Ressourcen
    val icons = listOf(
        "Plant" to R.drawable.plant,
        "Monstera" to R.drawable.monstera,
        "Alocasia" to R.drawable.alocasia,
        "Philodendron" to R.drawable.philodendron
    )

    // Button zum Hinzufügen einer Pflanze
    Button(onClick = { showDialog = true }) {
        Text("Add Plant")
    }

    // Zeige den Dialog für die Pflanzenauswahl
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add New Plant") },
            text = {
                Column {
                    TextField(
                        value = plantName,
                        onValueChange = { plantName = it },
                        label = { Text("Plant Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(onClick = { showDialog = false }) {
                        Text("Select Icon: ${selectedIcon.value}")
                    }

                    IconSelectionDialog(
                        selectedIcon = selectedIcon,
                        icons = icons,
                        onDismiss = { showDialog = false }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (plantName.isNotEmpty()) {
                        val plantDatabase = PlantDatabase(context)
                        plantDatabase.addPlant(plantName, selectedIcon.value) // Pflanze hinzufügen
                        navController.navigate("plant_table") // Navigiere zur Pflanzentabelle
                        showDialog = false // Schließe den Dialog
                    } else {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text("Add Plant")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}


@Composable
fun IconSelectionDialog(
    selectedIcon: MutableState<String>,
    icons: List<Pair<String, Int>>, // List of Pair(name, resourceId)
    onDismiss: () -> Unit
) {
    val showIconDialog = remember { mutableStateOf(true) }

    if (showIconDialog.value) {
        AlertDialog(
            onDismissRequest = {
                showIconDialog.value = false
                onDismiss()
            },
            title = { Text("Select an Icon") },
            text = {
                Column {
                    icons.forEach { (name, resId) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedIcon.value = name // Update the selected icon name
                                    showIconDialog.value = false // Close the dialog
                                    onDismiss() // Call dismiss callback
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = resId),
                                contentDescription = name,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(name)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showIconDialog.value = false }) {
                    Text("Close")
                }
            }
        )
    }
}



@Composable
fun PlantItem(plant: Plant, navController: NavHostController) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    Surface(
        modifier = Modifier
            .fillMaxWidth(1f / 3f)
            .aspectRatio(1f)
            .clickable { navController.navigate("plant_detail/${plant.name}") },
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, Color.White),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween // Adjusted for better distribution
        ) {
            val imageResId = getIconResId(plant.icon ?: "plant")

            // Image section with a reduced size
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = plant.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.7f)
            )

            // Text section
            Text(
                text = plant.name,
                fontWeight = FontWeight.Bold,
                fontSize = (0.03f * screenWidth.value).sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.3f)
            )
        }
    }
}


@Composable
fun AddPlantScreen(navController: NavHostController) {
    var plantName by remember { mutableStateOf("") }
    val selectedIcon = remember { mutableStateOf("plant") }
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    val icons = listOf(
        "Plant" to R.drawable.plant,
        "Monstera" to R.drawable.monstera,
        "Alocasia" to R.drawable.alocasia,
        "Philodendron" to R.drawable.philodendron
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Add New Plant", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = DarkGreen)

        TextField(
            value = plantName,
            onValueChange = { plantName = it },
            label = { Text("Plant Name") },
            modifier = Modifier.fillMaxWidth()
        )

        // Button zum Öffnen des Dialogs
        Button(onClick = { showDialog = true }) {
            Text("Select Icon: ${selectedIcon.value}")
        }

        // Zeige den Dialog für die Auswahl des Icons
        if (showDialog) {
            IconSelectionDialog(
                selectedIcon = selectedIcon,
                icons = icons,
                onDismiss = { showDialog = false } // Schließe den Dialog
            )
        }

        Spacer(modifier = Modifier.weight(0.2f))

        Button(onClick = {
            if (plantName.isNotEmpty()) {
                val plantDatabase = PlantDatabase(context)
                plantDatabase.addPlant(plantName, selectedIcon.value) // Pflanze hinzufügen
                navController.navigate("plant_table")
            } else {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Add Plant")
        }
    }
}




//TODO: data visualisation
@Composable
fun PlantDetailScreen(plantName: String?, navController: NavHostController, plantsState: Plants?) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    val plant = plantsState?.plants?.find { it.name == plantName }

    if (plant == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Plant not found",
                fontSize = (screenWidth.value * 0.06f).sp,
                fontWeight = FontWeight.Bold,
                color = Color.Red
            )

            Spacer(modifier = Modifier.height(screenHeight * 0.02f))

            Button(onClick = { navController.navigate("plant_table") }) {
                Text("Back to Plant Table")
            }
        }
    } else {
        // Speichern der watering-Daten in einer lokalen Variable
        val wateringData = plant.watering

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = plant.name,
                fontSize = (screenWidth.value * 0.06f).sp,
                fontWeight = FontWeight.Bold,
                color = DarkGreen
            )

            Spacer(modifier = Modifier.height(screenHeight * 0.02f))

            val imageResId = getIconResId(plant.icon ?: "plant")
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = plant.name,
                modifier = Modifier.size(screenWidth * 0.3f)
            )

            Spacer(modifier = Modifier.height(screenHeight * 0.02f))

            // Überprüfen, ob Wässerungsdaten vorhanden sind
            if (wateringData != null) {
                if (wateringData.isEmpty()) {
                    Text(
                        text = "No watering schedule available.",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                } else {
                    // Wenn es Wässerungsdaten gibt, werden sie angezeigt
                    WateringCycleList(wateringDates = wateringData)
                }
            }

            Spacer(modifier = Modifier.height(screenHeight * 0.02f))

            Button(onClick = { navController.navigate("plant_table") }) {
                Text("Back to Plant Table")
            }
        }
    }
}



@Composable
fun WateringCycleList(wateringDates: List<WateringEntry>) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "Watering Schedule",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (wateringDates.isNotEmpty()) {
            wateringDates.forEach { wateringEntry ->
                val timestamp = wateringEntry.timestamp // Hol den Timestamp von WateringEntry
                val formattedDate = timestamp?.let { formatTimestamp(it) }
                if (formattedDate != null) {
                    Text(
                        text = formattedDate,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .fillMaxWidth(),
                        color = Color.Black
                    )
                }
            }
        }
        else {
            Text(
                text = "No watering dates available.",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
    }
}

fun formatTimestamp(timestamp: String): String {
    // Beispiel: "2024-10-01T12:00:00Z"
    val formatter = DateTimeFormatter.ofPattern("dd MMM, HH:mm", Locale.getDefault())
    val instant = Instant.parse(timestamp)
    val dateTime = instant.atZone(ZoneId.systemDefault())
    return dateTime.format(formatter)
}


@SuppressLint("MutableCollectionMutableState")
@Composable
fun NotificationList(onDismiss: () -> Unit, plantsState: Plants?) {
    // MutableList für Benachrichtigungen verwenden
    val notifications by remember { mutableStateOf(mutableListOf<String>()) }

    // Benachrichtigungen hinzufügen, falls `plantsState` nicht null ist
    if (plantsState?.notifications != null) {
        notifications.clear()  // Alte Benachrichtigungen löschen, bevor neue hinzugefügt werden
        for (notification in plantsState.notifications) {
            // Nur Benachrichtigungen mit status == true hinzufügen
            if (notification.status == true) {
                notifications.add(notification.error_message ?: "No message available")
            }
        }
    }

    // Dialog anzeigen, wenn Benachrichtigungen vorhanden sind
    if (notifications.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Notifications") },
            text = {
                Column {
                    notifications.forEach { msg ->
                        Text(text = msg, modifier = Modifier.padding(4.dp))
                    }
                }
            },
            confirmButton = {
                Button(onClick = onDismiss) {
                    Text("Dismiss")
                }
            }
        )
    }
}



// Utility functions
fun getIconResId(iconName: String): Int {
    return when (iconName) {
        "plant" -> R.drawable.plant
        "monstera" -> R.drawable.monstera
        "alocasia" -> R.drawable.alocasia
        "philodendron" -> R.drawable.philodendron
        else -> R.drawable.plant
    }
}
