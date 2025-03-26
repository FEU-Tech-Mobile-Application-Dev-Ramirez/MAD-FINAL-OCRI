package com.example.food_traveler.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.compose.AsyncImage
import com.example.food_traveler.R
import com.example.food_traveler.data.PostRepository
import com.example.food_traveler.data.UserRepository
import com.example.food_traveler.model.Post
import com.example.food_traveler.model.PostStatus
import com.example.food_traveler.model.Comment
import com.example.food_traveler.ui.theme.FoodTravelerTheme
import com.example.food_traveler.ui.components.CommentItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.clickable
import com.example.food_traveler.ui.components.RatingBar

class ProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                FoodTravelerTheme {
                    ProfileScreen(
                        onLogout = {
                            UserRepository.logout()
                            findNavController().navigate(R.id.welcomeFragment)
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onLogout: () -> Unit) {
    val currentUser = UserRepository.getCurrentUser()
    var showAddPostDialog by remember { mutableStateOf(false) }
    var showRatingsDialog by remember { mutableStateOf<String?>(null) }
    var postTitle by remember { mutableStateOf("") }
    var postContent by remember { mutableStateOf("") }
    var postLocation by remember { mutableStateOf("") }
    
    if (currentUser == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Please log in to view your profile")
        }
        return
    }
    
    val userPosts = PostRepository.getPostsByUser(currentUser.id)
    
    if (showAddPostDialog) {
        AddPostDialog(
            title = postTitle,
            content = postContent,
            location = postLocation,
            onTitleChange = { postTitle = it },
            onContentChange = { postContent = it },
            onLocationChange = { postLocation = it },
            onDismiss = { 
                showAddPostDialog = false
                postTitle = ""
                postContent = ""
                postLocation = ""
            },
            onConfirm = {
                if (postTitle.isNotBlank() && postContent.isNotBlank()) {
                    PostRepository.addPost(
                        Post(
                            id = "",
                            userId = currentUser.id,
                            userName = currentUser.displayName,
                            title = postTitle,
                            content = postContent,
                            imageUrl = null,
                            timestamp = System.currentTimeMillis(),
                            likes = 0,
                            location = postLocation,
                            status = if (currentUser.isAdmin) PostStatus.APPROVED else PostStatus.PENDING
                        )
                    )
                    
                    showAddPostDialog = false
                    postTitle = ""
                    postContent = ""
                    postLocation = ""
                }
            }
        )
    }

    showRatingsDialog?.let { postId ->
        RatingsDialog(
            postId = postId,
            onDismiss = { showRatingsDialog = null }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile") },
                actions = {
                    if (currentUser.isAdmin) {
                        IconButton(onClick = { /* Navigate to admin panel */ }) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin Panel")
                        }
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                
                AsyncImage(
                    model = R.drawable.ic_person,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = currentUser.displayName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "@${currentUser.email}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (currentUser.isAdmin) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Admin",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem("Posts", userPosts.size.toString())
                    StatItem("Comments", userPosts.sumOf { PostRepository.getCommentCount(it.id) }.toString())
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = { showAddPostDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Create New Post")
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "My Posts",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    textAlign = TextAlign.Start
                )
            }
            
            if (userPosts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No posts yet. Create your first post!",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(userPosts) { post ->
                    UserPostItem(
                        post = post,
                        onDelete = {
                            PostRepository.deletePost(post.id)
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun UserPostItem(
    post: Post,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var commentText by remember { mutableStateOf("") }
    val currentUser = remember { UserRepository.getCurrentUser() }
    val comments = remember(post.id) { PostRepository.getCommentsForPost(post.id) }
    val commentCount = remember(post.id) { PostRepository.getCommentCount(post.id) }
    val postRatings = remember(post.id) { PostRepository.getPostRatings(post.id) }
    val userRating = remember(post.id, currentUser?.id) { 
        if (currentUser != null) {
            PostRepository.getRating(post.id, currentUser.id)
        } else {
            0f
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Post content
            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyLarge
            )

            // Rating section
            if (currentUser != null) {
                RatingBar(
                    rating = userRating,
                    onRatingChanged = { rating ->
                        PostRepository.ratePost(post.id, currentUser.id, rating)
                    },
                    isInteractive = true
                )
            }

            // Comments section
            Text(
                text = "$commentCount Comments",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(top = 8.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
            ) {
                items(comments) { comment ->
                    CommentItem(comment = comment)
                }
            }

            // Add comment section
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
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send comment"
                    )
                }
            }

            // Delete button
            if (currentUser?.id == post.userId) {
                Button(
                    onClick = onDelete,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Delete Post")
                }
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingsDialog(
    postId: String,
    onDismiss: () -> Unit
) {
    val ratings = remember { PostRepository.getPostRatings(postId) }
    val users = remember { UserRepository.getAllUsers() }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Post Ratings") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                if (ratings.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No ratings yet")
                    }
                } else {
                    LazyColumn {
                        items(ratings.size) { index ->
                            val rating = ratings[index]
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "User Rating ${index + 1}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    repeat(5) { starIndex ->
                                        Icon(
                                            Icons.Default.Star,
                                            contentDescription = null,
                                            tint = if (starIndex < rating) 
                                                MaterialTheme.colorScheme.primary 
                                            else 
                                                MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPostDialog(
    title: String,
    content: String,
    location: String,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Post") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = onTitleChange,
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = location,
                    onValueChange = onLocationChange,
                    label = { Text("Location") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = content,
                    onValueChange = onContentChange,
                    label = { Text("Content") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    maxLines = 5
                )
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
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