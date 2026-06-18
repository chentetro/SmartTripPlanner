package com.example.smarttripplanner.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.smarttripplanner.R
import com.example.smarttripplanner.databinding.RegisterLayoutBinding

class RegisterFragment : Fragment() {
    private var _binding: RegisterLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = RegisterLayoutBinding.inflate(inflater, container, false)
        
        // מעבר חזרה ללוגין בלחיצה על כפתור ההרשמה (סימולציה)
        binding.btnCreateAccount.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
        
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}