package com.example.catapult.breeds.list

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.catapult.breeds.model.BreedsListUiModel
import com.example.catapult.core.compose.LoadingIndicator
import com.example.catapult.core.compose.NoDataContent
import com.example.catapult.core.compose.TextToChips

fun NavGraphBuilder.breedsList(
    route: String,
    navController: NavController
) = composable(
    route = route,
    arguments = listOf(navArgument(name = "q") {
        type = NavType.StringType
        nullable = true
        defaultValue = ""
    })
) {
    val viewModel = hiltViewModel<BreedsListViewModel>()
    BreedsListScreen(
        viewModel = viewModel,
        onBreedListClick = { breedId: String ->
            navController.navigate(route = "details/$breedId")
        },
        onSearch = { query: String ->
            navController.navigate(route = "list?q=${query}")
        }
    )
}

@Composable
fun BreedsListScreen(
    viewModel: BreedsListViewModel,
    onBreedListClick: (id: String) -> Unit,
    onSearch: (String) -> Unit
) {
    val uiState = viewModel.state.collectAsState()
    BreedsListScreen(uiState.value, onBreedListClick, onSearch)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BreedsListScreen(
    state: BreedsListContract.UiState,
    onBreedListClick: (id: String) -> Unit,
    onSearch: (String) -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    var isSearchActive by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Main content
        AnimatedContent(
            targetState = when {
                state.loading -> ContentState.Loading
                state.error != null -> ContentState.Error
                state.data.isEmpty() -> ContentState.Empty
                else -> ContentState.Data
            },
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "content_transition",
            modifier = Modifier.fillMaxSize()
        ) { contentState ->
            when (contentState) {
                ContentState.Loading -> LoadingIndicator()
                ContentState.Error -> NoDataContent(
                    modifier = Modifier.padding(top = 80.dp),
                    text = "Error while fetching data:\n${state.error?.message}"
                )
                ContentState.Empty -> NoDataContent(
                    modifier = Modifier.padding(top = 80.dp),
                    text = "No breeds found"
                )
                ContentState.Data -> BreedsListContent(
                    modifier = Modifier.fillMaxSize(),
                    data = state.data,
                    onBreedListClick = onBreedListClick,
                    topPadding = 80.dp
                )
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(1f),
            color = MaterialTheme.colorScheme.background,
            shadowElevation = if (isSearchActive) 0.dp else 4.dp
        ) {
            MaterialSearchBar(
                query = query,
                onQueryChange = { query = it },
                onSearch = {
                    onSearch(it)
                    isSearchActive = false
                },
                active = isSearchActive,
                onActiveChange = { isSearchActive = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    active: Boolean,
    onActiveChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    SearchBar(
        query = query,
        onQueryChange = onQueryChange,
        onSearch = { onSearch(query) },
        active = active,
        onActiveChange = onActiveChange,
        modifier = modifier
            .padding(horizontal = 12.dp)
            .height(56.dp),
        placeholder = {
            Text(
                text = "Search cat breeds...",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = {
                    onQueryChange("")
                    onSearch("")
                }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        colors = SearchBarDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            dividerColor = Color.Transparent
        ),
        windowInsets = WindowInsets(0),
        tonalElevation = 2.dp
    ) {
    }
}


@Composable
private fun BreedsListContent(
    modifier: Modifier = Modifier,
    data: List<BreedsListUiModel>,
    onBreedListClick: (id: String) -> Unit,
    topPadding: Dp = 0.dp
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            top = topPadding + 16.dp,
            bottom = 16.dp,
            start = 16.dp,
            end = 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = data,
            key = { breed -> breed.id }
        ) { breed ->
            BreedListItem(
                data = breed,
                onClick = { onBreedListClick(breed.id) }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BreedListItem(
    data: BreedsListUiModel,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp,
            pressedElevation = 3.dp
        )
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = buildString {
                        append(data.name)
                        if (data.alt.trim().isNotEmpty()) {
                            append(" (${data.alt})")
                        }
                    },
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            },
            supportingContent = {
                Column {
                    Text(
                        text = data.description,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (data.temperament.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            TextToChips(
                                text = data.temperament,
                                amount = 4,
                                delim = ',',
                                onClick = onClick
                            )
                        }
                    }
                }
            },
            colors = ListItemDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }
}

private enum class ContentState {
    Loading, Error, Empty, Data
}