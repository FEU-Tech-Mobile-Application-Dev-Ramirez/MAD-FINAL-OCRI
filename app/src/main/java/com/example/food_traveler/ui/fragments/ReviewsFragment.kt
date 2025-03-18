package com.example.food_traveler.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.example.food_traveler.R
import com.example.food_traveler.data.Restaurant
import com.example.food_traveler.ui.theme.FoodTravelerTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.ComposeView

class ReviewsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                FoodTravelerTheme {
                    ReviewsScreen()
                }
            }
        }
    }
}

@Composable
fun ReviewsScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(sampleRestaurants) { restaurant ->
            ReviewCard(restaurant = restaurant)
        }
    }
}

@Composable
fun ReviewCard(restaurant: Restaurant) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            // Use the restaurant image from the drawable folder based on ID
            val restaurantImage = when (restaurant.id) {
                "1" -> R.drawable.restaurant1
                "2" -> R.drawable.restaurant2
                "3" -> R.drawable.restaurant3
                else -> null // No local image available
            }

            if (restaurantImage != null) {
                Image(
                    painter = painterResource(id = restaurantImage),
                    contentDescription = restaurant.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            // Restaurant Info
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = restaurant.name,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Sample reviews
                Text(
                    text = "This place has amazing ${restaurant.cuisine} food! Highly recommend the signature dishes.",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Rating: ${restaurant.rating} ⭐",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

// Sample data for testing
private val sampleRestaurants = listOf(
    Restaurant(
        id = "1",
        name = "La Piazza",
        cuisine = "Italian",
        rating = 4.5f,
        priceLevel = 3,
        imageUrl = "restaurant1.jpg",
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
        imageUrl = "restaurant2.jpg",
        address = "456 Oak Ave, Anytown",
        distance = 0.8f,
        country = "Japan"
    ),
    Restaurant(
        id = "3",
        name = "Café Gourmet",
        cuisine = "French",
        rating = 4.8f,
        priceLevel = 5,
        imageUrl = "restaurant3.jpg",
        address = "789 Elm St, Anytown",
        distance = 1.5f,
        country = "France"
    )
)