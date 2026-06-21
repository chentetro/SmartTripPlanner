package com.example.smarttripplanner.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smarttripplanner.R
import com.example.smarttripplanner.data.model.Trip
import com.example.smarttripplanner.databinding.ItemTripBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TripAdapter(
    private val onTripClick: (Trip) -> Unit
) : RecyclerView.Adapter<TripAdapter.TripViewHolder>() {

    var currentList: List<Trip> = emptyList()
        private set

    fun submitList(newList: List<Trip>) {
        currentList = newList
        notifyDataSetChanged()
    }

    fun removeItem(position: Int, newList: List<Trip>) {
        currentList = newList
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, currentList.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val binding = ItemTripBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TripViewHolder(binding, onTripClick)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    override fun getItemCount(): Int = currentList.size

    class TripViewHolder(
        private val binding: ItemTripBinding,
        private val onTripClick: (Trip) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(trip: Trip) {
            val context = binding.root.context
            val dateFormatter = SimpleDateFormat(
                context.getString(R.string.trip_date_format),
                Locale.getDefault()
            )

            binding.tvTripName.text = trip.tripName
            binding.tvTripDate.text = dateFormatter.format(Date(trip.tripDate))
            binding.tvTripTimeRange.text = context.getString(
                R.string.trip_time_range_format,
                trip.totalStartTime,
                trip.totalEndTime
            )
            binding.tvTripMeta.text = context.getString(
                R.string.trip_meta_format,
                trip.vibe,
                trip.participantsCount,
                trip.maxDistance.toInt()
            )

            binding.ivTripFavorite.visibility = if (trip.isFavorite) View.VISIBLE else View.GONE

            binding.root.setOnClickListener {
                onTripClick(trip)
            }
        }
    }
}
