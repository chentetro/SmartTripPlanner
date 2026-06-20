package com.example.smarttripplanner.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smarttripplanner.R
import com.example.smarttripplanner.databinding.TripDetailsBinding
import com.example.smarttripplanner.ui.adapters.SiteAdapter
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class TripDetailsFragment : Fragment() {

    private var _binding: TripDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TripDetailsViewModel by viewModels()
    private lateinit var siteAdapter: SiteAdapter
    private var currentTripId: Long = -1L
    private var isFavorite: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TripDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentTripId = arguments?.getLong("tripId", -1L) ?: -1L

        if (currentTripId == -1L) return

        siteAdapter = SiteAdapter(
            onSiteClick = { placeId ->
                findNavController().navigate(
                    R.id.action_tripDetailsFragment_to_siteDetailsFragment,
                    bundleOf("placeId" to placeId)
                )
            },
            onDeleteClick = { site ->
                viewModel.deleteSite(site.siteId)
            }
        )

        binding.rvTripSites.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = siteAdapter
        }

        val dateFormatter = SimpleDateFormat("EEEE, MMM d, yyyy", Locale.getDefault())

        viewModel.setTripId(currentTripId)

        viewModel.trip.observe(viewLifecycleOwner) { trip ->
            if (trip == null) return@observe

            isFavorite = trip.isFavorite
            binding.tvDetailTripName.text = trip.tripName
            binding.tvDetailTripDate.text = dateFormatter.format(Date(trip.tripDate))
            binding.tvDetailTripTimeRange.text = "${trip.totalStartTime} - ${trip.totalEndTime}"
            binding.tvDetailTripMeta.text =
                "Vibe: ${trip.vibe} • ${trip.participantsCount} participants • ${trip.maxDistance.toInt()} km radius"

            updateFavoriteButton(isFavorite)
        }

        binding.btnAddToFavorites.setOnClickListener {
            viewModel.toggleFavoriteStatus(currentTripId, isFavorite)
        }

        viewModel.savedSites.observe(viewLifecycleOwner) { sites ->
            siteAdapter.submitList(sites)
        }
    }

    private fun updateFavoriteButton(isFavorite: Boolean) {
        binding.btnAddToFavorites.text =
            if (isFavorite) "Remove from Favorites" else "Add to Favorites"
    }
    //להגדיר כאן שהADAPTER שלו זה SITEADAPTER

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
