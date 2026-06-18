package com.example.smarttripplanner.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.smarttripplanner.R
import com.example.smarttripplanner.databinding.HomeLayoutBinding

class HomeFragment : Fragment (){

    private var _binding: HomeLayoutBinding? = null

    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = HomeLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCreateTrip.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_questionnaireFragment)
        }

        binding.btnMyTrips.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_tripsFragment)
        }

        binding.btnFavorites.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_favoritesFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}