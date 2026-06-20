package com.example.smarttripplanner.ui.trips

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smarttripplanner.R
import com.example.smarttripplanner.databinding.TripsLayoutBinding
import com.example.smarttripplanner.ui.adapters.TripAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TripsFragment : Fragment() {

    private var _binding: TripsLayoutBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TripsViewModel by viewModels()
    private lateinit var tripAdapter: TripAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TripsLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tripAdapter = TripAdapter { trip ->
            findNavController().navigate(
                R.id.action_tripsFragment_to_tripDetailsFragment,
                bundleOf("tripId" to trip.id)
            )
        }

        binding.rvTrips.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = tripAdapter
        }

        attachSwipeToDelete()

        viewModel.allTrips.observe(viewLifecycleOwner) { trips ->
            if (trips != tripAdapter.currentList) {
                tripAdapter.submitList(trips)
            }
            binding.tvEmptyTrips.visibility = if (trips.isNullOrEmpty()) View.VISIBLE else View.GONE
            binding.rvTrips.visibility = if (trips.isNullOrEmpty()) View.GONE else View.VISIBLE
        }
    }

    private fun attachSwipeToDelete() {
        val callback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                if (position == RecyclerView.NO_POSITION) return

                val trip = tripAdapter.currentList.getOrNull(position)
                if (trip == null) {
                    tripAdapter.notifyItemChanged(position)
                    return
                }

                viewModel.deleteTrip(trip.id)
                val updatedList = tripAdapter.currentList.toMutableList().apply {
                    removeAt(position)
                }
                tripAdapter.removeItem(position, updatedList)
            }
        }

        ItemTouchHelper(callback).attachToRecyclerView(binding.rvTrips)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
