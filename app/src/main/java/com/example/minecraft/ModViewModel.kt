package com.example.minecraft

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.minecraft.data.Mod
import com.example.minecraft.database.ModDatabase
import com.example.minecraft.repository.ModRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ModViewModel(application: Application) : AndroidViewModel(application) {
    val readAllData: LiveData<List<Mod>>
    val readOnlyFavData: LiveData<List<Mod>>

    private val repository: ModRepository

    init {
        val modDao = ModDatabase.getDatabase(application).modDao()
        repository = ModRepository(modDao)
        readAllData = repository.readAllData
        readOnlyFavData = repository.readOnlyFavData
    }

    fun addMod(mod: Mod) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addMod(mod)
        }
    }

    fun updateMod(mod: Mod) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateMod((mod))
        }
    }
}