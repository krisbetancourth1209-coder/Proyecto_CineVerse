
package com.cineverse.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cineverse.app.data.MovieRepository
import com.cineverse.app.data.model.Movie
import com.cineverse.app.ui.components.MovieCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun HomeScreen(
    onExploreClick: () -> Unit
) {
    var movies by remember { mutableStateOf<List<Movie>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            movies = withContext(Dispatchers.IO) {
                MovieRepository().getPopularMovies()
            }
        } catch (e: Exception) {
            error = e.message ?: "No se pudieron cargar las películas"
        } finally {
            loading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "CineVerse",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Tu universo cinematográfico",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Películas populares",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        when {
            loading -> {
                CircularProgressIndicator()
            }

            error != null -> {
                Text(
                    text = "Error: $error",
                    color = MaterialTheme.colorScheme.error
                )

                Button(onClick = onExploreClick) {
                    Text("Ir al catálogo")
                }
            }

            movies.isEmpty() -> {
                Text("No hay películas disponibles.")
            }

            else -> {
                LazyRow(
                    horizontalArrangement =
                        Arrangement.spacedBy(14.dp)
                ) {
                    items(movies) { movie ->
                        MovieCard(movie = movie)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onExploreClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Explorar catálogo")
        }
    }
}
