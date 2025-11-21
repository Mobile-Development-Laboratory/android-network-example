package ru.mephi.nucleardroid.network

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.mephi.nucleardroid.network.model.Amiibo
import ru.mephi.nucleardroid.network.network.AmiiboService
import ru.mephi.nucleardroid.network.network.http.HttpUrlConnectionService
import ru.mephi.nucleardroid.network.network.ktor.KtorService
import ru.mephi.nucleardroid.network.network.retrofit.RetrofitService

enum class NetworkLibrary {
    HTTP_URL_CONNECTION, RETROFIT, KTOR
}

private const val AMIIBO_LIMIT = 20

class MainActivity : ComponentActivity() {

    private val httpRepository: AmiiboService = HttpUrlConnectionService()
    private val retrofitRepository: AmiiboService = RetrofitService()
    private val ktorRepository: AmiiboService = KtorService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AmiiboAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AmiiboScreen(
                        httpRepository = httpRepository,
                        retrofitRepository = retrofitRepository,
                        ktorRepository = ktorRepository
                    )
                }
            }
        }
    }
}

@Composable
fun AmiiboAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmiiboScreen(
    httpRepository: AmiiboService,
    retrofitRepository: AmiiboService,
    ktorRepository: AmiiboService
) {
    var selectedLibrary by remember { mutableStateOf(NetworkLibrary.HTTP_URL_CONNECTION) }
    var amiibos by remember { mutableStateOf(emptyList<Amiibo>()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val repository = when (selectedLibrary) {
        NetworkLibrary.HTTP_URL_CONNECTION -> httpRepository
        NetworkLibrary.RETROFIT -> retrofitRepository
        NetworkLibrary.KTOR -> ktorRepository
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Amiibo Collection",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NetworkLibrary.entries.forEach { library ->
                    FilterChip(
                        selected = selectedLibrary == library,
                        onClick = {
                            if (!isLoading) {
                                if (selectedLibrary != library) {
                                    amiibos = emptyList()
                                    errorMessage = null
                                }
                                selectedLibrary = library
                            }
                        },
                        label = {
                            Text(
                                text = when (library) {
                                    NetworkLibrary.HTTP_URL_CONNECTION -> "HTTP"
                                    NetworkLibrary.RETROFIT -> "Retrofit"
                                    NetworkLibrary.KTOR -> "Ktor"
                                },
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading
                    )
                }
            }

            Button(
                onClick = {
                    fetchAmiibos(
                        repository = repository,
                        onLoading = {
                            isLoading = true
                            amiibos = emptyList()
                            errorMessage = null
                        },
                        onSuccess = { amiiboList ->
                            isLoading = false
                            amiibos = amiiboList
                            errorMessage = null
                        },
                        onError = { error ->
                            isLoading = false
                            errorMessage = error
                            amiibos = emptyList()
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                enabled = !isLoading
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = if (isLoading) "Loading..." else "Fetch Amiibos")
            }

            if (errorMessage != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = "Error: $errorMessage",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else if (amiibos.isNotEmpty()) {
                Text(
                    text = "Loaded ${amiibos.size} amiibos",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            if (amiibos.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items = amiibos) { amiibo ->
                        AmiiboItem(amiibo = amiibo)
                    }
                }
            } else if (!isLoading && errorMessage == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Image(
                            imageVector = Icons.Default.Face,
                            contentDescription = "No amiibos",
                            modifier = Modifier.size(64.dp),
                            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        Text(
                            text = "Select a library and fetch amiibos",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (isLoading && amiibos.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator()
                        Text(
                            text = "Loading amiibos...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AmiiboItem(amiibo: Amiibo) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            AsyncImage(
                model = amiibo.image,
                contentDescription = "Amiibo image",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                placeholder = rememberVectorPainter(Icons.Default.Face),
                error = rememberVectorPainter(Icons.Default.Face)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = amiibo.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Character: ${amiibo.character}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "Series: ${amiibo.amiiboSeries}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "Type: ${amiibo.type}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "Game: ${amiibo.gameSeries}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun fetchAmiibos(
    repository: AmiiboService,
    onLoading: () -> Unit,
    onSuccess: (List<Amiibo>) -> Unit,
    onError: (String) -> Unit
) {
    CoroutineScope(Dispatchers.Main).launch {
        onLoading()

        val result = withContext(Dispatchers.IO) {
            repository.getAllAmiibos()
        }

        result.onSuccess { amiiboResponse ->
            val limitedAmiibos = amiiboResponse.amiibo.take(AMIIBO_LIMIT)
            onSuccess(limitedAmiibos)
        }.onFailure { exception ->
            onError(exception.message ?: "Unknown error occurred")
        }
    }
}