package com.example.smarttripplanner.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smarttripplanner.R
import com.example.smarttripplanner.data.model.Destination
import com.example.smarttripplanner.databinding.HomeLayoutBinding
import com.example.smarttripplanner.ui.adapters.PopularDestAdapter
import com.example.smarttripplanner.ui.adapters.RecommendedAdapter

class HomeFragment : Fragment() {

    private var _binding: HomeLayoutBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    private val popularAdapter  = PopularDestAdapter  { navigateToDetails(it) }
    private val recommendedAdapter = RecommendedAdapter { navigateToDetails(it) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = HomeLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews()
        observeViewModel()
        viewModel.loadDestinations()
    }

    private fun setupRecyclerViews() {
        binding.rvPopular.apply {
            adapter = popularAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
        binding.rvRecommended.adapter = recommendedAdapter
    }

    private fun observeViewModel() {
        viewModel.popularDestinations.observe(viewLifecycleOwner) {
            popularAdapter.submitList(it)
        }
        viewModel.recommendedDestinations.observe(viewLifecycleOwner) {
            recommendedAdapter.submitList(it)
        }
    }

    private fun navigateToDetails(dest: Destination) {
        val bundle = Bundle().apply {
            putString("destinationId",  dest.id)
            putString("name",           dest.name)
            putString("location",       dest.location)
            putString("imageUrl",       dest.imageUrl)
            putFloat ("rating",         dest.rating)
            putInt   ("temperature",    dest.temperature)
            putInt   ("price",          dest.price)
            putString("description",    dest.description)
        }
        findNavController().navigate(R.id.action_homeFragment_to_placeDetailsFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}