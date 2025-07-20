package com.example.catapult.breeds.gallery

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import coil3.request.ImageRequest
import coil3.ImageLoader
import coil3.request.crossfade
import com.example.catapult.breeds.model.ImageUiModel
import kotlinx.coroutines.launch

fun NavGraphBuilder.imageViewer(
    route: String,
    navController: NavController
) = composable(
    route = route,
    arguments = listOf(navArgument("index") { type = NavType.IntType })
) { entry ->
    val galleryEntry = remember(entry) {
        navController.getBackStackEntry("gallery/{breedId}")
    }
    val viewModel = hiltViewModel<BreedsGalleryViewModel>(galleryEntry)
    val imageIndex = entry.arguments?.getInt("index") ?: 0

    PhotoViewerScreen(
        viewModel = viewModel,
        initialImageIndex = imageIndex,
        onBackClick = { navController.navigateUp() }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhotoViewerScreen(
    viewModel: BreedsGalleryViewModel,
    initialImageIndex: Int,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.state.collectAsState()
    val pagerState = rememberPagerState(
        initialPage = initialImageIndex,
        pageCount = { uiState.images.size }
    )

    PreloadImages(
        images = uiState.images,
        currentPage = pagerState.currentPage
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        PhotoViewerContent(
            state = uiState,
            pagerState = pagerState,
            modifier = Modifier.fillMaxSize()
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .align(Alignment.TopStart)
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = "${pagerState.currentPage + 1} of ${uiState.images.size}",
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}

@Composable
private fun PreloadImages(
    images: List<ImageUiModel>,
    currentPage: Int
) {
    val context = LocalContext.current
    val imageLoader = remember { ImageLoader(context) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(currentPage, images.size) {
        if (images.isEmpty()) return@LaunchedEffect

        scope.launch {
            listOf(currentPage - 1, currentPage, currentPage + 1)
                .filter { it in images.indices }
                .forEach { index ->
                    runCatching {
                        val url = images[index].url
                        val request = ImageRequest.Builder(context)
                            .data(url)
                            .crossfade(true)
                            .build()
                        imageLoader.enqueue(request)
                    }
                }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PhotoViewerContent(
    state: BreedsGalleryContract.UIState,
    pagerState: PagerState,
    modifier: Modifier = Modifier
) {
    when {
        state.loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
        state.error != null -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error loading images: ${state.error}",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        state.images.isEmpty() -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No images available",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        else -> HorizontalPager(
            state = pagerState,
            modifier = modifier,
            beyondViewportPageCount = 1,
            pageSpacing = 0.dp
        ) { page ->
            PhotoViewerImage(
                imageUrl = state.images[page].url,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun PhotoViewerImage(
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    SubcomposeAsyncImage(
        model = ImageRequest.Builder(context)
            .data(imageUrl)
            .crossfade(true)
            .crossfade(300)
            .build(),
        contentDescription = "Cat Photo",
        modifier = modifier,
        contentScale = ContentScale.Fit,
        loading = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(48.dp),
                    strokeWidth = 4.dp
                )
            }
        },
        error = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Failed to load image",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        },
        success = {
            SubcomposeAsyncImageContent()
        }
    )
}


