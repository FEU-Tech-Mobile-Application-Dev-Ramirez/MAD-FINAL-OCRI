package com.example.food_traveler.data

import com.example.food_traveler.model.Post
import java.util.Date
import java.util.UUID

object PostRepository {
    private val posts = mutableListOf(
        Post(
            id = "1",
            userId = "1",
            title = "Amazing Italian Dinner",
            content = "Had the most amazing pasta at La Piazza last night. The ambiance was perfect and the service was exceptional. The chef even came out to greet us! Definitely worth a visit if you're in the area.",
            imageUrl = "restaurant1.jpg",
            restaurantId = null,
            timestamp = Date(System.currentTimeMillis() - 86400000),
            likes = 15
        ),
        Post(
            id = "2",
            userId = "1",
            title = "Best Breakfast Spot",
            content = "Found this hidden gem for breakfast. The pancakes are fluffy and the coffee is strong. Just what you need to start your day!",
            imageUrl = "breakfast.jpg",
            restaurantId = null,
            timestamp = Date(System.currentTimeMillis() - 432000000),
            likes = 8
        ),
        
        Post(
            id = "3",
            userId = "2",
            title = "Sushi Experience",
            content = "The freshest sushi I've ever had. Chef's special roll was out of this world! I highly recommend trying the omakase menu - the chef's selection was outstanding and introduced me to flavors I wouldn't have tried otherwise.",
            imageUrl = "restaurant2.jpg",
            restaurantId = null,
            timestamp = Date(System.currentTimeMillis() - 172800000),
            likes = 12
        ),
        Post(
            id = "4",
            userId = "2",
            title = "Street Food Festival",
            content = "Visited the annual street food festival downtown. So many amazing vendors and flavors from around the world. My favorite was the Thai curry stand!",
            imageUrl = "streetfood.jpg",
            restaurantId = null,
            timestamp = Date(System.currentTimeMillis() - 345600000),
            likes = 20
        ),
        
        Post(
            id = "5",
            userId = "3",
            title = "Mexican Fiesta",
            content = "The tacos at Taco Fiesta are authentic and delicious. Highly recommend! The homemade salsa is to die for. I asked for the recipe but it's a family secret passed down for generations.",
            imageUrl = "restaurant3.jpg",
            restaurantId = null,
            timestamp = Date(System.currentTimeMillis() - 259200000),
            likes = 18
        ),
        Post(
            id = "6",
            userId = "3",
            title = "Farm to Table Experience",
            content = "Visited a local farm restaurant where everything is grown on site. The freshness of the ingredients really comes through in every dish. We even got a tour of the gardens!",
            imageUrl = "farm.jpg",
            restaurantId = null,
            timestamp = Date(System.currentTimeMillis() - 518400000),
            likes = 14
        )
    )
    
    fun getAllPosts(): List<Post> = posts.sortedByDescending { it.timestamp }
    
    fun getPostsByUser(userId: String): List<Post> = 
        posts.filter { it.userId == userId }.sortedByDescending { it.timestamp }
    
    fun addPost(post: Post) {
        val newPost = if (post.id.isEmpty()) {
            post.copy(id = UUID.randomUUID().toString())
        } else {
            post
        }
        posts.add(newPost)
    }
    
    fun likePost(postId: String) {
        val post = posts.find { it.id == postId }
        post?.let {
            val index = posts.indexOf(it)
            posts[index] = it.copy(likes = it.likes + 1)
        }
    }
    
    fun deletePost(postId: String): Boolean {
        val post = posts.find { it.id == postId } ?: return false
        return posts.remove(post)
    }
}

