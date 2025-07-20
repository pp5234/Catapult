package com.example.catapult.navigation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavBottomBar(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object CatList : NavBottomBar(
        route = "list?q={q}",
        title = "Cats",
        icon = Icons.Default.Pets
    )

    object Quiz : NavBottomBar(
        route = "quiz_start",
        title = "Quiz",
        icon = Icons.Default.Quiz
    )

    object Leaderboard : NavBottomBar(
        route = "leaderboard",
        title = "Leaderboard",
        icon = Icons.Default.Leaderboard
    )

    object Profile : NavBottomBar(
        route = "profile",
        title = "Profile",
        icon = Icons.Default.Person
    )
}