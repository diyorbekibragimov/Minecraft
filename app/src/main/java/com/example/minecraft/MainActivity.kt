package com.example.minecraft

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.minecraft.data.Mod
import com.example.minecraft.favorites.FavoritesFragmentDirections
import com.example.minecraft.main_screen.MainFragmentDirections
import kotlinx.android.synthetic.main.activity_main.*
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


        if (nav_host_fragment.findNavController().currentDestination!!.label.toString() == "Main") {
            fragment_replace.text = getString(R.string.favorites)
        } else if (nav_host_fragment.findNavController().currentDestination!!.label.toString() == "Favorites") {
            fragment_replace.text = getString(R.string.mods)
        }

        fragment_replace.setOnClickListener {
            when(fragment_replace.text) {
                getString(R.string.favorites) -> {
                    fragment_replace.text = getString(R.string.mods)
                    nav_host_fragment.findNavController().navigate(MainFragmentDirections.actionMainFragmentToFavoritesFragment())
                }

                getString(R.string.mods) -> {
                    fragment_replace.text = getString(R.string.favorites)
                    nav_host_fragment.findNavController().navigate(FavoritesFragmentDirections.actionFavoritesFragmentToMainFragment())
                }
            }
        }

        mModViewModel = ViewModelProvider(this).get(ModViewModel::class.java)

        val jsonString: String? = readJson()
        val jsonObject = JSONObject(jsonString)
        val keys: Iterator<String> = jsonObject.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            if (jsonObject.get(key) is JSONObject) {
                val content = (jsonObject.get(key) as JSONObject).toMap()
                // If data is in database, then it will ignore and will not inserted
                val mod = Mod(id = 0, title = content.get("xzgd4d4") as String, content = content.get("xzgd4i1") as String, imageUrl = content.get("xzgd4f2") as String, isFav = false, modUri = content.get("xzgd4t3") as String, isImported = false)
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