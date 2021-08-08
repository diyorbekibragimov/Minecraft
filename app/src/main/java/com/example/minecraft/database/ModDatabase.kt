package com.example.minecraft.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.minecraft.dao.ModDao
import com.example.minecraft.data.Mod

@Database(entities = [Mod::class], version = 1, exportSchema = false)
abstract class ModDatabase : RoomDatabase() {
    abstract fun modDao(): ModDao

    companion object {
        @Volatile
        private var INSTANCE: ModDatabase? = null

        fun getDatabase(context: Context): ModDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        ModDatabase::class.java,
                        "mod_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}