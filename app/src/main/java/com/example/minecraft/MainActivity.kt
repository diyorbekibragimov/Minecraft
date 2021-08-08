package com.example.minecraft

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.minecraft.data.Mod
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream


class MainActivity : AppCompatActivity() {
    private lateinit var mModViewModel: ModViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view = R.layout.activity_main
        setContentView(view)

        supportActionBar?.hide()

        // Instantiating Fragments for the Bottom Navigation
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val navController = findNavController(R.id.nav_host_fragment)

        bottomNavigationView.setupWithNavController(navController)

        mModViewModel = ViewModelProvider(this).get(ModViewModel::class.java)

        val jsonString: String? = readJson()
        val jsonObject = JSONObject(jsonString)
        val keys: Iterator<String> = jsonObject.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            if (jsonObject.get(key) is JSONObject) {
                val content = (jsonObject.get(key) as JSONObject).toMap()
                // If data is in database, then it will ignore and will not inserted
                val mod = Mod(id = 0, title = content.get("xzgd4d4") as String, content = content.get("xzgd4i1") as String, imageUrl = content.get("xzgd4f2") as String, isFav = false, modUri = content.get("xzgd4t3") as String)
                // Add data to database
                mModViewModel.addMod(mod)
            }
        }
    }

    private fun readJson(): String? {
        var json: String? = null
        try {
            val inputStream: InputStream = assets.open("content.json")
            json = inputStream.bufferedReader().use { it.readText() }
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
        return json
    }

    private fun JSONObject.toMap(): Map<String, *> = keys().asSequence().associateWith {
        when (val value = this[it]) {
            is JSONArray -> {
                val map = (0 until value.length()).associate { Pair(it.toString(), value[it]) }
                JSONObject(map).toMap().values.toList()
            }
            is JSONObject -> value.toMap()
            JSONObject.NULL -> null
            else -> value
        }
    }
}