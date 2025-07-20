package com.example.catapult.users.profile

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.catapult.core.compose.LoadingIndicator
import com.example.catapult.core.compose.NoDataContent
import com.example.catapult.quiz.db.Result
import com.example.catapult.users.account.UserAccount
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun NavGraphBuilder.usersProfile(
    route: String,
    navController: NavController
) = composable(route = route) {
    val viewModel = hiltViewModel<UsersProfileViewModel>()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is UsersProfileContract.SideEffect.NavigateToRegistration -> {
                    navController.navigate("register") {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                    }
                }
            }
        }
    }

    UsersProfileScreen(
        viewModel = viewModel ,
        onLogout = { viewModel.setEvent(UsersProfileContract.UiEvent.Logout) }
    )
}

@Composable
fun UsersProfileScreen(
    viewModel: UsersProfileViewModel,
    onLogout: () -> Unit
) {
    val uiState = viewModel.state.collectAsState()
    UsersProfileScreen(
        uiState = uiState.value,
        onLogout = onLogout
        )
}

@Composable
private fun UsersProfileScreen(
    uiState: UsersProfileContract.UiState,
    onLogout: () -> Unit
) {

    when {
        uiState.isLoading -> LoadingIndicator()
        uiState.error != null -> NoDataContent(text = "Error: ${uiState.error.message}")
        uiState.userAccount == null -> NoDataContent(text = "User not found.")
        else -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        TextButton(
                            onClick = onLogout,
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "Logout",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Logout")
                        }
                    }
                }

                item {
                    ProfileHeader(userAccount = uiState.userAccount)
                }

                item {
                    Divider()
                }

                item {
                    BestPerformanceSection(
                        bestScore = uiState.bestScore,
                        bestRank = uiState.bestRank
                    )
                }

                item {
                    Divider()
                }

                item {
                    SectionTitle(
                        icon = Icons.Default.History,
                        title = "Quiz History"
                    )
                }

                if (uiState.quizResults.isEmpty()) {
                    item {
                        NoDataContent(text = "No quizzes taken yet.")
                    }
                } else {
                    items(uiState.quizResults) { result ->
                        QuizResultItem(result = result)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(userAccount: UserAccount) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Profile",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "${userAccount.firstName} ${userAccount.lastName}",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "@${userAccount.username}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = "Email",
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = userAccount.email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun BestPerformanceSection(bestScore: Result?, bestRank: Int?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            PerformanceStat(
                icon = Icons.Default.Star,
                label = "Best Score",
                value = bestScore?.let { String.format("%.2f", it.score) } ?: "N/A"
            )
            PerformanceStat(
                icon = Icons.Default.EmojiEvents,
                label = "Best Rank",
                value = bestRank?.toString() ?: "N/A"
            )
        }
    }
}

@Composable
private fun PerformanceStat(icon: ImageVector, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, style = MaterialTheme.typography.labelMedium)
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SectionTitle(icon: ImageVector, title: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(imageVector = icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary)
        Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

@SuppressLint("DefaultLocale")
@Composable
private fun QuizResultItem(result: Result) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Score: ${String.format("%.2f", result.score)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(result.createdAt)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (result.isPublished) {
                Icon(
                    imageVector = Icons.Default.Public,
                    contentDescription = "Published",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
