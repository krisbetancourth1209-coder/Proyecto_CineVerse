package com.cineverse.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cineverse.app.data.MovieRepository
import com.cineverse.app.data.model.Genre
import com.cineverse.app.data.model.Movie
import com.cineverse.app.ui.components.MovieCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val CinemaPurpleBg = Color(0xFF140B24)
private val CinemaPurpleCard = Color(0xFF22133B)
private val CinemaGold = Color(0xFFD4AF37)
private val CinemaGoldBright = Color(0xFFF3D06B)
private val CinemaTextPrimary = Color(0xFFF8F5FF)
private val CinemaTextSecondary = Color(0xFFD6CCE8)

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

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .background(CinemaPurpleBg)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                text = "Explorar películas",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = CinemaGoldBright
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Spacer(modifier = Modifier.height(4.dp))
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Buscar películas...", color = CinemaTextSecondary)
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CinemaPurpleCard,
                    unfocusedContainerColor = CinemaPurpleCard,
                    focusedTextColor = CinemaTextPrimary,
                    unfocusedTextColor = CinemaTextPrimary,
                    cursorColor = CinemaGold,
                    focusedBorderColor = CinemaGold,
                    unfocusedBorderColor = CinemaGold.copy(alpha = 0.5f)
                )
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Spacer(modifier = Modifier.height(4.dp))
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                text = "Géneros",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = CinemaTextPrimary
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Spacer(modifier = Modifier.height(2.dp))
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedGenre == null,
                    onClick = { selectedGenre = null },
                    label = { Text("Todos", fontWeight = FontWeight.Medium) },
                    border = BorderStroke(1.dp, CinemaGold.copy(alpha = 0.7f)),
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = CinemaPurpleCard,
                        labelColor = CinemaTextPrimary,
                        selectedContainerColor = CinemaGold,
                        selectedLabelColor = CinemaPurpleBg
                    )
                )

                genres.take(4).forEach { genre ->
                    FilterChip(
                        selected = selectedGenre == genre.id,
                        onClick = {
                            selectedGenre = genre.id
                        },
                        label = { Text(genre.name, fontWeight = FontWeight.Medium) },
                        border = BorderStroke(1.dp, CinemaGold.copy(alpha = 0.7f)),
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = CinemaPurpleCard,
                            labelColor = CinemaTextPrimary,
                            selectedContainerColor = CinemaGold,
                            selectedLabelColor = CinemaPurpleBg
                        )
                    )
                }
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Spacer(modifier = Modifier.height(8.dp))
        }

        when {
            loading -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = CinemaGold)
                    }
                }
            }

            error != null -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = "Error: $error",
                        color = Color(0xFFFF8A80)
                    )
                }
            }

            filteredMovies.isEmpty() -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = "No se encontraron películas.",
                        color = CinemaTextSecondary
                    )
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
