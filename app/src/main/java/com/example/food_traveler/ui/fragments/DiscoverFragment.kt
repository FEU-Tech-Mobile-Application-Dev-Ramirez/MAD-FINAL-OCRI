package com.example.food_traveler.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.food_traveler.R
import com.example.food_traveler.data.Restaurant
import com.example.food_traveler.ui.screens.DiscoverScreen
import com.example.food_traveler.ui.theme.FoodTravelerTheme

class DiscoverFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                FoodTravelerTheme {
                    DiscoverScreen(
                        onRestaurantClick = { restaurant ->
                            // Navigate to details fragment with the restaurant ID
                            findNavController().navigate(
                                R.id.action_discoverFragment_to_detailsFragment,
                                bundleOf("restaurantId" to restaurant.id)
                            )
                        }
                    )
                }
            }
        }
    }
} 