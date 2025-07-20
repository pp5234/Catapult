package com.example.catapult.navigation

import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import breedsDetails
import com.example.catapult.breeds.gallery.breedsGallery
import com.example.catapult.breeds.gallery.imageViewer
import com.example.catapult.breeds.list.breedsList
import com.example.catapult.leaderboard.leaderboardScreen
import com.example.catapult.quiz.active.quizActiveScreen
import com.example.catapult.quiz.start.quizStart

import com.example.catapult.users.profile.usersProfile
import com.example.catapult.users.registration.registrationScreen

@Composable
fun Navigation(
    startDestination: String,
    navController: NavHostController = rememberNavController(),
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    val bottomNavRoutes = setOf(
        "list?q={q}",
        "quiz_start",
        "leaderboard",
        "profile"
    )
    val showBottomBar = currentDestination?.route in bottomNavRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController, currentDestination)
            }
        }
    ) { paddingValues ->
        val navModifier = if (showBottomBar) {
            Modifier.padding(paddingValues)
        } else {
            Modifier.padding(
                top = paddingValues.calculateTopPadding(),
                start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                end = paddingValues.calculateEndPadding(LayoutDirection.Ltr)
            )
        }

        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = navModifier
        ) {
            registrationScreen(
                route = "register",
                navController = navController
            )
            breedsList(
                route = "list?q={q}",
                navController = navController
            )
            breedsDetails(
                route = "details/{breedId}",
                navController = navController
            )
            breedsGallery(
                route = "gallery/{breedId}",
                navController = navController
            )
            imageViewer(
                route = "images/{index}",
                navController = navController
            )
            usersProfile(
                route = "profile",
                navController = navController
            )
            leaderboardScreen(
                route = "leaderboard",
            )
            quizStart(
                route = "quiz_start",
                navController = navController
            )
            quizActiveScreen(
                route = "quiz_active/{quizType}",
                navController = navController
            )
        }
    }
}

@Composable
private fun BottomNavigationBar(
    navController: NavHostController,
    currentDestination: NavDestination?
) {
    val items = listOf(
        NavBottomBar.CatList,
        NavBottomBar.Quiz,
        NavBottomBar.Leaderboard,
        NavBottomBar.Profile
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        items.forEach { destination ->
            val selected = currentDestination?.hierarchy?.any { it.route == destination.route } == true
            NavigationBarItem(
                icon = { Icon(imageVector = destination.icon, contentDescription = destination.title) },
                label = { Text(destination.title, style = MaterialTheme.typography.labelSmall) },
                selected = selected,
                onClick = {
                    navController.navigate(destination.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
