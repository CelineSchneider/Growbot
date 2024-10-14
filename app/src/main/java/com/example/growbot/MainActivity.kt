package com.example.growbot

import android.os.Bundle
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
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


// Entry point of the application
class MainActivity : ComponentActivity() {
    // Zustand für die Pflanzen-Daten
    var plantsState by mutableStateOf<Plants?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Lade die Pflanzen beim Start
        plantsState = readXmlFromAssets(applicationContext)

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
        composable("add_plant") { AddPlantScreen(navController, plantsState) }
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
        PlantList(plants.plants ?: emptyList(), navController, plantsState)
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
fun PlantList(plants: List<Plant>, navController: NavHostController, plantsState: Plants?) {
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
                        PlantItem(plant, navController, plantsState)
                    }

                    // Add Plant Button
                    item { AddPlantButton(navController, plantsState) }
                }
            }
        }
    }
}



@Composable
fun PlantItem(plant: Plant, navController: NavHostController, plantsState: Plants?) {
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
                text = plant.name ?: "Unnamed",
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


// Add Plant Button
@Composable
fun AddPlantButton(navController: NavHostController, plantsState: Plants?) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    Surface(
        modifier = Modifier
            .fillMaxWidth(1f / 3f)
            .aspectRatio(1f)
            .clickable { navController.navigate("add_plant") },
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, Color.White),
        color = Color.White
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.plus),
                contentDescription = "Add Plant",
                modifier = Modifier.size(0.1f * screenWidth),
                contentScale = ContentScale.Fit
            )
        }
    }
}

// AddPlantScreen for adding a new plant
@Composable
fun AddPlantScreen(navController: NavHostController, plantsState: Plants?) {
    var plantName by remember { mutableStateOf("") }
    val selectedIcon = remember { mutableStateOf("plant") }
    val context = LocalContext.current

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

        IconSelectionDropdown(selectedIcon, listOf("plant", "monstera", "alocasia", "philodendron"))

        Spacer(modifier = Modifier.weight(0.2f))

        Button(onClick = {
            if (plantName.isNotEmpty()) {
//                addPlantToXml(context, Plant(name = plantName, icon = selectedIcon.value), plantsState)
                navController.navigate("plant_table")
            } else {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Add Plant")
        }
    }
}

// Icon selection dropdown
@Composable
fun IconSelectionDropdown(selectedIcon: MutableState<String>, icons: List<String>) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            "Select Icon: ${selectedIcon.value}",
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            icons.forEach { iconName ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = getIconResId(iconName)),
                                contentDescription = iconName,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = iconName)
                        }
                    },
                    onClick = {
                        selectedIcon.value = iconName
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun PlantDetailScreen(plantName: String?, navController: NavHostController, plantsState: Plants?) {
    val context = LocalContext.current
    val plant = plantsState?.plants?.find { it.name == plantName }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        plant?.let {
            Text(
                text = it.name ?: "Unnamed",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGreen
            )

            Spacer(modifier = Modifier.height(16.dp))

            val imageResId = getIconResId(it.icon ?: "plant")
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = it.name,
                modifier = Modifier.size(128.dp) // Größe des Pflanzenbildes anpassen
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Hier kannst du zusätzliche Details zur Pflanze hinzufügen, z.B. Beschreibung
            Text(
                text = "Details about ${it.name}",
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { navController.navigate("plant_table") }) {
                Text("Back to Plant Table")
            }
        } ?: run {
            Text(
                text = "Plant not found",
                fontSize = 16.sp,
                color = Color.Red
            )
        }
    }
}



@Composable
fun NotificationList(onDismiss: () -> Unit, plantsState: Plants?) {
    // MutableList für Benachrichtigungen verwenden
    var notifications by remember { mutableStateOf(mutableListOf<String>()) }

    // Benachrichtigungen hinzufügen, falls `plantsState` nicht null ist
    if (plantsState != null && plantsState.notifications != null) {
        notifications.clear()  // Alte Benachrichtigungen löschen, bevor neue hinzugefügt werden
        for (notification in plantsState.notifications!!) {
            // Nur Benachrichtigungen mit status == true hinzufügen
            if (notification.status == true) {
                notifications.add("${notification.code}: ${notification.status}")
            }
        }
    }

    // Dialog anzeigen mit einer Liste von Benachrichtigungen
    AlertDialog(
        onDismissRequest = onDismiss,  // Die Funktion, die aufgerufen wird, wenn der Dialog geschlossen wird
        title = { Text("Notifications") },
        text = {
            Column {
                // Wenn keine Benachrichtigungen angezeigt werden, zeigen wir eine Standardnachricht an
                if (notifications.isEmpty()) {
                    Text("No new notifications.")
                } else {
                    notifications.forEach { notification ->
                        Text(text = notification)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onDismiss() }) {  // Schließt den Dialog, wenn der Button gedrückt wird
                Text("Close")
            }
        }
    )
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
