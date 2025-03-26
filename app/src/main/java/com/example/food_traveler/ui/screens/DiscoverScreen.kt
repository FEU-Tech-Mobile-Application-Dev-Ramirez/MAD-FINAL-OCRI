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
import androidx.compose.ui.unit.Dp
import coil.compose.AsyncImage
import com.example.food_traveler.data.Restaurant
import com.example.food_traveler.R
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Check
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.AnimatedVisibility
import com.example.food_traveler.data.AdminRepository
import com.example.food_traveler.data.PostRepository
import com.example.food_traveler.data.UserRepository
import com.example.food_traveler.model.Post
import com.example.food_traveler.model.PostStatus
import com.example.food_traveler.ui.components.CommunityPost
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    var showFilterOptions by remember { mutableStateOf(false) }
    val filterOptions = listOf("Vegetarian", "Vegan", "Gluten-Free", "Halal", "Spicy")
    var selectedFilters by remember { mutableStateOf(setOf<String>()) }
    
    Column(modifier = modifier) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search restaurants...") },
            leadingIcon = { 
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            trailingIcon = {
                Row {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search"
                            )
                        }
                    }
                    IconButton(onClick = { showFilterOptions = !showFilterOptions }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter options"
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(24.dp)
        )
        
        AnimatedVisibility(
            visible = showFilterOptions,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(filterOptions) { option ->
                    val isSelected = selectedFilters.contains(option)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedFilters = if (isSelected) {
                                selectedFilters - option
                            } else {
                                selectedFilters + option
                            }
                        },
                        label = { Text(option) },
                        leadingIcon = if (isSelected) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else null
                    )
                }
            }
        }
    }
}

