package com.example.smarttripplanner.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.smarttripplanner.R
import com.example.smarttripplanner.data.model.SavedSite
import com.example.smarttripplanner.databinding.ItemSiteBinding

class SiteAdapter(
    private val onSiteClick: (String) -> Unit,
    private val onDeleteClick: (SavedSite) -> Unit
) : RecyclerView.Adapter<SiteAdapter.SiteViewHolder>() {

    var currentList: List<SavedSite> = emptyList()
        private set

    fun submitList(newList: List<SavedSite>) {
        currentList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SiteViewHolder {
        val binding = ItemSiteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SiteViewHolder(binding, onSiteClick, onDeleteClick)
    }

    override fun onBindViewHolder(holder: SiteViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    override fun getItemCount(): Int = currentList.size

    class SiteViewHolder(
        private val binding: ItemSiteBinding,
        private val onSiteClick: (String) -> Unit,
        private val onDeleteClick: (SavedSite) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(site: SavedSite) {
            val context = binding.root.context
            binding.tvSiteOrderAndName.text = site.visitOrder?.let { visitOrder ->
                context.getString(R.string.site_order_name_format, visitOrder, site.name)
            } ?: site.name
            binding.tvSiteCategory.text = site.category

            if (site.rating.isNullOrBlank()) {
                binding.tvSiteRating.visibility = View.GONE
            } else {
                binding.tvSiteRating.visibility = View.VISIBLE
                binding.tvSiteRating.text = context.getString(R.string.site_rating_format, site.rating)
            }

            val photoBytes = site.photoBytes
            if (photoBytes != null) {
                Glide.with(binding.root.context)
                    .load(photoBytes)
                    .placeholder(R.drawable.outline_mode_of_travel_24)
                    .error(R.drawable.outline_mode_of_travel_24)
                    .into(binding.ivSiteImage)
            } else {
                binding.ivSiteImage.setImageResource(R.drawable.outline_mode_of_travel_24)
            }

            binding.root.setOnClickListener { onSiteClick(site.placeId) }
            binding.btnDeleteSite.setOnClickListener { onDeleteClick(site) }
        }
    }
}
