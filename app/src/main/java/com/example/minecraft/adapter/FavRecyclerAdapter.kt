package com.example.minecraft.adapter

import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.minecraft.ModViewModel
import com.example.minecraft.R
import com.example.minecraft.data.Mod
import com.example.minecraft.favorites.FavoritesFragmentDirections
import com.google.android.material.card.MaterialCardView
import java.io.File

class FavRecyclerAdapter(var mModViewModel: ModViewModel) : RecyclerView.Adapter<FavRecyclerAdapter.ViewHolder>(), ChangeFavState {
    private var modList = emptyList<Mod>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemTitle: TextView
        var itemContent: TextView
        var itemIsFavIcon: ImageView
        var itemUploadIcon: ImageView
        var itemCard: MaterialCardView

        init {
            itemTitle = itemView.findViewById(R.id.title)
            itemContent = itemView.findViewById(R.id.content)
            itemIsFavIcon = itemView.findViewById(R.id.favButton)
            itemUploadIcon = itemView.findViewById(R.id.dwnButton)
            itemCard = itemView.findViewById(R.id.item_card)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavRecyclerAdapter.ViewHolder {
        val view = FavRecyclerAdapter.ViewHolder(
                LayoutInflater.from(parent.context).inflate(
                        R.layout.list_item,
                        parent,
                        false
                )
        )
        view.itemIsFavIcon.setOnClickListener {
            val currentItem = modList[view.adapterPosition]
            val newState = changeState(currentItem, mModViewModel)

            if (!newState) {
                notifyItemRemoved(view.adapterPosition)
            }
        }

        return view
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = modList[position]
        holder.itemTitle.text = currentItem.title
        holder.itemContent.text = currentItem.content
        holder.itemIsFavIcon.setImageResource(R.drawable.ic_active_star)

        val filePath = File(holder.itemView.context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString() + "/")
        val file = File(filePath, currentItem.modUri)

        if (file.exists()) { // If file exists
            holder.itemUploadIcon.setImageResource(R.drawable.downloaded)
        }

        holder.itemCard.setOnClickListener {
            val action = FavoritesFragmentDirections.actionFavoritesFragmentToDetailsFragment(currentItem)
            holder.itemView.findNavController().navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return modList.size
    }

    fun setData(mod: List<Mod>) {
        this.modList = mod
        notifyDataSetChanged()
    }
}