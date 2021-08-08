package com.example.minecraft.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "mod_table", indices = arrayOf(Index(value = ["title"], unique = true)))
data class Mod(
        @PrimaryKey(autoGenerate = true)
        val id: Int,
        var title: String,
        var content: String,
        var imageUrl: String,
        var isFav: Boolean,
        var modUri: String,
) : Parcelable