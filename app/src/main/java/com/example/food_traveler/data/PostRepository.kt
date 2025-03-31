package com.example.food_traveler.data

import android.net.Uri
import android.util.Log
import com.example.food_traveler.model.Post
import com.example.food_traveler.model.PostStatus
import com.example.food_traveler.model.Comment
import java.util.UUID

object PostRepository {
    private val posts = mutableListOf<Post>()
    private val ratings = mutableMapOf<String, MutableMap<String, Float>>() // postId -> (userId -> rating)
    private val comments = mutableMapOf<String, MutableList<Comment>>() // postId -> comments
    private val likes = mutableMapOf<String, MutableSet<String>>() // postId -> set of userIds who liked
    private var currentImageUri: Uri? = null
    private var currentLocation: String? = null

    private const val TAG = "PostRepository"

    // Initialization can include sample data
    init {
        // Initialize likes map for each post
        posts.forEach { post ->
            likes[post.id] = mutableSetOf()
        }
    }

    fun setCurrentImageUri(uri: Uri?) {
        currentImageUri = uri
    }

    fun getAndClearCurrentImageUri(): Uri? {
        val uri = currentImageUri
        currentImageUri = null
        return uri
    }

    fun setCurrentLocation(location: String?) {
        currentLocation = location
    }

    fun getAndClearCurrentLocation(): String? {
        val location = currentLocation
        currentLocation = null
        return location
    }

    fun uriToString(uri: Uri?): String? {
        return uri?.toString()
    }

    fun stringToUri(uriString: String?): Uri? {
        return uriString?.let { Uri.parse(it) }
    }

    fun addPost(post: Post) {
        val newPost = if (post.id.isEmpty()) {
            val imageUrl = currentImageUri?.toString() ?: post.imageUrl
            val location = currentLocation ?: post.location
            post.copy(
                id = UUID.randomUUID().toString(),
                imageUrl = imageUrl,
                location = location,
                likes = 0  // Ensure new posts start with 0 likes
            )
        } else {
            post
        }
        Log.d(TAG, "Adding post: $newPost")
        posts.add(newPost)
        ratings[newPost.id] = mutableMapOf()
        comments[newPost.id] = mutableListOf()
        likes[newPost.id] = mutableSetOf()
        
        currentImageUri = null
        currentLocation = null
    }

    fun getPost(postId: String): Post? {
        return posts.find { it.id == postId }
    }

    fun getAllPosts(): List<Post> {
        return posts.toList() // Return a copy to prevent external modifications
    }

    fun getPostsByUserId(userId: String): List<Post> {
        return posts.filter { it.userId == userId }
    }

    fun getPostsByUser(userId: String): List<Post> = posts.filter { it.userId == userId }

    fun getPostById(postId: String): Post? = posts.find { it.id == postId }

    fun addRating(postId: String, userId: String, rating: Float) {
        Log.d(TAG, "Adding rating: postId=$postId, userId=$userId, rating=$rating")
        ratings.getOrPut(postId) { mutableMapOf() }[userId] = rating
        val post = posts.find { it.id == postId }
        post?.let {
            val index = posts.indexOf(it)
            val ratingsValues = ratings[postId]?.values?.toList() ?: emptyList()
            val avgRating = calculateAverageRating(postId)
            Log.d(TAG, "Updating post with new ratings: count=${ratingsValues.size}, avg=$avgRating")
            
            posts[index] = it.copy(
                ratings = ratingsValues,
                ratingCount = ratingsValues.size,
                averageRating = avgRating
            )
        }
        updatePostAverageRating(postId)
    }

    fun getRating(postId: String, userId: String): Float {
        val rating = ratings[postId]?.get(userId) ?: 0f
        Log.d(TAG, "Getting rating for postId=$postId, userId=$userId: $rating")
        return rating
    }

