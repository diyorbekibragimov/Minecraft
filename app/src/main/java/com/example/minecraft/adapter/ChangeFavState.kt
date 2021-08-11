package com.example.minecraft.adapter

import com.example.minecraft.ModViewModel
import com.example.minecraft.data.Mod

interface ChangeFavState {
    fun changeState(mod: Mod, mModViewModel: ModViewModel): Boolean {
        val changed = mod.isFav
        val updatedMod = Mod(mod.id, mod.title, mod.content, mod.imageUrl, !changed, mod.modUri, mod.isImported)
        mModViewModel.updateMod(updatedMod)
        return !changed
    }
}