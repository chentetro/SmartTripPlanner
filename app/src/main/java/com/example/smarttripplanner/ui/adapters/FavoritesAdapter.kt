package com.example.smarttripplanner.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.smarttripplanner.R
import com.example.smarttripplanner.data.model.FavoriteEntity
import com.example.smarttripplanner.databinding.ItemFavoriteBinding

class FavoritesAdapter(
    private val onItemClick: (FavoriteEntity) -> Unit,
    private val onRemoveClick: (FavoriteEntity) -> Unit
) : RecyclerView.Adapter<FavoritesAdapter.VH>() {

    private var list = listOf<FavoriteEntity>()

    fun submitList(newList: List<FavoriteEntity>) {
        list = newList
        notifyDataSetChanged()
    }

    inner class VH(val binding: ItemFavoriteBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemFavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val fav = list[position]
        with(holder.binding) {
            tvFavTitle.text = fav.name
            Glide.with(ivFavImage)
                .load(fav.imageUrl.ifEmpty { null })
                .placeholder(R.drawable.big_blue_circle)
                .error(getLocalImage(fav.name))          // ← fix
                .centerCrop()
                .into(ivFavImage)
            btnRemoveFav.setOnClickListener { onRemoveClick(fav) }
        }
        holder.itemView.setOnClickListener { onItemClick(fav) }
    }

    private fun getLocalImage(name: String): Int = when (name.lowercase()) {
        "paris"     -> R.drawable.dest_paris
        "bali"    -> R.drawable.dest_bali
        "new york"  -> R.drawable.dest_newyork
        "tokyo"     -> R.drawable.dest_tokyo
        "maldives"     -> R.drawable.dest_maldives
        "rome" -> R.drawable.dest_rome
        else        -> R.drawable.big_blue_circle
    }
}