    // Get all users who rated a post and their ratings
    fun getUserRatings(postId: String): Map<String, Float> {
        return ratings[postId]?.toMap() ?: emptyMap()
    }

    private fun calculateAverageRating(postId: String): Float {
        val postRatings = ratings[postId]?.values?.toList() ?: emptyList()
        return if (postRatings.isNotEmpty()) {
            postRatings.average().toFloat()
        } else {
            0f
        }
    }

    fun addComment(postId: String, userId: String, content: String) {
        val comment = Comment(
            id = UUID.randomUUID().toString(),
            postId = postId,
            userId = userId,
            content = content,
            timestamp = System.currentTimeMillis()
        )
        comments.getOrPut(postId) { mutableListOf() }.add(comment)
    }

    fun getComments(postId: String): List<Comment> {
        return comments[postId]?.toList() ?: emptyList()
    }

    fun getCommentsForPost(postId: String): List<Comment> = comments[postId] ?: emptyList()

    fun getCommentCount(postId: String): Int = comments[postId]?.size ?: 0

    fun deletePost(postId: String): Boolean {
        val postIndex = posts.indexOfFirst { it.id == postId }
        if (postIndex >= 0) {
            // Remove from posts list immediately
            posts.removeAt(postIndex)
            // Remove associated data
            ratings.remove(postId)
            comments.remove(postId)
            likes.remove(postId)
            Log.d(TAG, "Post deleted: $postId")
            return true
        } else {
            Log.d(TAG, "Post not found for deletion: $postId")
            return false
        }
    }

    fun updatePostStatus(postId: String, status: PostStatus) {
        val post = posts.find { it.id == postId }
        post?.let {
            val index = posts.indexOf(it)
            posts[index] = it.copy(status = status)
        }
    }

    fun likePost(postId: String, userId: String? = null) {
        val post = posts.find { it.id == postId }
        post?.let {
            val index = posts.indexOf(it)
            val newLikes = it.likes + 1
            posts[index] = it.copy(likes = newLikes)
            
            // Track who liked the post if userId is provided
            if (userId != null) {
                likes.getOrPut(postId) { mutableSetOf() }.add(userId)
            }
        }
    }

    fun unlikePost(postId: String, userId: String? = null) {
        val post = posts.find { it.id == postId }
        post?.let {
            val index = posts.indexOf(it)
            posts[index] = it.copy(likes = maxOf(0, it.likes - 1))
            
            // Remove user from likes if userId provided
            if (userId != null) {
                likes[postId]?.remove(userId)
            }
        }
    }

    // Get all users who liked a specific post
    fun getUsersWhoLiked(postId: String): Set<String> {
        return likes[postId]?.toSet() ?: emptySet()
    }

    // Check if a user has liked a post
    fun hasUserLiked(postId: String, userId: String): Boolean {
        return likes[postId]?.contains(userId) ?: false
    }

    fun ratePost(postId: String, userId: String, rating: Float): Post? {
        addRating(postId, userId, rating)
        // Return the updated post immediately for UI updates
        return getPostById(postId)
    }

    fun getPostRatings(postId: String): List<Float> {
        return ratings[postId]?.values?.toList() ?: emptyList()
    }

    // Get rating distribution for a post (how many 5-stars, 4-stars, etc.)
    fun getRatingDistribution(postId: String): Map<Int, Int> {
        val postRatings = ratings[postId]?.values ?: emptyList()
        return postRatings.groupingBy { it.toInt() }.eachCount()
    }

    private fun updatePostAverageRating(postId: String) {
        posts.find { it.id == postId }?.let { post ->
            val postRatings = ratings[postId]?.values ?: emptyList()
            val averageRating = if (postRatings.isNotEmpty()) {
                postRatings.average().toFloat()
            } else {
                0f
            }
            val updatedPost = post.copy(
                averageRating = averageRating,
                ratingCount = postRatings.size
            )
            posts[posts.indexOf(post)] = updatedPost
        }
    }
}

