package com.example.growbot

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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            GrowbotTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    ScoreTable()
                }
            }
        }
    }
}

@Composable
fun ScoreTable() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 5.dp, vertical = 10.dp)
    ) {
        // Titel
        Text(
            text = "Growbot", // Ersetze dies durch getString(R.string.app_name) in einer Activity
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
            Text(modifier = Modifier.weight(1f), text = "Rank")
            Text(modifier = Modifier.weight(1f), text = "Player")
            Text(modifier = Modifier.weight(1f), text = "Team")
            Text(modifier = Modifier.weight(1f), text = "Points")
        }

        // Datenzeilen
        val players = listOf(
            Player("1", "Virat Kohli", "IND", "895"),
            Player("2", "Rohit Sharma", "IND", "863"),
            Player("3", "Faf du Plessis", "PAK", "834"),
            Player("4", "Steven Smith", "AUS", "820"),
            Player("5", "Ross Taylor", "NZ", "817")
        )

        for (player in players) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF0F7F7))
                    .padding(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(modifier = Modifier.weight(1f), text = player.rank)
                Text(modifier = Modifier.weight(1f), text = player.name)
                Text(modifier = Modifier.weight(1f), text = player.team)
                Text(modifier = Modifier.weight(1f), text = player.points)
            }
        }
    }
}

data class Player(val rank: String, val name: String, val team: String, val points: String)










