package com.example.minecraft.repository

import androidx.lifecycle.LiveData
import com.example.minecraft.dao.ModDao
import com.example.minecraft.data.Mod

class ModRepository(private val modDao: ModDao) {
    val readAllData: LiveData<List<Mod>> = modDao.readAllData()
    val readOnlyFavData: LiveData<List<Mod>> = modDao.readOnlyFavData()

    suspend fun addMod(mod: Mod) {
        modDao.addMod(mod)
    }

    suspend fun updateMod(mod: Mod) {
        modDao.updateMod(mod)
    }
}