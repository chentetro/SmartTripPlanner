package com.example.smarttripplanner.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.smarttripplanner.R
import com.example.smarttripplanner.data.model.SavedSite
import com.example.smarttripplanner.databinding.ItemSiteBinding

class SiteAdapter(
    private val onSiteClick: (String) -> Unit,
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
        private val onSiteClick: (String) -> Unit,
        private val onDeleteClick: (SavedSite) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(site: SavedSite) {
            val orderPrefix = site.visitOrder?.let { "$it. " } ?: ""
            binding.tvSiteOrderAndName.text = "$orderPrefix${site.name}"
            binding.tvSiteCategory.text = site.category

            if (site.rating.isNullOrBlank()) {
                binding.tvSiteRating.visibility = View.GONE
            } else {
                binding.tvSiteRating.visibility = View.VISIBLE
                binding.tvSiteRating.text = "★ ${site.rating}"
            }

            Glide.with(binding.ivSiteImage)
                .load(site.imageUrl.toImageModel(binding.root.context))
                .placeholder(R.drawable.outline_mode_of_travel_24)
                .error(R.drawable.outline_mode_of_travel_24)
                .fallback(R.drawable.outline_mode_of_travel_24)
                .into(binding.ivSiteImage)

            binding.root.setOnClickListener { onSiteClick(site.placeId) }
            binding.btnDeleteSite.setOnClickListener { onDeleteClick(site) }
        }

        private fun String?.toImageModel(context: android.content.Context): Any? {
            if (isNullOrBlank()) return null
            if (startsWith("http://") || startsWith("https://")) return this
            val resourceId = context.resources.getIdentifier(this, "drawable", context.packageName)
            return resourceId.takeIf { it != 0 }
        }
    }

    private class SiteDiffCallback : DiffUtil.ItemCallback<SavedSite>() {
        override fun areItemsTheSame(oldItem: SavedSite, newItem: SavedSite): Boolean =
            oldItem.siteId == newItem.siteId

        override fun areContentsTheSame(oldItem: SavedSite, newItem: SavedSite): Boolean =
            oldItem == newItem
    }
}
