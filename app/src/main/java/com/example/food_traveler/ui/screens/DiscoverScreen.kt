package com.example.food_traveler.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.food_traveler.data.Restaurant
import com.example.food_traveler.R
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import android.util.Log

@Composable
fun DiscoverScreen(
    onRestaurantClick: (Restaurant) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCuisine by remember { mutableStateOf<String?>(null) }
    
    Column(modifier = Modifier.fillMaxSize()) {
        // App Bar with search
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        
        // Cuisine filters
        CuisineFilters(
            selectedCuisine = selectedCuisine,
            onCuisineSelected = { selectedCuisine = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
        
        // Restaurant list
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val filteredRestaurants = sampleRestaurants
                .filter { restaurant ->
                    (searchQuery.isEmpty() || restaurant.name.contains(searchQuery, ignoreCase = true)) &&
                    (selectedCuisine == null || restaurant.cuisine == selectedCuisine)
                }
            
            items(filteredRestaurants) { restaurant ->
                RestaurantCard(
                    restaurant = restaurant,
                    onRestaurantClicked = { 
                        Log.d("DiscoverScreen", "Restaurant clicked: ${restaurant.name}")
                        onRestaurantClick(restaurant)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("Search restaurants...") },
        leadingIcon = { 
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search"
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear search"
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
fun CuisineFilters(
    selectedCuisine: String?,
    onCuisineSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val cuisines = listOf("Italian", "Japanese", "Mexican", "Indian", "Thai")
    
    ScrollableTabRow(
        selectedTabIndex = cuisines.indexOf(selectedCuisine).takeIf { it >= 0 } ?: 0,
        modifier = modifier,
        edgePadding = 0.dp,
        divider = {}
    ) {
        Tab(
            selected = selectedCuisine == null,
            onClick = { onCuisineSelected(null) },
            text = { Text("All") }
        )
        
        cuisines.forEach { cuisine ->
            Tab(
                selected = selectedCuisine == cuisine,
                onClick = { onCuisineSelected(cuisine) },
                text = { Text(cuisine) }
            )
        }
    }
}

@Composable
fun RestaurantCard(
    restaurant: Restaurant,
    modifier: Modifier = Modifier,
    onRestaurantClicked: (Restaurant) -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        onClick = { onRestaurantClicked(restaurant) }
    ) {
        Column {
            // Use the restaurant image from the drawable folder based on ID
            val restaurantImage = when (restaurant.id) {
                "1" -> R.drawable.restaurant1
                "2" -> R.drawable.restaurant2
                "3" -> R.drawable.restaurant3
                // Remove or comment out the line below if restaurant4 does not exist
                // "4" -> R.drawable.restaurant4 
                else -> null // No local image available
            }
            
            if (restaurantImage != null) {
                // Use local drawable image
                Image(
                    painter = painterResource(id = restaurantImage),
                    contentDescription = restaurant.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Use remote URL with AsyncImage
                AsyncImage(
                    model = restaurant.imageUrl,
                    contentDescription = restaurant.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = R.drawable.ic_broken_image),
                    placeholder = painterResource(id = R.drawable.ic_placeholder)
                )
            }
            
            // Restaurant Info
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = restaurant.name,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    PriceLevelIndicator(restaurant.priceLevel)
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Restaurant,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = restaurant.cuisine,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Address",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = restaurant.address,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PriceLevelIndicator(priceLevel: Int) {
    Row {
        repeat(priceLevel) {
            Icon(
                imageVector = Icons.Default.AttachMoney,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(16.dp)
            )
        }
        repeat(4 - priceLevel) {
            Icon(
                imageVector = Icons.Default.AttachMoney,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                modifier = Modifier.size(16.dp)
            )
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
        name = "Taco Fiesta",
        cuisine = "Mexican",
        rating = 4.2f,
        priceLevel = 2,
        imageUrl = "restaurant3.jpg",
        address = "789 Pine Blvd, Anytown",
        distance = 1.5f,
        country = "Mexico"
    )
    // Remove or comment out the restaurant4 entry if it does not exist
    // Restaurant(
    //     id = "4",
    //     name = "Taco Palace",
    //     cuisine = "Mexican",
    //     rating = 4.2f,
    //     priceLevel = 2,
    //     imageUrl = "restaurant4.jpg",
    //     address = "321 Pine St, Anytown",
    //     distance = 0.5f,
    //     country = "Mexico"
    // )
) 