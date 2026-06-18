package com.example.smarttripplanner.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.smarttripplanner.R
import com.example.smarttripplanner.data.model.Destination
import com.example.smarttripplanner.databinding.ItemRecommendedBinding

class RecommendedAdapter(
    private val onItemClick: (Destination) -> Unit
) : RecyclerView.Adapter<RecommendedAdapter.VH>() {

    private var list = listOf<Destination>()

    fun submitList(newList: List<Destination>) {
        list = newList
        notifyDataSetChanged()
    }

    inner class VH(val binding: ItemRecommendedBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemRecommendedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val dest = list[position]
        with(holder.binding) {
            tvRecTitle.text = dest.name
            tvRecSub.text   = dest.location
            Glide.with(ivRecImage)
                .load(dest.imageUrl.ifEmpty { null })
                .placeholder(R.drawable.big_blue_circle)
                .error(getLocalImage(dest.name))         // ← fix
                .centerCrop()
                .into(ivRecImage)
        }
        holder.itemView.setOnClickListener { onItemClick(dest) }
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