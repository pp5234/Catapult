import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.catapult.breeds.details.BreedsDetailsContract
import com.example.catapult.breeds.details.BreedsDetailsViewModel
import com.example.catapult.breeds.model.BreedsDetailsUiModel
import com.example.catapult.core.compose.LoadingIndicator
import com.example.catapult.core.compose.NoDataContent
import com.example.catapult.core.compose.TextToChips

fun NavGraphBuilder.breedsDetails(
    route: String,
    navController: NavController
) = composable(
    route = route,
    arguments = listOf(navArgument(name = "breedId") {
        type = NavType.StringType
        nullable = true
        defaultValue = null
    })
) {
    val viewModel = hiltViewModel<BreedsDetailsViewModel>()
    BreedsDetailsScreen(
        viewModel = viewModel,
        onGalleryClick = { breedId ->
            navController.navigate("gallery/$breedId")
        }
    )
}

@Composable
fun BreedsDetailsScreen(viewModel: BreedsDetailsViewModel, onGalleryClick: (String) -> Unit) {
    val uiState = viewModel.state.collectAsState()
    BreedsDetailsScreen(uiState.value, onGalleryClick)
}

@Composable
private fun BreedsDetailsScreen(
    state: BreedsDetailsContract.UIState,
    onGalleryClick: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when {
            state.loading -> LoadingIndicator()
            state.error != null -> NoDataContent("Error when fetching details: ${state.error}")
            state.data == null -> NoDataContent("No details about this breed")
            else -> BreedsDetailsScreenContent(breed = state.data, onGalleryClick = onGalleryClick)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun BreedsDetailsScreenContent(
    breed: BreedsDetailsUiModel,
    onGalleryClick: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Section
        BreedHeaderSection(breed = breed)

        // Image Section with Gallery Button
        BreedImageSection(breed = breed, onGalleryClick = onGalleryClick)

        // Quick Info Section
        BreedQuickInfoSection(breed = breed)

        // Description Section
        BreedDescriptionSection(breed = breed)

        // Temperament Section
        BreedTemperamentSection(breed = breed)

        // Behavioral Traits Section
        BreedBehavioralTraitsSection(breed = breed)

        // Wikipedia Button
        if (breed.wikipediaUrl.isNotEmpty()) {
            WikiButton(breed.wikipediaUrl)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun BreedHeaderSection(breed: BreedsDetailsUiModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = breed.name,
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        if (breed.isRare == 1) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Rare Breed",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun BreedImageSection(
    breed: BreedsDetailsUiModel,
    onGalleryClick: (String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val borderModifier = if (breed.isRare == 1) {
                Modifier.border(
                    width = 3.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(12.dp)
                )
            } else Modifier

            BreedImage(
                modifier = Modifier
                    .size(250.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .then(borderModifier),
                url = breed.imageUrl,
                description = "${breed.name} Image",
                contentScale = ContentScale.Fit
            )

            FilledTonalButton(
                onClick = { onGalleryClick(breed.id) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Collections,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("View Gallery")
            }
        }
    }
}

@Composable
private fun BreedQuickInfoSection(breed: BreedsDetailsUiModel) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            QuickInfoItem(title = "Origin", value = breed.origin)
            QuickInfoItem(title = "Life Span", value = "${breed.lifeSpan} years")
            QuickInfoItem(title = "Weight", value = "${breed.weightMetric} kg")
        }
    }
}

@Composable
private fun QuickInfoItem(title: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun BreedDescriptionSection(breed: BreedsDetailsUiModel) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Description",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = breed.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BreedTemperamentSection(breed: BreedsDetailsUiModel) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Temperament",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextToChips(
                    text = breed.temperament,
                    delim = ','
                )
            }
        }
    }
}

@Composable
private fun BreedBehavioralTraitsSection(breed: BreedsDetailsUiModel) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Behavioral Traits",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                BreedBehaviorTraitIndicator(label = "Adaptability", level = breed.adaptability)
                BreedBehaviorTraitIndicator(label = "Intelligence", level = breed.intelligence)
                BreedBehaviorTraitIndicator(label = "Vocalisation", level = breed.vocalisation)
                BreedBehaviorTraitIndicator(label = "Health Issues", level = breed.healthIssues)
                BreedBehaviorTraitIndicator(label = "Grooming Needs", level = breed.grooming)
                BreedBehaviorTraitIndicator(label = "Social Needs", level = breed.socialNeeds)
                BreedBehaviorTraitIndicator(label = "Affection Level", level = breed.affectionLevel)
                BreedBehaviorTraitIndicator(label = "Energy Level", level = breed.energyLevel)
                BreedBehaviorTraitIndicator(label = "Shedding Level", level = breed.sheddingLevel)
                BreedBehaviorTraitIndicator(label = "Child Friendly", level = breed.childFriendly)
                BreedBehaviorTraitIndicator(label = "Dog Friendly", level = breed.dogFriendly)
                BreedBehaviorTraitIndicator(label = "Stranger Friendly", level = breed.strangerFriendly)
            }
        }
    }
}

@Composable
private fun WikiButton(wikipediaUrl: String) {
    val context = LocalContext.current

    OutlinedButton(
        onClick = {
            val intent = Intent(Intent.ACTION_VIEW, wikipediaUrl.toUri())
            context.startActivity(intent)
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.Language,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("View on Wikipedia")
    }
}

@Composable
private fun BreedImage(
    modifier: Modifier = Modifier,
    url: String?,
    description: String,
    contentScale: ContentScale
) {
    if (!url.isNullOrEmpty()) {
        SubcomposeAsyncImage(
            model = url,
            contentDescription = description,
            modifier = modifier,
            contentScale = contentScale,
            loading = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingIndicator()
                }
            },
            error = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.errorContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Failed to load image",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        textAlign = TextAlign.Center
                    )
                }
            },
            success = {
                SubcomposeAsyncImageContent()
            }
        )
    } else {
        Box(
            modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No Image Available",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun BreedBehaviorTraitIndicator(
    label: String,
    level: Int,
    maxLevel: Int = 5
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1.3f)
        )

        LinearProgressIndicator(
            progress = { level / maxLevel.toFloat() },
            modifier = Modifier
                .weight(1f)
                .height(8.dp),
            color = when {
                level <= 2 -> MaterialTheme.colorScheme.error
                level <= 3 -> MaterialTheme.colorScheme.tertiary
                else -> MaterialTheme.colorScheme.primary
            },
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeCap = StrokeCap.Round
        )

        Text(
            text = "$level/$maxLevel",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(32.dp),
            textAlign = TextAlign.End
        )
    }
}