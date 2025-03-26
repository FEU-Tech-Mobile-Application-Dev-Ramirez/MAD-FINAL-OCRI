package com.example.food_traveler.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.food_traveler.data.PostRepository
import com.example.food_traveler.data.UserRepository
import com.example.food_traveler.ui.components.CommunityFeedScreen
import com.example.food_traveler.ui.theme.FoodTravelerTheme

class CommunityFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                FoodTravelerTheme {
                    CommunityFeedScreen(
                        onPostRate = { postId, rating ->
                            UserRepository.getCurrentUser()?.let { user ->
                                PostRepository.ratePost(postId, user.id, rating)
                            }
                        }
                    )
                }
            }
        }
    }
} 