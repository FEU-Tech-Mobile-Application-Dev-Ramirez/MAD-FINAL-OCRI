package com.example.food_traveler.data

import android.util.Log
import com.example.food_traveler.model.Post
import com.example.food_traveler.model.PostStatus
import com.example.food_traveler.model.User

object AdminRepository {
    private const val TAG = "AdminRepository"

    fun getPendingPosts(): List<Post> = PostRepository.getPendingPosts()

    fun getApprovedPosts(): List<Post> = PostRepository.getApprovedPosts()

    fun getRejectedPosts(): List<Post> = PostRepository.getAllPosts()
        .filter { it.status == PostStatus.REJECTED }
        .sortedByDescending { it.timestamp }

    fun addPost(post: Post) {
        Log.d(TAG, "Adding post through AdminRepository: $post")
        PostRepository.addPost(post)
    }

    fun approvePost(postId: String) {
        Log.d(TAG, "Approving post: $postId")
        PostRepository.approvePost(postId)
    }

    fun rejectPost(postId: String) {
        Log.d(TAG, "Rejecting post: $postId")
        PostRepository.rejectPost(postId)
    }

    fun isUserAdmin(userId: String): Boolean {
        // In a real app, this would check against a database or backend service
        val isAdmin = userId == "admin123" // Example admin ID
        Log.d(TAG, "Checking if user $userId is admin: $isAdmin")
        return isAdmin
    }
} 