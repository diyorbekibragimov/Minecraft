package com.example.minecraft.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.RecyclerView
import com.example.minecraft.DetailDialogFragment
import com.example.minecraft.ModViewModel
import com.example.minecraft.R
import com.example.minecraft.data.Mod
import com.example.minecraft.main_screen.MainFragment

class RecyclerAdapter(var mModViewModel: ModViewModel) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>(), ChangeFavState, DetailDialogFragment.Listener {
    private var modList = emptyList<Mod>()
    private var isDownloadedList = mutableMapOf<Int, Boolean>()
    private var isFavMainList = mutableMapOf<Int, Boolean>()
    private var mLastClickTime = System.currentTimeMillis()
    private val CLICK_TIME_INTERVAL: Long = 300

    override fun updateData(isFav: Boolean, position: Int) {
        isFavMainList[position] = isFav
        changeMainData(isFavMainList)
    }

    override fun changeUploadIcon(isImported: Boolean, position: Int) {
        isDownloadedList[position] = isImported
        changeMainIcon(isDownloadedList)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemTitle: TextView
        var itemContent: TextView
        var itemIsFavIcon: ImageView
        var itemUploadIcon: ImageView
        var itemImage: ImageView

        init {
            itemTitle = itemView.findViewById(R.id.title)
            itemContent = itemView.findViewById(R.id.content)
            itemIsFavIcon = itemView.findViewById(R.id.favButton)
            itemUploadIcon = itemView.findViewById(R.id.dwnButton)
            itemImage = itemView.findViewById(R.id.placeholder)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(parent.context).inflate(
                        R.layout.list_item,
                        parent,
                        false
                )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = modList[position]

        holder.itemTitle.text = currentItem.title
        holder.itemContent.text = currentItem.content

        val image = holder.itemView.context.assets.open("images/${currentItem.imageUrl}")
        val bitmap = BitmapFactory.decodeStream(image)
        holder.itemImage.setImageBitmap(bitmap)

        manageUploadIcon(currentItem, holder, position)
        manageFavIcon(currentItem, holder, position)

        holder.itemView.setOnClickListener {
            onClickItem(currentItem, position, holder)
        }
    }

    private fun onClickItem(currentItem: Mod, position: Int, holder: ViewHolder): Any? {
        val now = System.currentTimeMillis()
        if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
            return null
        }
        mLastClickTime = now
        openDialog(currentItem, position, holder)
        return true
    }

    private fun openDialog(currentItem: Mod, position: Int, holder: ViewHolder) {
        val dialog = DetailDialogFragment(currentItem, position, "Main")
        dialog.setListener(this)
        dialog.show(holder.itemView.findFragment<MainFragment>().requireFragmentManager(), "detailDialog")
    }

    override fun getItemCount(): Int {
        return modList.size
    }

    fun setData(mod: List<Mod>) {
        this.modList = mod
        notifyDataSetChanged()
    }

    private fun changeItemFavIcon(currentItem: Mod, holder: ViewHolder, position: Int) {
        val newState = changeState(currentItem, mModViewModel)

        if (newState) {
            holder.itemIsFavIcon.setImageResource(R.drawable.ic_active_star)
        } else {
            holder.itemIsFavIcon.setImageResource(R.drawable.ic_inactive_star)
        }
        notifyItemChanged(position)
        isFavMainList[position] = newState
        changeMainData(isFavMainList)
    }

    private fun manageUploadIcon(currentItem: Mod, holder: ViewHolder, position: Int) {
        if (isDownloadedList.contains(position)) {
            if (isDownloadedList[position] == true) {
                holder.itemUploadIcon.setImageResource(R.drawable.downloaded)
            } else {
                holder.itemUploadIcon.setImageResource(R.drawable.ic_download)
            }
        } else {
            isDownloadedList[position] = currentItem.isImported
            if (isDownloadedList[position] == true) {
                holder.itemUploadIcon.setImageResource(R.drawable.downloaded)
            } else {
                holder.itemUploadIcon.setImageResource(R.drawable.ic_download)
            }
        }
    }

    private fun manageFavIcon(currentItem: Mod, holder: ViewHolder, position: Int) {
        if (isFavMainList.contains(position)) {
            if (isFavMainList[position] == true) {
                holder.itemIsFavIcon.setImageResource(R.drawable.ic_active_star)
            } else {
                holder.itemIsFavIcon.setImageResource(R.drawable.ic_inactive_star)
            }
        } else {
            isFavMainList[position] = currentItem.isFav
            if (isFavMainList[position] == true) {
                holder.itemIsFavIcon.setImageResource(R.drawable.ic_active_star)
            } else {
                holder.itemIsFavIcon.setImageResource(R.drawable.ic_inactive_star)
            }
        }

        holder.itemIsFavIcon.setOnClickListener {
            changeItemFavIcon(currentItem, holder, position)
        }
    }

     fun changeMainData(isFavList: MutableMap<Int, Boolean>) {
        this.isFavMainList = isFavList
        notifyDataSetChanged()
    }

    fun changeMainIcon(isDownloadedList: MutableMap<Int, Boolean>) {
        this.isDownloadedList = isDownloadedList
        notifyDataSetChanged()
    }
}