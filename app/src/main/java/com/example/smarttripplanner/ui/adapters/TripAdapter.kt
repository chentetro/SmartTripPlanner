package com.example.smarttripplanner.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.smarttripplanner.data.model.Trip
import com.example.smarttripplanner.databinding.ItemTripBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TripAdapter(
    private val onTripClick: (Trip) -> Unit
) : ListAdapter<Trip, TripAdapter.TripViewHolder>(TripDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val binding = ItemTripBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TripViewHolder(binding, onTripClick)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TripViewHolder(
        private val binding: ItemTripBinding,
        private val onTripClick: (Trip) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormatter = SimpleDateFormat("EEEE, MMM d, yyyy", Locale.getDefault())

        fun bind(trip: Trip) {
            binding.tvTripName.text = trip.tripName
            binding.tvTripDate.text = dateFormatter.format(Date(trip.tripDate))
            binding.tvTripTimeRange.text = "${trip.totalStartTime} - ${trip.totalEndTime}"
            binding.tvTripMeta.text =
                "${trip.vibe} • ${trip.participantsCount} participants • ${trip.maxDistance.toInt()} km"

            binding.ivTripFavorite.visibility = if (trip.isFavorite) View.VISIBLE else View.GONE

            binding.root.setOnClickListener {
                onTripClick(trip)
            }
        }
    }

    private class TripDiffCallback : DiffUtil.ItemCallback<Trip>() {
        override fun areItemsTheSame(oldItem: Trip, newItem: Trip): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Trip, newItem: Trip): Boolean =
            oldItem == newItem
    }
}