@Composable
fun CuisineFilters(
    selectedCuisine: String?,
    onCuisineSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val cuisines = listOf("Italian", "Japanese", "Mexican", "Street Food", "Farm to Table")
    
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
        
        cuisines.forEachIndexed { index, cuisine ->
            val cuisineImage = when (cuisine) {
                "Italian" -> R.drawable.italian
                "Japanese" -> R.drawable.sushi
                "Mexican" -> R.drawable.mexican
                "Street Food" -> R.drawable.restaurant1 // Using restaurant1 as fallback for streetfood
                "Farm to Table" -> R.drawable.farmtotable
                else -> null
            }
            
            Tab(
                selected = selectedCuisine == cuisine,
                onClick = { onCuisineSelected(cuisine) },
                modifier = Modifier.height(80.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(4.dp)
                ) {
                    if (cuisineImage != null) {
                        Image(
                            painter = painterResource(id = cuisineImage),
                            contentDescription = cuisine,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Text(
                        text = cuisine,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun RatingBar(
    rating: Float,
    maxRating: Int = 5,
    onRatingChanged: (Float) -> Unit = {},
    isInteractive: Boolean = false,
    starSize: Dp = 16.dp,
    starColor: Color = MaterialTheme.colorScheme.secondary,
    unfilledStarColor: Color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
) {
    Row {
        for (i in 1..maxRating) {
            val isFilled = i <= rating
            val isHalfFilled = i > rating && i - 0.5f <= rating
            
            Box(
                modifier = Modifier
                    .size(starSize)
                    .then(
                        if (isInteractive) {
                            Modifier.clickable { onRatingChanged(i.toFloat()) }
                        } else {
                            Modifier
                        }
                    )
            ) {
                Icon(
                    imageVector = if (isHalfFilled) Icons.Default.StarHalf else if (isFilled) Icons.Default.Star else Icons.Default.StarOutline,
                    contentDescription = "Rating $i of $maxRating",
                    tint = if (isFilled || isHalfFilled) starColor else unfilledStarColor,
                    modifier = Modifier.size(starSize)
                )
            }
            
            if (i < maxRating) {
                Spacer(modifier = Modifier.width(2.dp))
            }
        }
    }
}

@Composable
fun RestaurantCard(
    restaurant: Restaurant,
    modifier: Modifier = Modifier,
    onRestaurantClicked: (Restaurant) -> Unit
) {
    // Create mutable state for rating to allow user interaction
    var currentRating by remember { mutableStateOf(restaurant.rating) }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        onClick = { onRestaurantClicked(restaurant) }
    ) {
        Column {
            // Choose image based on cuisine or ID
            val restaurantImage = when {
                restaurant.cuisine == "Italian" -> R.drawable.italian
                restaurant.cuisine == "Japanese" -> R.drawable.sushi
                restaurant.cuisine == "Mexican" -> R.drawable.mexican
                restaurant.cuisine == "Street Food" -> R.drawable.restaurant1 // Using restaurant1 instead of streetfood
                restaurant.cuisine == "Farm to Table" -> R.drawable.farmtotable
                restaurant.id == "1" -> R.drawable.restaurant1
                restaurant.id == "2" -> R.drawable.restaurant2
                restaurant.id == "3" -> R.drawable.restaurant3
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
                
                // Rating with stars
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RatingBar(
                        rating = currentRating,
                        isInteractive = true,
                        onRatingChanged = { newRating ->
                            currentRating = newRating
                            // Here you would typically update a database or repository
                            // For now we just update the local state
                        }
                    )
                    
                    Text(
                        text = String.format("%.1f", currentRating),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen() {
    val approvedPosts = remember { mutableStateOf(AdminRepository.getApprovedPosts()) }
    var showCreatePost by remember { mutableStateOf(false) }
    var postContent by remember { mutableStateOf("") }
    var postRating by remember { mutableStateOf(0f) }
    val currentUser = remember { UserRepository.getCurrentUser() }
    val snackbarHostState = remember { SnackbarHostState() }
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }
    
    LaunchedEffect(Unit) {
        // Initially load the approved posts
        approvedPosts.value = PostRepository.getAllPosts().filter { it.status == PostStatus.APPROVED }
    }
    
    LaunchedEffect(showSnackbar) {
        if (showSnackbar) {
            snackbarHostState.showSnackbar(snackbarMessage)
            showSnackbar = false
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreatePost = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create post")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Community",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            if (approvedPosts.value.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No posts yet. Be the first to share your experience!")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(approvedPosts.value) { post ->
                        CommunityPost(
                            post = post,
                            onRateClick = { rating ->
                                if (currentUser != null) {
                                    PostRepository.ratePost(post.id, currentUser.id, rating)
                                    // Update the list to reflect changes
                                    approvedPosts.value = PostRepository.getAllPosts()
                                        .filter { it.status == PostStatus.APPROVED }
                                    snackbarMessage = "Rating submitted: $rating stars"
                                    showSnackbar = true
                                } else {
                                    snackbarMessage = "Please log in to rate posts"
                                    showSnackbar = true
                                }
                            },
                            onAddComment = { commentContent ->
                                if (currentUser != null) {
                                    PostRepository.addComment(post.id, currentUser.id, commentContent)
                                    // Update the current post to reflect the new comment
                                    approvedPosts.value = PostRepository.getAllPosts()
                                        .filter { it.status == PostStatus.APPROVED }
                                    snackbarMessage = "Comment added"
                                    showSnackbar = true
                                } else {
                                    snackbarMessage = "Please log in to comment"
                                    showSnackbar = true
                                }
                            },
                            currentUserRating = if (currentUser != null) PostRepository.getRating(post.id, currentUser.id) else 0f,
                            comments = PostRepository.getCommentsForPost(post.id),
                            onLikeClick = {
                                // Handle like functionality here if needed
                                snackbarMessage = "Post liked!"
                                showSnackbar = true
                            },
                            onCommentClick = {
                                // Handle comment click functionality here if needed
                                snackbarMessage = "Comments section expanded"
                                showSnackbar = true
                            }
                        )
                    }
                }
            }
        }
    }
    
    if (showCreatePost) {
        CreatePostDialog(
            content = postContent,
            rating = postRating,
            onContentChange = { postContent = it },
            onRatingChange = { postRating = it },
            onDismiss = { 
                showCreatePost = false
                postContent = ""
                postRating = 0f
            },
            onSubmit = {
                if (postContent.isNotBlank() && currentUser != null) {
                    val newPost = Post(
                        id = "",
                        userId = currentUser.id,
                        userName = currentUser.displayName,
                        title = "Food Experience",
                        content = postContent,
                        status = if (currentUser.isAdmin) PostStatus.APPROVED else PostStatus.PENDING,
                        averageRating = postRating,
                        ratingCount = 1,
                        ratings = listOf(postRating)
                    )
                    
                    PostRepository.addPost(newPost)
                    
                    // Refresh the posts list if the new post is approved
                    if (newPost.status == PostStatus.APPROVED) {
                        approvedPosts.value = PostRepository.getAllPosts()
                            .filter { it.status == PostStatus.APPROVED }
                    }
                    
                    showCreatePost = false
                    postContent = ""
                    postRating = 0f
                    
                    snackbarMessage = if (currentUser.isAdmin) "Post published" else "Post submitted for approval"
                    showSnackbar = true
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostDialog(
    content: String,
    rating: Float,
    onContentChange: (String) -> Unit,
    onRatingChange: (Float) -> Unit,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Post") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = content,
                    onValueChange = onContentChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    label = { Text("Share your food experience...") },
                    placeholder = { Text("What did you eat? How was it?") }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Rate your experience:")
                
                Spacer(modifier = Modifier.height(8.dp))
                
                RatingBar(
                    rating = rating,
                    onRatingChanged = onRatingChange,
                    isInteractive = true,
                    starSize = 32.dp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onSubmit,
                enabled = content.isNotBlank() && rating > 0
            ) {
                Text("Post")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
} 