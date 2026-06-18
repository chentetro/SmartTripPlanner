package com.example.smarttripplanner.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.smarttripplanner.R
import com.example.smarttripplanner.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHost = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHost.navController

        binding.bottomNav.setupWithNavController(navController)

        // Hide bottom nav on auth + detail screens
        navController.addOnDestinationChangedListener { _, dest, _ ->
            binding.bottomNav.visibility = when (dest.id) {
                R.id.loginFragment,
                R.id.registerFragment,
                R.id.placeDetailsFragment -> View.GONE
                else -> View.VISIBLE
            }
        }
    }
}