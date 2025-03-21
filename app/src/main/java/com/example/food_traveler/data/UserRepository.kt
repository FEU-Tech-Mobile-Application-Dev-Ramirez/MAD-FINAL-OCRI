package com.example.food_traveler.data

import com.example.food_traveler.model.User

object UserRepository {
    // Predefined users
    private val users = listOf(
        User("1", "user1", "password1", "User 1", "profile1.jpg"),
        User("2", "user2", "password2", "User 2", "profile2.jpg"),
        User("3", "user3", "password3", "User 3", "profile3.jpg")
    )
    
    // Current logged in user
    private var currentUser: User? = null
    
    fun login(username: String, password: String): Boolean {
        val user = users.find { it.username == username && it.password == password }
        if (user != null) {
            currentUser = user
            return true
        }
        return false
    }
    
    fun getCurrentUser(): User? = currentUser
    
    fun logout() {
        currentUser = null
    }
    
    fun getAllUsers(): List<User> = users
}
