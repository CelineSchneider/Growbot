import com.google.gson.annotations.SerializedName

data class Plants(
    @SerializedName("plants")
    var plants: List<Plant> = emptyList(), // Standardwert ist eine leere Liste

    @SerializedName("waterLevel")
    var waterLevel: Int? = null,

    @SerializedName("notifications")
    var notifications: List<PlantNotification> = emptyList() // Standardwert ist eine leere Liste
)

data class Plant(
    @SerializedName("id")
    var id: Int, // ID sollte nicht null sein

    @SerializedName("name")
    var name: String, // Name sollte auch nicht null sein

    @SerializedName("measurements")
    var measurements: List<Measurement> = emptyList(), // Ändere in eine Liste, um mehrere Messungen zu unterstützen

    @SerializedName("watering")
    var watering: List<WateringEntry>? = emptyList(),

    @SerializedName("icon")
    var icon: String? = null
)

data class Measurement(
    @SerializedName("timestamp")
    var timestamp: String? = null,

    @SerializedName("temperature")
    var temperature: Double? = null,

    @SerializedName("humidity")
    var humidity: Double? = null
)

data class WateringEntry(
    @SerializedName("entry")
    var timestamp: String? = null
)

data class PlantNotification(
    @SerializedName("code")
    var code: String? = null,

    @SerializedName("status")
    var status: Boolean? = null,

    @SerializedName("error_message")
    var error_message: String? = null
)
