package com.example.smarttripplanner.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smarttripplanner.R
import com.example.smarttripplanner.data.repository.TripRepository
import com.example.smarttripplanner.databinding.FavoritesLayoutBinding
import com.example.smarttripplanner.ui.adapters.TripAdapter

class FavoritesFragment : Fragment() {

    private var _binding: FavoritesLayoutBinding? = null
    private val binding get() = _binding!!

    private lateinit var tripRepository: TripRepository
    private lateinit var tripAdapter: TripAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FavoritesLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tripRepository = TripRepository(requireActivity().application)

        tripAdapter = TripAdapter { trip ->
            findNavController().navigate(
                R.id.action_favoritesFragment_to_tripDetailsFragment,
                bundleOf("tripId" to trip.id)
            )
        }

        binding.rvFavoriteTrips.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = tripAdapter
        }

        tripRepository.getFavoriteTrips().observe(viewLifecycleOwner) { trips ->
            tripAdapter.submitList(trips)
            binding.tvEmptyFavorites.visibility = if (trips.isNullOrEmpty()) View.VISIBLE else View.GONE
            binding.rvFavoriteTrips.visibility = if (trips.isNullOrEmpty()) View.GONE else View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
