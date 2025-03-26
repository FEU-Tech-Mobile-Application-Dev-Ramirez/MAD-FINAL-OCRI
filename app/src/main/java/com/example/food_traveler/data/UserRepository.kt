package com.example.food_traveler.data

import com.example.food_traveler.model.User

object UserRepository {
    private val users = mutableMapOf<String, User>()
    private var currentUser: User? = null
    
    init {
        // Add a default admin user
        val adminUser = User(
            id = "admin123",
            email = "admin@example.com",
            displayName = "Admin",
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
        users[user.id] = user
        if (currentUser?.id == user.id) {
            currentUser = user
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
            isAdmin = false
        )
        users[newUser.id] = newUser
        return newUser
    }
}
