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
import com.example.minecraft.main_screen.MainFragmentDirections
import com.google.android.material.card.MaterialCardView
import java.io.File

class RecyclerAdapter(var mModViewModel: ModViewModel) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>(), ChangeFavState {
    private var modList = emptyList<Mod>()
    private var isFavList = mutableMapOf<Int, Boolean>()

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
            itemCard = itemView.findViewById(R.id.item_card)
            itemUploadIcon = itemView.findViewById(R.id.dwnButton)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = ViewHolder(
                LayoutInflater.from(parent.context).inflate(
                        R.layout.list_item,
                        parent,
                        false
                )
        )

        v.itemIsFavIcon.setOnClickListener {
            val currentItem = modList[v.adapterPosition]
            val newState = changeState(currentItem, mModViewModel)

            if (newState) {
                v.itemIsFavIcon.setImageResource(R.drawable.ic_active_star)
            } else {
                v.itemIsFavIcon.setImageResource(R.drawable.ic_inactive_star)
            }
            notifyItemChanged(v.adapterPosition)
            isFavList[v.adapterPosition] = newState
            changeData(isFavList)
        }

        return v
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = modList[position]

        holder.itemTitle.text = currentItem.title
        holder.itemContent.text = currentItem.content

        val filePath = File(holder.itemView.context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString() + "/")
        val file = File(filePath, currentItem.modUri)

        if (file.exists()) { // If file exists
            holder.itemUploadIcon.setImageResource(R.drawable.downloaded)
        }

        if (isFavList.contains(position)) {
            if (isFavList[position] == true) {
                holder.itemIsFavIcon.setImageResource(R.drawable.ic_active_star)
            } else {
                holder.itemIsFavIcon.setImageResource(R.drawable.ic_inactive_star)
            }
        } else {
            isFavList.put(position, currentItem.isFav)
            if (isFavList[position] == true) {
                holder.itemIsFavIcon.setImageResource(R.drawable.ic_active_star)
            } else {
                holder.itemIsFavIcon.setImageResource(R.drawable.ic_inactive_star)
            }
        }

        holder.itemCard.setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToDetailsFragment(currentItem)
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

    fun changeData(isFavList: MutableMap<Int, Boolean>) {
        this.isFavList = isFavList
        notifyDataSetChanged()
    }
}