package com.example.food_traveler.data

import android.util.Log
import com.example.food_traveler.model.Post
import com.example.food_traveler.model.PostStatus
import com.example.food_traveler.model.Comment
import java.util.UUID

object PostRepository {
    private val posts = mutableListOf<Post>()
    private val ratings = mutableMapOf<String, MutableMap<String, Float>>() // postId -> (userId -> rating)
    private val comments = mutableMapOf<String, MutableList<Comment>>() // postId -> comments

    private const val TAG = "PostRepository"

    init {
        // Add some sample posts
        addPost(
            Post(
                id = "1",
                userId = "user1",
                userName = "John Doe",
                title = "Amazing Italian Restaurant",
                content = "Just tried this amazing Italian restaurant! The pasta was incredible, and the tiramisu for dessert was the best I've ever had. Highly recommend for anyone visiting downtown.",
                imageUrl = "italian.jpg",
                timestamp = System.currentTimeMillis() - 86400000, // 1 day ago
                likes = 25,
                location = "Downtown",
                status = PostStatus.APPROVED,
                ratings = listOf(4.5f, 5.0f, 4.0f),
                ratingCount = 3,
                averageRating = 4.5f
            )
        )
        
        addPost(
            Post(
                id = "2",
                userId = "user2",
                userName = "Jane Smith",
                title = "Best Sushi Place",
                content = "Best sushi in town! Must try their signature rolls. The chef is incredibly talented and the fish is always fresh. Great ambiance too!",
                imageUrl = "sushi.jpg",
                timestamp = System.currentTimeMillis() - 43200000, // 12 hours ago
                likes = 18,
                location = "Eastside",
                status = PostStatus.APPROVED,
                ratings = listOf(5.0f, 4.8f, 4.9f),
                ratingCount = 3,
                averageRating = 4.9f
            )
        )
        
        // Add more sample posts
        addPost(
            Post(
                id = "3",
                userId = "user3",
                userName = "Mike Johnson",
                title = "Hidden Mexican Gem",
                content = "Discovered this little Mexican place tucked away in the corner of Main St. The tacos are authentic and the margaritas are strong! Can't believe I haven't tried it before.",
                imageUrl = "mexican.jpg",
                timestamp = System.currentTimeMillis() - 129600000, // 1.5 days ago
                likes = 32,
                location = "Main Street",
                status = PostStatus.APPROVED,
                ratings = listOf(4.2f, 4.5f, 3.8f, 4.0f),
                ratingCount = 4,
                averageRating = 4.1f
            )
        )
        
        addPost(
            Post(
                id = "4",
                userId = "user4",
                userName = "Emily Wilson",
                title = "Farm-to-Table Experience",
                content = "Had the most amazing farm-to-table dinner last night. Everything was harvested that morning, and you could really taste the freshness. The chef came out and explained each dish too!",
                imageUrl = "farmtotable.jpg",
                timestamp = System.currentTimeMillis() - 172800000, // 2 days ago
                likes = 45,
                location = "Rural Outskirts",
                status = PostStatus.APPROVED,
                ratings = listOf(5.0f, 5.0f, 4.8f, 5.0f, 4.9f),
                ratingCount = 5,
                averageRating = 4.9f
            )
        )
        
        // Add a pending post
        addPost(
            Post(
                id = "5",
                userId = "user5",
                userName = "Alex Brown",
                title = "Disappointing Steakhouse",
                content = "Went to the new steakhouse everyone's talking about and was really disappointed. Overpriced and the meat was overcooked. Service was slow too.",
                imageUrl = null,
                timestamp = System.currentTimeMillis() - 21600000, // 6 hours ago
                likes = 0,
                location = "Financial District",
                status = PostStatus.PENDING,
                ratings = emptyList(),
                ratingCount = 0,
                averageRating = 0f
            )
        )
        
        // Initialize ratings and comments for sample posts
        ratings["1"] = mutableMapOf(
            "user3" to 4.5f,
            "user4" to 5.0f,
            "user5" to 4.0f
        )
        
        ratings["2"] = mutableMapOf(
            "user1" to 5.0f,
            "user3" to 4.8f,
            "user5" to 4.9f
        )
        
        ratings["3"] = mutableMapOf(
            "user1" to 4.2f,
            "user2" to 4.5f,
            "user4" to 3.8f,
            "user5" to 4.0f
        )
        
        ratings["4"] = mutableMapOf(
            "user1" to 5.0f,
            "user2" to 5.0f,
            "user3" to 4.8f,
            "admin123" to 5.0f,
            "user5" to 4.9f
        )
        
        // Add some comments
        comments["1"] = mutableListOf(
            Comment(
                id = UUID.randomUUID().toString(),
                postId = "1",
                userId = "user3",
                content = "I love this place too! Their carbonara is amazing.",
                timestamp = System.currentTimeMillis() - 43200000
            ),
            Comment(
                id = UUID.randomUUID().toString(),
                postId = "1",
                userId = "user4",
                content = "What's the name of the restaurant?",
                timestamp = System.currentTimeMillis() - 21600000
            )
        )
        
        comments["2"] = mutableListOf(
            Comment(
                id = UUID.randomUUID().toString(),
                postId = "2",
                userId = "user1",
                content = "The dragon roll is my favorite!",
                timestamp = System.currentTimeMillis() - 3600000
            )
        )
    }

