package com.example.minecraft.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.minecraft.data.Mod

@Dao
interface ModDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addMod(mod: Mod)

    @Update
    suspend fun updateMod(mod: Mod)

    @Query("SELECT * FROM mod_table ORDER BY id ASC")
    fun readAllData(): LiveData<List<Mod>>

    @Query("SELECT * FROM mod_table WHERE isFav = 1 ORDER BY id ASC")
    fun readOnlyFavData(): LiveData<List<Mod>>
}