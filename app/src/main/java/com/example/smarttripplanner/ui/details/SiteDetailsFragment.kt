package com.example.smarttripplanner.ui.details

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.smarttripplanner.R
import com.example.smarttripplanner.data.model.SavedSite
import com.example.smarttripplanner.databinding.SiteDetailsLayoutBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SiteDetailsFragment : Fragment() {

    private var _binding: SiteDetailsLayoutBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SiteDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SiteDetailsLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val placeId = arguments?.getString("placeId").orEmpty()
        if (placeId.isBlank()) return

        viewModel.setPlaceId(placeId)

        viewModel.site.observe(viewLifecycleOwner) { site ->
            if (site == null) return@observe
            bindSite(site)
        }

        viewModel.refreshMissingDetails(placeId)
    }

    private fun bindSite(site: SavedSite) {
        binding.tvDetailedSiteName.text = site.name
        binding.tvDetailedSiteCategory.text = site.category
        binding.tvDetailedSiteRating.text = site.rating?.let { "★ $it" } ?: "Rating unavailable"
        binding.tvDetailedSiteDescription.text =
            site.description ?: "No description available yet."

        Glide.with(binding.ivDetailedSiteImage)
            .load(site.imageUrl.toImageModel())
            .placeholder(R.drawable.outline_mode_of_travel_24)
            .error(R.drawable.outline_mode_of_travel_24)
            .fallback(R.drawable.outline_mode_of_travel_24)
            .into(binding.ivDetailedSiteImage)

        val siteUrl = site.siteUrl
        binding.btnOpenOfficialWebsite.visibility =
            if (siteUrl.isNullOrBlank()) View.GONE else View.VISIBLE
        binding.btnOpenOfficialWebsite.setOnClickListener {
            if (!siteUrl.isNullOrBlank()) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(siteUrl)))
            }
        }
    }

    private fun String?.toImageModel(): Any? {
        if (isNullOrBlank()) return null
        if (startsWith("http://") || startsWith("https://")) return this
        val resourceId = resources.getIdentifier(this, "drawable", requireContext().packageName)
        return resourceId.takeIf { it != 0 }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