    fun addPost(post: Post) {
        val newPost = if (post.id.isEmpty()) {
            post.copy(id = UUID.randomUUID().toString())
        } else {
            post
        }
        Log.d(TAG, "Adding post: $newPost")
        posts.add(newPost)
        ratings[newPost.id] = mutableMapOf()
        comments[newPost.id] = mutableListOf()
    }

    fun getPost(postId: String): Post? {
        return posts.find { it.id == postId }
    }

    fun getAllPosts(): List<Post> {
        return posts.toList() // Return a copy to prevent external modifications
    }

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
    }

    fun getRating(postId: String, userId: String): Float {
        val rating = ratings[postId]?.get(userId) ?: 0f
        Log.d(TAG, "Getting rating for postId=$postId, userId=$userId: $rating")
        return rating
    }

    fun addComment(postId: String, userId: String, content: String) {
        Log.d(TAG, "Adding comment: postId=$postId, userId=$userId")
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
        return comments[postId]?.toList() ?: emptyList() // Return a copy to prevent external modifications
    }

    fun getCommentCount(postId: String): Int {
        return comments[postId]?.size ?: 0
    }

    fun getPostRatings(postId: String): List<Float> {
        return ratings[postId]?.values?.toList() ?: emptyList()
    }

    fun ratePost(postId: String, userId: String, rating: Float) {
        Log.d(TAG, "Rating post: postId=$postId, userId=$userId, rating=$rating")
        addRating(postId, userId, rating)
    }

    fun getCommentsForPost(postId: String): List<Comment> {
        return getComments(postId)
    }

    private fun calculateAverageRating(postId: String): Float {
        val postRatings = ratings[postId]?.values ?: return 0f
        if (postRatings.isEmpty()) return 0f
        return postRatings.sum() / postRatings.size
    }

    fun getPendingPosts(): List<Post> = posts
        .filter { it.status == PostStatus.PENDING }
        .sortedByDescending { it.timestamp }
    
    fun getPostsByUser(userId: String): List<Post> = 
        posts.filter { it.userId == userId }.sortedByDescending { it.timestamp }
    
    fun approvePost(postId: String) {
        Log.d(TAG, "Approving post: $postId")
        val post = posts.find { it.id == postId }
        post?.let {
            val index = posts.indexOf(it)
            posts[index] = it.copy(status = PostStatus.APPROVED)
            Log.d(TAG, "Post approved: ${posts[index]}")
        }
    }
    
    fun rejectPost(postId: String) {
        Log.d(TAG, "Rejecting post: $postId")
        val post = posts.find { it.id == postId }
        post?.let {
            val index = posts.indexOf(it)
            posts[index] = it.copy(status = PostStatus.REJECTED)
            Log.d(TAG, "Post rejected: ${posts[index]}")
        }
    }
    
    fun deletePost(postId: String): Boolean {
        val post = posts.find { it.id == postId } ?: return false
        return posts.remove(post)
    }

    fun getPostById(postId: String): Post? {
        return posts.find { it.id == postId }
    }
    
    fun getApprovedPosts(): List<Post> = posts
        .filter { it.status == PostStatus.APPROVED }
        .sortedByDescending { it.timestamp }
}

