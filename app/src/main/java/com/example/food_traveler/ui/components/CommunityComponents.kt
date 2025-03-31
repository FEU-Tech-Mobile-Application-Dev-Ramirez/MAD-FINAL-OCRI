package com.example.food_traveler.ui.components

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.food_traveler.R
import com.example.food_traveler.data.PostRepository
import com.example.food_traveler.data.UserRepository
import com.example.food_traveler.model.Post
import com.example.food_traveler.model.Comment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun CommunityFeedScreen(onPostRate: (String, Float) -> Unit) {
    var refreshing by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    val allPosts = remember { mutableStateOf(PostRepository.getAllPosts()) }
    val allUsers = remember { UserRepository.getAllUsers() }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    
    var isOffline by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    DisposableEffect(Unit) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        
        isOffline = !isNetworkAvailable(connectivityManager)
        
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                isOffline = false
            }
            
            override fun onLost(network: Network) {
                isOffline = true
            }
        }
        
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        
        try {
            connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
            
            onDispose {
                try {
                    connectivityManager.unregisterNetworkCallback(networkCallback)
                } catch (e: Exception) {
                }
            }
        } catch (e: Exception) {
            isOffline = false
        }
        
        onDispose {}
    }
    
    LaunchedEffect(key1 = Unit) {
        delay(1000)
        isLoading = false
    }
    
    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshing,
        onRefresh = {
            if (!isOffline) {
                coroutineScope.launch {
                    refreshing = true
                    isLoading = true
                    delay(1000)
                    allPosts.value = PostRepository.getAllPosts()
                    isLoading = false
                    refreshing = false
                }
            }
        }
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        if (isOffline) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                color = MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudOff,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Text(
                        text = "You're offline",
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
        
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            if (isLoading) {
                items(3) {
                    ShimmerPostItem()
                    Spacer(modifier = Modifier.height(16.dp))
                }
            } else {
                items(
                    items = allPosts.value,
                    key = { it.id }
                ) { post ->
                    val postUser = allUsers.find { it.id == post.userId }
                    CommunityPostItem(
                        post = post,
                        userName = postUser?.displayName ?: "Unknown User",
                        userImage = R.drawable.ic_person,
                        onRateClick = { rating ->
                            onPostRate(post.id, rating)
                            allPosts.value = PostRepository.getAllPosts()
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
        
        PullRefreshIndicator(
            refreshing = refreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            backgroundColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun ShimmerPostItem() {
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f)
    )
    
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ), label = "shimmer"
    )
    
    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(10f, 10f),
        end = Offset(translateAnim.value, translateAnim.value)
    )
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(brush)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Spacer(
                        modifier = Modifier
                            .height(15.dp)
                            .width(120.dp)
                            .background(brush)
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Spacer(
                        modifier = Modifier
                            .height(10.dp)
                            .width(80.dp)
                            .background(brush)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .background(brush)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(brush)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            repeat(3) {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .background(brush)
                )
                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityPostItem(
    post: Post,
    userName: String,
    userImage: Int,
    onRateClick: (Float) -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    val formattedDate = remember(post.timestamp) { dateFormat.format(Date(post.timestamp)) }
    var showRatingDialog by remember { mutableStateOf(false) }
    var showCommentsDialog by remember { mutableStateOf(false) }
    var commentText by remember { mutableStateOf("") }
    val currentUser = remember { UserRepository.getCurrentUser() }
    
    // Use mutableStateOf for post to immediately reflect changes
    var currentPost by remember { mutableStateOf(post) }
    
    // Update currentPost when post changes
    LaunchedEffect(post) {
        currentPost = post
    }

    val imageRes = remember(post.imageUrl) {
        when (post.imageUrl) {
            "italian.jpg" -> R.drawable.italian
            "sushi.jpg" -> R.drawable.sushi
            "mexican.jpg" -> R.drawable.mexican
            "farmtotable.jpg" -> R.drawable.farmtotable
            else -> R.drawable.ic_placeholder
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Post Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = userImage),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Post Image
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = post.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )

            // Post Content
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = post.content,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (post.location != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = post.location,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Post Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Rating Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { showRatingDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rate",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${post.ratingCount} ratings",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Comments Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { showCommentsDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Comment,
                        contentDescription = "Comments",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${PostRepository.getCommentCount(post.id)} comments",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }

    // Rating Dialog
    if (showRatingDialog) {
        AlertDialog(
            onDismissRequest = { showRatingDialog = false },
            title = { Text("Rate this post") },
            text = {
                Column {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(5) { index ->
                            val isFilled = index < currentPost.averageRating.toInt()
                            
                            Icon(
                                imageVector = if (isFilled) Icons.Default.Star else Icons.Default.StarOutline,
                                contentDescription = "Star ${index + 1}",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clickable {
                                        onRateClick(index + 1.0f)
                                        // Immediately update the local post state with the expected rating
                                        // This makes the UI update feel instantaneous
                                        val updatedPost = PostRepository.getPostById(post.id)
                                        if (updatedPost != null) {
                                            currentPost = updatedPost
                                        }
                                        showRatingDialog = false
                                    }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showRatingDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Comments Dialog
    if (showCommentsDialog) {
        AlertDialog(
            onDismissRequest = { showCommentsDialog = false },
            title = { Text("Comments") },
            text = {
                Column {
                    // Comments List
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                    ) {
                        val comments = PostRepository.getCommentsForPost(post.id)
                        if (comments.isEmpty()) {
                            item {
                                Text(
                                    text = "No comments yet. Be the first to comment!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        } else {
                            itemsIndexed(comments) { _, comment ->
                                CommentItem(comment)
                                Divider(modifier = Modifier.padding(vertical = 4.dp))
                            }
                        }
                    }

                    // Add Comment
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = commentText,
                            onValueChange = { commentText = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Add a comment...") },
                            singleLine = true
                        )
                        IconButton(
                            onClick = {
                                if (commentText.isNotBlank() && currentUser != null) {
                                    PostRepository.addComment(post.id, currentUser.id, commentText)
                                    commentText = ""
                                }
                            },
                            enabled = commentText.isNotBlank()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send comment"
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCommentsDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun CommunityPost(
    post: Post,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onRateClick: (Float) -> Unit = {},
    onAddComment: (String) -> Unit = {},
    currentUserRating: Float = 0f,
    comments: List<Comment> = emptyList(),
    isInteractive: Boolean = true
) {
    var showComments by remember { mutableStateOf(false) }
    var commentText by remember { mutableStateOf("") }
    var showLikesDialog by remember { mutableStateOf(false) }
    var showRatingsDialog by remember { mutableStateOf(false) }
    val users = remember { UserRepository.getAllUsers() }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Post Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = post.userName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(post.timestamp)),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            
            // Post Content
            if (post.title.isNotBlank()) {
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyLarge
            )

            // Post Image if available
            post.imageUrl?.let { imageUrl ->
                Spacer(modifier = Modifier.height(8.dp))
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Post image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Rating Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RatingBar(
                    rating = currentUserRating,
                    onRatingChanged = onRateClick,
                    isInteractive = isInteractive
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable(enabled = post.ratingCount > 0) { showRatingsDialog = true }
                ) {
                    Text(
                        text = "${post.ratingCount} ratings",
                        style = MaterialTheme.typography.bodySmall
                    )
                    if (post.ratingCount > 0) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "View ratings",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable(enabled = isInteractive) { 
                        if (isInteractive) onLikeClick() 
                    }
                ) {
                    IconButton(
                        onClick = { if (isInteractive) onLikeClick() },
                        enabled = isInteractive
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Like",
                            tint = if (isInteractive) 
                                MaterialTheme.colorScheme.primary 
                                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable(enabled = post.likes > 0) { showLikesDialog = true }
                    ) {
                        Text(
                            text = "${post.likes}",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                        if (post.likes > 0) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "View likes",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { showComments = !showComments },
                        enabled = isInteractive
                    ) {
                        Icon(
                            imageVector = Icons.Default.Comment,
                            contentDescription = "Comment",
                            tint = if (isInteractive) 
                                MaterialTheme.colorScheme.primary 
                                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                    Text(
                        text = "${comments.size}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }

            // Pending approval message if not approved
            if (!isInteractive) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "This post is waiting for approval. Interactions are disabled until approved.",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(8.dp),
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }

            // Comments Section
            if (showComments) {
                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(16.dp))

                // Add Comment
                if (isInteractive) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = commentText,
                            onValueChange = { commentText = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Add a comment...") },
                            singleLine = true,
                            enabled = isInteractive
                        )
                        IconButton(
                            onClick = {
                                if (commentText.isNotBlank() && isInteractive) {
                                    onAddComment(commentText)
                                    commentText = ""
                                }
                            },
                            enabled = commentText.isNotBlank() && isInteractive
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send comment"
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Comments List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                ) {
                    if (comments.isEmpty()) {
                        item {
                            Text(
                                text = "No comments yet. Be the first to comment!",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    } else {
                        itemsIndexed(comments) { _, comment ->
                            CommentItem(comment = comment)
                            if (comment != comments.last()) {
                                Divider(modifier = Modifier.padding(vertical = 4.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog to show who liked the post
    if (showLikesDialog) {
        AlertDialog(
            onDismissRequest = { showLikesDialog = false },
            title = { Text("Liked by") },
            text = {
                val userIds = PostRepository.getUsersWhoLiked(post.id)
                if (userIds.isEmpty()) {
                    Text("No likes yet.")
                } else {
                    Column {
                        userIds.forEach { userId ->
                            val user = UserRepository.getUserById(userId)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = user?.displayName ?: "Unknown User",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLikesDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Dialog to show who rated the post
    if (showRatingsDialog) {
        AlertDialog(
            onDismissRequest = { showRatingsDialog = false },
            title = { Text("Ratings") },
            text = {
                Column {
                    Text(
                        text = "Average Rating: ${String.format("%.1f", post.averageRating)} (${post.ratingCount} ratings)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    // Show ratings distribution
                    Text(
                        text = "Rating breakdown:",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                    
                    // For each star rating (5 to 1), show the count
                    val ratingsMap = post.ratings.groupingBy { it.toInt() }.eachCount()
                    for (i in 5 downTo 1) {
                        val count = ratingsMap[i] ?: 0
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$i",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.width(16.dp)
                            )
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            LinearProgressIndicator(
                                progress = if (post.ratingCount > 0) count.toFloat() / post.ratingCount else 0f,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "$count",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showRatingsDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun RatingBar(
    rating: Float,
    onRatingChanged: (Float) -> Unit,
    isInteractive: Boolean = false
) {
    var selectedRating by remember { mutableStateOf(rating) }
    
    // Update local rating when the prop changes
    LaunchedEffect(rating) {
        selectedRating = rating
    }
    
    Row(verticalAlignment = Alignment.CenterVertically) {
        repeat(5) { index ->
            val starFilled = index < selectedRating
            val starIcon = if (starFilled) {
                Icons.Default.Star
            } else {
                Icons.Default.StarOutline
            }
            
            Icon(
                imageVector = starIcon,
                contentDescription = "Star ${index + 1}",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(24.dp)
                    .clickable(enabled = isInteractive) {
                        if (isInteractive) {
                            val newRating = index + 1f
                            // Update the local state immediately for responsive UI
                            selectedRating = newRating
                            // Notify parent
                            onRatingChanged(newRating)
                        }
                    }
            )
        }
    }
}

private fun isNetworkAvailable(connectivityManager: ConnectivityManager): Boolean {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
    } catch (e: Exception) {
        return true
    }
} 