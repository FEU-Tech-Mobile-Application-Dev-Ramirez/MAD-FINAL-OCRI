package com.example.food_traveler

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.food_traveler.data.UserRepository
import com.example.food_traveler.ui.navigation.NavigationItem
import com.example.food_traveler.ui.screens.*
import com.example.food_traveler.ui.theme.FoodTravelerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FoodTravelerTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val showBottomBar = remember(currentRoute) {
        currentRoute != "login" && currentRoute != "signup" && currentRoute != "welcome"
    }
    val isLoggedIn = remember { UserRepository.getCurrentUser() != null }
    
    val startDestination = remember {
        if (isLoggedIn) NavigationItem.Discover.route else "login"
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    listOf(
                        NavigationItem.Discover,
                        NavigationItem.Reviews,
                        NavigationItem.Profile,
                        NavigationItem.Admin
                    ).forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = null) },
                            label = { Text(stringResource(item.title)) },
                            selected = currentRoute == item.route,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            NavHost(
                navController = navController,
                startDestination = startDestination
            ) {
                composable("login") {
                    LoginScreen(
                        onLoginSuccess = {
                            navController.navigate(NavigationItem.Discover.route) {
                                popUpTo("login") { inclusive = true }
                            }
                        },
                        onSignupClick = {
                            navController.navigate("signup")
                        }
                    )
                }
                
                composable("signup") {
                    SignupScreen(
                        onSignupSuccess = {
                            navController.navigate(NavigationItem.Discover.route) {
                                popUpTo("login") { inclusive = true }
                            }
                        },
                        onBackClick = {
                            navController.popBackStack()
                        }
                    )
                }
                
                composable(NavigationItem.Discover.route) {
                    DiscoverScreen()
                }
                
                composable(NavigationItem.Reviews.route) {
                    ReviewsScreen()
                }
                
                composable(NavigationItem.Profile.route) {
                    ProfileScreen(
                        onLogout = {
                            UserRepository.logout()
                            navController.navigate("login") {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    inclusive = true
                                }
                            }
                        }
                    )
                }
                
                composable(NavigationItem.Admin.route) {
                    AdminScreen()
                }
            }
        }
    }
}