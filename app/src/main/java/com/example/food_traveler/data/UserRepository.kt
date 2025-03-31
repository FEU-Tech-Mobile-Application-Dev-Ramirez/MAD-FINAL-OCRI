package com.example.food_traveler.data

import com.example.food_traveler.model.User

object UserRepository {
    private val users = mutableMapOf<String, User>()
    private var currentUser: User? = null
    
    init {
        // Add a dedicated admin user with fixed credentials
        val adminUser = User(
            id = "admin123",
            email = "admin@foodtraveler.com",
            displayName = "Food Traveler Admin",
            isAdmin = true
        )
        users[adminUser.id] = adminUser
    }
    
    fun getCurrentUser(): User? = currentUser
    
    fun setCurrentUser(user: User) {
        currentUser = user
        if (!users.containsKey(user.id)) {
            users[user.id] = user
        }
    }
    
    fun getUserById(id: String): User? = users[id]
    
    fun isCurrentUserAdmin(): Boolean = currentUser?.isAdmin == true
    
    fun updateUser(user: User) {
        // Prevent changing admin status through update
        val existingUser = users[user.id]
        val updatedUser = if (existingUser?.isAdmin == true) {
            user.copy(isAdmin = true)
        } else {
            user.copy(isAdmin = false)
        }
        users[user.id] = updatedUser
        if (currentUser?.id == user.id) {
            currentUser = updatedUser
        }
    }
    
    fun logout() {
        currentUser = null
    }

    fun getAllUsers(): List<User> = users.values.toList()

    fun login(email: String, password: String): Boolean {
        val user = users.values.find { it.email == email }
        if (user != null) {
            currentUser = user
            return true
        }
        return false
    }

    fun createUser(email: String, displayName: String): User {
        val newUser = User(
            id = "user${users.size + 1}",
            email = email,
            displayName = displayName,
            isAdmin = false  // Ensure new users are never admins
        )
        users[newUser.id] = newUser
        return newUser
    }
}
