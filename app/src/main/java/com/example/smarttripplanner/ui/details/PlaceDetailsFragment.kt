package com.example.smarttripplanner.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.smarttripplanner.R                        // ← ADD THIS IMPORT
import com.example.smarttripplanner.data.local.AppDatabase
import com.example.smarttripplanner.data.model.FavoriteEntity
import com.example.smarttripplanner.data.repository.TripRepository
import com.example.smarttripplanner.databinding.FragmentDetailsBinding
import kotlinx.coroutines.launch

class PlaceDetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id          = arguments?.getString("destinationId") ?: ""
        val name        = arguments?.getString("name")          ?: ""
        val location    = arguments?.getString("location")      ?: ""
        val imageUrl    = arguments?.getString("imageUrl")      ?: ""
        val description = arguments?.getString("description")   ?: ""
        val rating      = arguments?.getFloat("rating")         ?: 4.8f
        val temperature = arguments?.getInt("temperature")      ?: 24
        val price       = arguments?.getInt("price")            ?: 120

        with(binding) {
            tvPlaceTitle.text  = name
            tvLocation.text    = location
            tvDescription.text = description.ifEmpty {
                "A beautiful destination worth exploring."
            }

            // Load destination image
            Glide.with(ivPlaceHero)
                .load(imageUrl.ifEmpty { null })
                .placeholder(R.drawable.big_blue_circle)
                .error(R.drawable.big_blue_circle)
                .centerCrop()
                .into(ivPlaceHero)

            // Back button
            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }

            // Favorite button — observe real-time state from Room
            val dao = AppDatabase.getDatabase(requireContext()).favoriteDao()

            dao.isFavorite(id).observe(viewLifecycleOwner) { isFav ->
                btnFav.setImageResource(
                    if (isFav) R.drawable.ic_favorite        // ← now resolves correctly
                    else       R.drawable.ic_favorite_border // ← now resolves correctly
                )
            }

            btnFav.setOnClickListener {
                lifecycleScope.launch {
                    val repo = TripRepository(dao)
                    repo.addFavorite(
                        FavoriteEntity(id, name, location, imageUrl, description)
                    )
                    Toast.makeText(
                        requireContext(), "$name added to favorites!", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}