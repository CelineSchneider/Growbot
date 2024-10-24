package com.example.growbot

import Plants
import android.content.Context
import com.google.gson.Gson
import java.io.InputStreamReader

class Helper {

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

}