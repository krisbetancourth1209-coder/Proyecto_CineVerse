
package com.cineverse.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cineverse.app.data.MovieRepository
import com.cineverse.app.data.model.Genre
import com.cineverse.app.data.model.Movie
import com.cineverse.app.ui.components.MovieCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun CatalogScreen() {

    var movies by remember { mutableStateOf<List<Movie>>(emptyList()) }
    var genres by remember { mutableStateOf<List<Genre>>(emptyList()) }

    var selectedGenre by remember { mutableStateOf<Int?>(null) }
    var searchText by remember { mutableStateOf("") }

    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            val repository = MovieRepository()

            val result = withContext(Dispatchers.IO) {
                repository.getGenres()
            }

            genres = result

        } catch (e: Exception) {
            error = e.message ?: "No se pudieron cargar los géneros"
        }
    }

    LaunchedEffect(selectedGenre) {
        loading = true
        error = null

        try {
            movies = withContext(Dispatchers.IO) {
                MovieRepository().getMovies(selectedGenre)
            }
        } catch (e: Exception) {
            error = e.message ?: "No se pudieron cargar las películas"
        } finally {
            loading = false
        }
    }

    val filteredMovies = movies.filter {
        it.title.contains(searchText, ignoreCase = true)
    }

    // Usamos LazyVerticalGrid para que toda la pantalla se desplace fluidamente sin conflictos
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Encabezado: Título
        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                text = "Explorar películas",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        // Espaciador
        item(span = { GridItemSpan(maxLineSpan) }) {
            Spacer(modifier = Modifier.height(4.dp))
        }

        // Buscador
        item(span = { GridItemSpan(maxLineSpan) }) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Buscar películas...")
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
        }

        // Espaciador
        item(span = { GridItemSpan(maxLineSpan) }) {
            Spacer(modifier = Modifier.height(4.dp))
        }

        // Título de Géneros
        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                text = "Géneros",
                style = MaterialTheme.typography.titleMedium
            )
        }

        // Espaciador
        item(span = { GridItemSpan(maxLineSpan) }) {
            Spacer(modifier = Modifier.height(2.dp))
        }

        // Chips de Géneros
        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedGenre == null,
                    onClick = { selectedGenre = null },
                    label = { Text("Todos") }
                )

                genres.take(4).forEach { genre ->
                    FilterChip(
                        selected = selectedGenre == genre.id,
                        onClick = {
                            selectedGenre = genre.id
                        },
                        label = { Text(genre.name) }
                    )
                }
            }
        }

        // Espaciador antes de la cuadrícula de películas
        item(span = { GridItemSpan(maxLineSpan) }) {
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Estados de carga, error o resultados
        when {
            loading -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            error != null -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = "Error: $error",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            filteredMovies.isEmpty() -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text("No se encontraron películas.")
                }
            }

            else -> {
                items(filteredMovies) { movie ->
                    MovieCard(movie = movie)
                }
            }
        }
    }
}
