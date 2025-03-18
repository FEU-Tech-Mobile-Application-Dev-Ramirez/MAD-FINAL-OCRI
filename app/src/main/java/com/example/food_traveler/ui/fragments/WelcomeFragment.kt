package com.example.food_traveler.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.food_traveler.R

class WelcomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the Get Started button click listener
        view.findViewById<Button>(R.id.btnGetStarted).setOnClickListener {
            try {
                // Check if fragment is still attached to activity
                if (isAdded) {
                    findNavController().navigate(R.id.action_welcomeFragment_to_discoverFragment)
                }
            } catch (e: Exception) {
                Log.e("WelcomeFragment", "Error navigating: ${e.message}")
            }
        }
    }
} 