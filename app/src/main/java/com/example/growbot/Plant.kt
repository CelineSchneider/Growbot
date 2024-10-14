package com.example.growbot
import android.content.Context
import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.core.Persister
import java.io.StringReader

@Root(name = "plants", strict = false)
class Plants @JvmOverloads constructor(
    @field:ElementList(inline = true, required = false)
    var plants: List<Plant>? = null,  // com.example.growbot.Plants könnte null sein, wenn das XML leer ist
    @field:Element(name = "waterLevel", required = false)
    var waterLevel: Int? = null,
    @field:ElementList(name = "notifications", required = false)
    var notifications: List<PlantNotification>? = null
)

@Root(name = "plant", strict = false)
class Plant @JvmOverloads constructor(
    @field:Attribute(name = "id", required = false)
    var id: Int? = null,  // ID optional gemacht
    @field:Element(name = "name", required = false)
    var name: String? = null,  // Name optional gemacht
    @field:ElementList(name = "measurements", inline = true, required = false)
    var measurements: List<Measurement>? = null,
    @field:ElementList(name = "watering", inline = true, required = false)
    var watering: List<WateringEntry>? = null,
    @field:Element(name = "icon", required = false)
    var icon: String? = null
)

@Root(name = "measurement", strict = false)
class Measurement @JvmOverloads constructor(
    @field:Element(name = "timestamp", required = false)
    var timestamp: String? = null,  // Timestamp optional gemacht
    @field:Element(name = "temperature", required = false)
    var temperature: Double? = null,  // Temperatur optional gemacht
    @field:Element(name = "humidity", required = false)
    var humidity: Double? = null,  // Feuchtigkeit optional gemacht
)

@Root(name = "entry", strict = false)
class WateringEntry @JvmOverloads constructor(
    @field:Element(name = "entry", required = false)
    var timestamp: String
)


@Root(name = "plantNotification", strict = false)
class PlantNotification @JvmOverloads constructor(
    @field:Attribute(name = "code", required = false)
    var code: String? = null,
    @field:Attribute(name = "status", required = false)
    var status: Boolean? = null
)




fun readXmlFromAssets(context: Context): Plants? {
    // AssetManager verwenden, um die Datei zu öffnen
    val assetManager = context.assets

    // Öffne den InputStream der Datei
    val inputStream = assetManager.open("plants.xml")
    val xmlString = inputStream.bufferedReader().use { it.readText() }  // Lese den Inhalt als String

    // Erstelle den Simple XML Serializer
    val serializer: Serializer = Persister()

    // Verwende StringReader, um den XML-String in ein lesbares Format zu bringen
    val reader = StringReader(xmlString)

    // Lese und parse die XML-Daten
    return try {
        serializer.read(Plants::class.java, reader)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}





