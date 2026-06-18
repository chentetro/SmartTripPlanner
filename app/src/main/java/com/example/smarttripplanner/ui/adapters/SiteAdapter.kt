package com.example.smarttripplanner.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.smarttripplanner.R
import com.example.smarttripplanner.data.model.SavedSite
import com.example.smarttripplanner.databinding.ItemSiteBinding

class SiteAdapter(
    private val onSiteClick: (SavedSite) -> Unit,
    private val onDeleteClick: (SavedSite) -> Unit
) : ListAdapter<SavedSite, SiteAdapter.SiteViewHolder>(SiteDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SiteViewHolder {
        val binding = ItemSiteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SiteViewHolder(binding, onSiteClick, onDeleteClick)
    }

    override fun onBindViewHolder(holder: SiteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class SiteViewHolder(
        private val binding: ItemSiteBinding,
        private val onSiteClick: (SavedSite) -> Unit,
        private val onDeleteClick: (SavedSite) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(site: SavedSite) {
            val orderPrefix = site.visitOrder?.let { "$it. " } ?: ""
            binding.tvSiteOrderAndName.text = "$orderPrefix${site.name}"
            binding.tvSiteCategory.text = site.category
            binding.tvSiteRating.text = "★ ${site.rating ?: "N/A"}"

            if (site.imageUrl.isNullOrBlank()) {
                binding.ivSiteImage.setImageResource(R.drawable.outline_mode_of_travel_24)
            } else {
                // Glide can load site.imageUrl here when the dependency is added.
                binding.ivSiteImage.setImageResource(R.drawable.outline_mode_of_travel_24)
            }

            binding.root.setOnClickListener { onSiteClick(site) }
            binding.btnDeleteSite.setOnClickListener { onDeleteClick(site) }
        }
    }

    private class SiteDiffCallback : DiffUtil.ItemCallback<SavedSite>() {
        override fun areItemsTheSame(oldItem: SavedSite, newItem: SavedSite): Boolean =
            oldItem.siteId == newItem.siteId

        override fun areContentsTheSame(oldItem: SavedSite, newItem: SavedSite): Boolean =
            oldItem == newItem
    }
}
