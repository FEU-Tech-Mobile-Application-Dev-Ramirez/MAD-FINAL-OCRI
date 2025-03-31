package com.example.food_traveler.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.food_traveler.data.UserRepository
import com.example.food_traveler.data.PostRepository
import com.example.food_traveler.model.PostStatus
import com.example.food_traveler.ui.components.CommunityPost

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onLogout: () -> Unit = {}) {
    val currentUser = remember { UserRepository.getCurrentUser() }
    
    // State to trigger UI refresh when a post is deleted or updated
    var refreshTrigger by remember { mutableStateOf(0) }
    
    // Force recomposition when posts change
    val userPosts by remember(refreshTrigger) { 
        mutableStateOf(PostRepository.getPostsByUserId(currentUser?.id ?: ""))
    }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Profile Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = currentUser?.displayName ?: "Guest User",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = currentUser?.email ?: "Not signed in",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // User Stats
        item {
            val approvedPostsCount = userPosts.count { it.status == PostStatus.APPROVED }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = userPosts.size.toString(),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "Total Posts",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = approvedPostsCount.toString(),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "Approved",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = (userPosts.size - approvedPostsCount).toString(),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "Pending",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        
        // Admin indicator (only shown if user is admin)
        if (currentUser?.isAdmin == true) {
            item {
                Text(
                    text = "Account Settings",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                ListItem(
                    headlineContent = { Text("Admin Access") },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingContent = {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Admin Access Enabled",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                )
            }
        }
        
        // User Posts Section
        item {
            Text(
                text = "My Posts",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            
            if (userPosts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("You haven't posted anything yet.")
                }
            }
        }
        
        // Display posts with status indicator
        items(userPosts) { post ->
            Column(modifier = Modifier.fillMaxWidth()) {
                // Status indicator
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val statusColor = when(post.status) {
                        PostStatus.APPROVED -> MaterialTheme.colorScheme.primary
                        PostStatus.REJECTED -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.tertiary
                    }
                    val statusText = when(post.status) {
                        PostStatus.APPROVED -> "Approved"
                        PostStatus.REJECTED -> "Rejected"
                        else -> "Pending Approval"
                    }
                    
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelMedium,
                        color = statusColor
                    )
                    
                    // Delete button
                    IconButton(
                        onClick = {
                            // Update immediately after deleting the post
                            if (PostRepository.deletePost(post.id)) {
                                refreshTrigger++
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete post",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
                
                CommunityPost(
                    post = post,
                    onLikeClick = { 
                        if (post.status == PostStatus.APPROVED && currentUser != null) {
                            PostRepository.likePost(post.id, currentUser.id)
                            refreshTrigger++ // Trigger refresh
                        }
                    },
                    onCommentClick = { /* Handle comment click */ },
                    onRateClick = { rating ->
                        if (currentUser != null && post.status == PostStatus.APPROVED) {
                            PostRepository.addRating(post.id, currentUser.id, rating)
                            // Immediately update UI
                            refreshTrigger++
                        }
                    },
                    onAddComment = { commentContent ->
                        if (currentUser != null && post.status == PostStatus.APPROVED) {
                            PostRepository.addComment(post.id, currentUser.id, commentContent)
                            // Immediately update UI
                            refreshTrigger++
                        }
                    },
                    currentUserRating = if (currentUser != null) PostRepository.getRating(post.id, currentUser.id) else 0f,
                    comments = PostRepository.getComments(post.id),
                    isInteractive = post.status == PostStatus.APPROVED
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Logout Button
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    Icons.Default.Logout,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout")
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
} 