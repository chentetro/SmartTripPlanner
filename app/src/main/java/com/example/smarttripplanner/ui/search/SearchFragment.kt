package com.example.smarttripplanner.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.smarttripplanner.R
import com.example.smarttripplanner.data.local.AppDatabase
import com.example.smarttripplanner.data.repository.TripRepository
import com.example.smarttripplanner.databinding.FragmentSearchBinding
import com.example.smarttripplanner.ui.adapters.RecommendedAdapter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private var searchJob: Job? = null

    private val adapter = RecommendedAdapter { dest ->
        val bundle = Bundle().apply {
            putString("destinationId", dest.id)
            putString("name",          dest.name)
            putString("location",      dest.location)
            putString("imageUrl",      dest.imageUrl)
            putString("description",   dest.description)
        }
        findNavController().navigate(R.id.action_searchFragment_to_placeDetailsFragment, bundle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvSearchResults.adapter = adapter

        val repo = TripRepository(AppDatabase.getDatabase(requireContext()).favoriteDao())

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                searchJob?.cancel()
                searchJob = lifecycleScope.launch {
                    delay(400) // debounce
                    val results = repo.searchDestinations(s.toString())
                    adapter.submitList(results)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}