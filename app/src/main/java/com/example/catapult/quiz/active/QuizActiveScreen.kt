package com.example.catapult.quiz.active

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import coil3.compose.rememberAsyncImagePainter
import com.example.catapult.quiz.active.QuizQuestionContract.UiEvent
import com.example.catapult.quiz.model.QuizQuestionUiModel
import com.example.catapult.quiz.start.QuizType

fun NavGraphBuilder.quizActiveScreen(
    route: String,
    navController: NavHostController
) = composable(
    route = "$route?quizType={quizType}",
    arguments = listOf(
        navArgument("quizType") { type = NavType.StringType }
    )
) { backStackEntry ->
    val quizTypeValue = backStackEntry.arguments?.getString("quizType") ?: QuizType.BREED.value
    val quizType = QuizType.entries.firstOrNull { it.value == quizTypeValue } ?: QuizType.BREED
    val viewModel: QuizActiveViewModel = hiltViewModel()

    LaunchedEffect(quizType) { viewModel.handleEvent(UiEvent.Initialize(quizType)) }

    val state by viewModel.state.collectAsState()

    BackHandler(enabled = !state.isQuizCompleted && !state.isLoading) {
        viewModel.handleEvent(UiEvent.ShowCancelDialog)
    }

    LaunchedEffect(state.navigateBack) {
        if (state.navigateBack) {
            navController.popBackStack("quiz_start", inclusive = false)
            viewModel.handleEvent(UiEvent.NavigationCompleted)
        }
    }

    QuizActiveScreen(
        state = state,
        onEvent = { event -> viewModel.handleEvent(event) },
        onNavigateBack = { navController.popBackStack("quiz_start", inclusive = false) }
    )
}

@Composable
fun QuizActiveScreen(
    state: QuizQuestionContract.UiState,
    onEvent: (UiEvent) -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            if (!state.isQuizCompleted && !state.isLoading && state.error == null) {
                QuizTopBar(state, onEvent)
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                state.isLoading -> CircularProgressIndicator()
                state.error != null -> QuizError(error = state.error, onNavigateBack = onNavigateBack)
                state.isQuizCompleted -> QuizFinished(state = state, onEvent = onEvent)
                state.questions.isNotEmpty() -> QuizContent(state = state, onEvent = onEvent)
            }
        }
    }

    if (state.showCancelDialog) {
        AlertDialog(
            onDismissRequest = { onEvent(UiEvent.HideCancelDialog) },
            title = { Text("Leave Quiz?") },
            text = { Text("Your progress will not be saved. Are you sure you want to exit?") },
            confirmButton = {
                TextButton(onClick = { onEvent(UiEvent.CancelAndSaveQuiz) }) { Text("Leave") }
            },
            dismissButton = {
                TextButton(onClick = { onEvent(UiEvent.HideCancelDialog) }) { Text("Stay") }
            }
        )
    }
}

@Composable
private fun QuizContent(
    state: QuizQuestionContract.UiState,
    onEvent: (UiEvent) -> Unit
) {
    val q = state.questions[state.currentQuestionIndex]
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LinearProgressIndicator(
            progress = { (state.currentQuestionIndex + 1) / state.questions.size.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        if (isLandscape) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                QuestionImage(
                    imageUrl = q.imageUrl,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    AnswerButtons(q = q, state = state, onEvent = onEvent)
                }
            }
        } else {
            QuestionImage(
                imageUrl = q.imageUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )
            AnswerButtons(
                q = q,
                state = state,
                onEvent = onEvent,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun AnswerButtons(
    q: QuizQuestionUiModel,
    state: QuizQuestionContract.UiState,
    onEvent: (UiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
    ) {
        q.options.forEach { option ->
            val isSelected = state.userAnswers[state.currentQuestionIndex] == option
            val hasAnswered = state.userAnswers.containsKey(state.currentQuestionIndex)

            val buttonColors = if (isSelected) {
                ButtonDefaults.filledTonalButtonColors()
            } else {
                ButtonDefaults.outlinedButtonColors()
            }

            OutlinedButton(
                onClick = { if (!hasAnswered) onEvent(UiEvent.SelectAnswer(option)) },
                modifier = Modifier.fillMaxWidth(),
                colors = buttonColors,
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                Text(option, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuizTopBar(
    state: QuizQuestionContract.UiState,
    onEvent: (UiEvent) -> Unit
) {
    val timerColor = if (state.timeLeft < 30) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface

    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Question ${state.currentQuestionIndex + 1} / ${state.questions.size}",
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            IconButton(onClick = { onEvent(UiEvent.ShowCancelDialog) }) {
                Icon(Icons.Default.Close, contentDescription = "Cancel Quiz")
            }
        },
        actions = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = "Time left",
                    tint = timerColor
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = formatTime(state.timeLeft),
                    style = MaterialTheme.typography.titleMedium,
                    color = timerColor
                )
            }
        }
    )
}

@Composable
private fun QuizFinished(
    state: QuizQuestionContract.UiState,
    onEvent: (UiEvent) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text("Quiz Finished!", style = MaterialTheme.typography.displaySmall)
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Score: ${"%.2f".format(state.finalScore)}",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(Modifier.height(32.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(onClick = { onEvent(UiEvent.SaveResultLocal) }) { Text("Save Locally") }
            Button(onClick = { onEvent(UiEvent.SaveResultGlobal) }) { Text("Save & Publish") }
        }
    }
}

@Composable
private fun QuizError(
    error: String,
    onNavigateBack: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = error,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.height(24.dp))
        Button(onClick = onNavigateBack) { Text("Go Back") }
    }
}

@Composable
private fun QuestionImage(imageUrl: String, modifier: Modifier = Modifier) {
    Image(
        painter = rememberAsyncImagePainter(imageUrl),
        contentDescription = "Quiz cat image",
        modifier = modifier
            .clip(RoundedCornerShape(16.dp)),
        contentScale = ContentScale.Crop
    )
}


private fun formatTime(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return "%d:%02d".format(m, s)
}