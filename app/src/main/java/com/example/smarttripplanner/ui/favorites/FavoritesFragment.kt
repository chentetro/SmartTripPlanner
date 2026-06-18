package com.example.smarttripplanner.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.smarttripplanner.R
import com.example.smarttripplanner.databinding.FragmentFavoritesBinding
import com.example.smarttripplanner.ui.adapters.FavoritesAdapter

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FavoritesViewModel by viewModels()

    private val adapter = FavoritesAdapter(
        onItemClick = { fav ->
            val bundle = Bundle().apply {
                putString("destinationId", fav.id)
                putString("name",         fav.name)
                putString("location",     fav.location)
                putString("imageUrl",     fav.imageUrl)
                putString("description",  fav.description)
            }
            findNavController().navigate(R.id.action_favoritesFragment_to_placeDetailsFragment, bundle)
        },
        onRemoveClick = { fav -> viewModel.removeFavorite(fav.id) }
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvFavorites.adapter = adapter
        viewModel.favorites.observe(viewLifecycleOwner) { adapter.submitList(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}