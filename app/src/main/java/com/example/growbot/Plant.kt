package com.example.growbot

class Plant(
    var name: String,
    var id: Int,
    var measurements: List<Measurement>,
    var watering: MutableList<String>, // MutableList für die watering-Liste
    var waterLevel: Int
)
