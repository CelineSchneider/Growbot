package com.example.growbot

import Plant
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class PlantDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {


    companion object {
        private const val DATABASE_NAME = "plants.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_PLANTS = "plants"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_ICON = "icon"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE $TABLE_PLANTS ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_NAME TEXT, "
                + "$COLUMN_ICON TEXT"
                + ")")
        db.execSQL(createTable)

        createAdditionalTables(db)
    }

    private fun createAdditionalTables(db: SQLiteDatabase) {
        // Tabelle für Wasserstand erstellen
        db.execSQL(
            """
            CREATE TABLE watering_tank (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                current_water_level INTEGER
            )
        """
        )

        // Tabelle für Bewässerungseinträge erstellen
        db.execSQL(
            """
            CREATE TABLE watering_entries (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                plant_id INTEGER,
                timestamp TEXT,
                FOREIGN KEY (plant_id) REFERENCES $TABLE_PLANTS($COLUMN_ID)
            )
        """
        )

        // Tabelle für Benachrichtigungen erstellen
        db.execSQL(
            """
            CREATE TABLE plant_notifications (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                code TEXT,
                status INTEGER
            )
        """
        )

        // Tabelle für den Bewässerungsbehälter erstellen
        db.execSQL(
            """
            CREATE TABLE watering_tank (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                current_water_level INTEGER
            )
        """
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PLANTS")
        db.execSQL("DROP TABLE IF EXISTS measurements")
        db.execSQL("DROP TABLE IF EXISTS watering_entries")
        db.execSQL("DROP TABLE IF EXISTS plant_notifications")
        db.execSQL("DROP TABLE IF EXISTS watering_tank")
        onCreate(db)
    }

    // Methode zum Abrufen aller Pflanzen
    @SuppressLint("Range")
    fun getAllPlants(): List<Plant> {
        val plants = mutableListOf<Plant>()
        val db = readableDatabase
        val cursor = db.query(TABLE_PLANTS, null, null, null, null, null, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                val name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
                val icon = cursor.getString(cursor.getColumnIndex(COLUMN_ICON))
                plants.add(Plant(id, name, emptyList(), emptyList(), icon))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return plants
    }

    // Methode zum Hinzufügen einer neuen Pflanze
    fun addPlant(name: String, icon: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_ICON, icon)
        }
        db.insert(TABLE_PLANTS, null, values)
        db.close()
    }

    // Methode zum Aktualisieren des Namens einer Pflanze
    fun updatePlantName(plantId: Int, newName: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, newName)
        }
        db.update(TABLE_PLANTS, values, "$COLUMN_ID = ?", arrayOf(plantId.toString()))
        db.close()
    }

    // Methode zum Aktualisieren des Icons einer Pflanze
    fun updatePlantIcon(plantId: Int, newIcon: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ICON, newIcon)
        }
        db.update(TABLE_PLANTS, values, "$COLUMN_ID = ?", arrayOf(plantId.toString()))
        db.close()
    }
}
