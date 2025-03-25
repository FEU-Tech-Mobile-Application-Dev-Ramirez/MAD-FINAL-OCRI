package com.example.food_traveler.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.food_traveler.R
import com.example.food_traveler.data.Restaurant

class DetailsFragment : Fragment() {
    
    private var restaurantId: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            restaurantId = it.getString("restaurantId")
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val textView = view.findViewById<TextView>(R.id.detailsText)
        
        // Find the restaurant by ID
        val restaurant = findRestaurantById(restaurantId)
        
        if (restaurant != null) {
            textView.text = "Details for ${restaurant.name}\n" +
                    "Cuisine: ${restaurant.cuisine}\n" +
                    "Country: ${restaurant.country}\n" +
                    "Rating: ${restaurant.rating}\n" +
                    "Address: ${restaurant.address}"
        } else {
            textView.text = "Restaurant not found"
        }
    }
    
    private fun findRestaurantById(id: String?): Restaurant? {
        if (id == null) return null
        
        // This is a simple example. In a real app, you would get this from a database or API
        val sampleRestaurants = listOf(
            Restaurant(
                id = "1",
                name = "La Piazza",
                cuisine = "Italian",
                rating = 4.5f,
                priceLevel = 3,
                imageUrl = "italian.jpg",
                address = "123 Main St, Anytown",
                distance = 1.2f,
                country = "Italy"
            ),
            Restaurant(
                id = "2",
                name = "Sushi Master",
                cuisine = "Japanese",
                rating = 4.7f,
                priceLevel = 4,
                imageUrl = "sushi.jpg",
                address = "456 Oak Ave, Anytown",
                distance = 0.8f,
                country = "Japan"
            ),
            Restaurant(
                id = "3",
                name = "Taco Fiesta",
                cuisine = "Mexican",
                rating = 4.2f,
                priceLevel = 2,
                imageUrl = "mexican.jpg",
                address = "789 Pine Blvd, Anytown",
                distance = 1.5f,
                country = "Mexico"
            )
        )
        
        return sampleRestaurants.find { it.id == id }
    }
}