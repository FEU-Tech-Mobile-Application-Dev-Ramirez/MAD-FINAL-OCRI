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

class ProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                FoodTravelerTheme {
                    ProfileScreen()
                }
            }
        }
    }
}

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Picture
        Image(
            painter = painterResource(id = R.drawable.profile), // Updated to use profile.png
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(50.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        // User Name
        Text(
            text = "John Doe", // Replace with dynamic user name
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        // User Email
        Text(
            text = "john.doe@example.com", // Replace with dynamic email
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Favorite Restaurants Header
        Text(
            text = "Favorite Restaurants",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // List of Favorite Restaurants
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(sampleFavoriteRestaurants) { restaurant ->
                FavoriteRestaurantCard(restaurant)
            }
        }
    }
}

@Composable
fun FavoriteRestaurantCard(restaurant: Restaurant) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Restaurant Image
            Image(
                painter = painterResource(id = R.drawable.restaurant1), // Replace with dynamic image resource
                contentDescription = restaurant.name,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Restaurant Info
            Column {
                Text(
                    text = restaurant.name,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = restaurant.cuisine,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// Sample data for favorite restaurants
private val sampleFavoriteRestaurants = listOf(
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
    )
) 