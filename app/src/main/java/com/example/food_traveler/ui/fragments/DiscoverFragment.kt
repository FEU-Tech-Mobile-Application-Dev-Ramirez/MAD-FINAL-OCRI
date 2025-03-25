package com.example.food_traveler.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.compose.AsyncImage
import com.example.food_traveler.R
import com.example.food_traveler.data.PostRepository
import com.example.food_traveler.data.UserRepository
import com.example.food_traveler.model.Post
import com.example.food_traveler.model.Comment
import com.example.food_traveler.ui.theme.FoodTravelerTheme
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.Image
import androidx.compose.material3.Surface
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.animation.core.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.animation.*
import androidx.compose.animation.AnimatedVisibility
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.content.Context
import androidx.compose.runtime.DisposableEffect
import androidx.compose.foundation.background
import android.os.Build
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.ui.geometry.Offset

class DiscoverFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                FoodTravelerTheme {
                    CommunityFeedScreen(
                        onPostLike = { postId ->
                            PostRepository.likePost(postId)
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun CommunityFeedScreen(onPostLike: (String) -> Unit) {
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
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Food Traveler Community") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .pullRefresh(pullRefreshState)
        ) {
            Column {
                if (isOffline) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.errorContainer)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudOff,
                            contentDescription = "Offline",
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "You're offline. Some features may be limited.",
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
                
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    state = listState
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
                                onLikeClick = { 
                                    if (!isOffline) {
                                        onPostLike(post.id)
                                    }
                                }
                            )
                        }
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityPostItem(
    post: Post,
    userName: String,
    userImage: Int,
    onLikeClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    val formattedDate = remember(post.timestamp) { dateFormat.format(post.timestamp) }

    var showLikeAnimation by remember { mutableStateOf(false) }
    var isLiked by remember { mutableStateOf(false) }
    var showCommentsDialog by remember { mutableStateOf(false) }
    
    val imageRes = remember(post.imageUrl) {
        when (post.imageUrl) {
            "italian.jpg" -> R.drawable.italian
            "sushi.jpg" -> R.drawable.sushi
            "mexican.jpg" -> R.drawable.mexican
            "farmtotable.jpg" -> R.drawable.farmtotable
            "breakfast.jpg" -> R.drawable.restaurant1
            else -> R.drawable.ic_placeholder
        }
    }

    if (showCommentsDialog) {
        CommentsDialog(
            postId = post.id,
            onDismiss = { showCommentsDialog = false }
        )
    }

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
                Image(
                    painter = painterResource(id = userImage),
                    contentDescription = "User Avatar",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
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
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = post.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (post.imageUrl != null) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onDoubleTap = {
                                    if (!isLiked) {
                                        onLikeClick()
                                        showLikeAnimation = true
                                        isLiked = true
                                    }
                                }
                            )
                        },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (imageRes == R.drawable.ic_placeholder) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.BrokenImage,
                                        contentDescription = "Image not available",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Image not available",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            Image(
                                painter = painterResource(id = imageRes),
                                contentDescription = "Post Image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        
                        if (showLikeAnimation) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Like",
                                    tint = Color.White,
                                    modifier = Modifier.size(80.dp)
                                )
                            }
                        }
                        
                        LaunchedEffect(showLikeAnimation) {
                            if (showLikeAnimation) {
                                delay(800)
                                showLikeAnimation = false
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyLarge
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            if (!isLiked) {
                                onLikeClick()
                                isLiked = true
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = "Like",
                            tint = if (isLiked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "${post.likes + if (isLiked) 1 else 0} likes",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                Row {
                    IconButton(onClick = { showCommentsDialog = true }) {
                        Icon(
                            Icons.AutoMirrored.Filled.Comment,
                            contentDescription = "Comment"
                        )
                    }
                    
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Share"
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsDialog(
    postId: String,
    onDismiss: () -> Unit
) {
    var commentText by remember { mutableStateOf("") }
    val comments = remember { mutableStateOf(PostRepository.getCommentsForPost(postId)) }
    val currentUser = remember { UserRepository.getCurrentUser() }
    val allUsers = remember { UserRepository.getAllUsers() }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Comments") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items(comments.value) { comment ->
                        val commentUser = allUsers.find { it.id == comment.userId }
                        CommentItem(
                            comment = comment,
                            userName = commentUser?.displayName ?: "Unknown User",
                            userImage = R.drawable.ic_person
                        )
                    }
                }
                
                if (currentUser != null) {
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
                            maxLines = 3
                        )
                        
                        IconButton(
                            onClick = {
                                if (commentText.isNotBlank()) {
                                    PostRepository.addComment(postId, currentUser.id, commentText)
                                    comments.value = PostRepository.getCommentsForPost(postId)
                                    commentText = ""
                                }
                            },
                            enabled = commentText.isNotBlank()
                        ) {
                            Icon(
                                Icons.Default.Send,
                                contentDescription = "Send",
                                tint = if (commentText.isNotBlank()) 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun CommentItem(
    comment: Comment,
    userName: String,
    userImage: Int
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()) }
    val formattedDate = remember(comment.timestamp) { dateFormat.format(comment.timestamp) }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Image(
            painter = painterResource(id = userImage),
            contentDescription = "User Avatar",
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = userName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium
            )
        }
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