package com.cineverse.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.cineverse.app.data.FavoritesManager
import com.cineverse.app.data.MovieRepository
import com.cineverse.app.data.model.Genre
import com.cineverse.app.data.model.Movie
import com.cineverse.app.ui.components.MovieCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val CinemaPurpleBg = Color(0xFF140B24)
private val CinemaCardBg = Color(0xFF21133A)
private val CinemaGold = Color(0xFFD4AF37)
private val CinemaGoldBright = Color(0xFFF3D06B)
private val CinemaTextPrimary = Color(0xFFF8F5FF)
private val CinemaTextSecondary = Color(0xFFD6CCE8)

@Composable
fun GenresScreen(
    onMovieClick: (Int) -> Unit = {},
    repository: MovieRepository = remember { MovieRepository() }
) {
    var genres by remember { mutableStateOf<List<Genre>>(emptyList()) }
    var selectedGenre by remember { mutableStateOf<Genre?>(null) }
    var movies by remember { mutableStateOf<List<Movie>>(emptyList()) }
    var isLoadingGenres by remember { mutableStateOf(true) }
    var isLoadingMovies by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val favorites by FavoritesManager.favorites.collectAsState()

    LaunchedEffect(Unit) {
        isLoadingGenres = true
        errorMessage = null
        try {
            val fetchedGenres = withContext(Dispatchers.IO) {
                repository.getGenres()
            }
            genres = fetchedGenres
            if (fetchedGenres.isNotEmpty()) {
                selectedGenre = fetchedGenres.first()
            }
        } catch (e: Exception) {
            errorMessage = e.localizedMessage ?: "No se pudieron cargar los géneros"
        } finally {
            isLoadingGenres = false
        }
    }

    LaunchedEffect(selectedGenre?.id) {
        val currentGenre = selectedGenre ?: return@LaunchedEffect
        isLoadingMovies = true
        errorMessage = null
        try {
            movies = withContext(Dispatchers.IO) {
                repository.getMoviesByGenre(genreId = currentGenre.id)
            }
        } catch (e: Exception) {
            errorMessage = e.localizedMessage ?: "No se pudieron cargar las películas"
        } finally {
            isLoadingMovies = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CinemaPurpleBg)
            .padding(16.dp)
    ) {
        Text(
            text = "Géneros",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = CinemaGoldBright
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Explora películas por categoría",
            style = MaterialTheme.typography.bodyMedium,
            color = CinemaTextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoadingGenres -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = CinemaGold)
                }
            }

            genres.isNotEmpty() -> {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(genres, key = { it.id }) { genre ->
                        val isSelected = selectedGenre?.id == genre.id
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedGenre = genre },
                            label = {
                                Text(
                                    text = genre.name,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = CinemaCardBg,
                                labelColor = CinemaTextSecondary,
                                selectedContainerColor = CinemaGold,
                                selectedLabelColor = CinemaPurpleBg
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) CinemaGoldBright else Color(0xFF3B2661)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        selectedGenre?.let { genre ->
            Text(
                text = "Películas de ${genre.name}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = CinemaTextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (errorMessage != null) {
            Text(
                text = "Error: $errorMessage",
                color = Color(0xFFFF8A80),
                style = MaterialTheme.typography.bodyMedium
            )
        } else if (isLoadingMovies) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = CinemaGold)
            }
        } else if (movies.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay películas disponibles para este género.",
                    color = CinemaTextSecondary
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(movies, key = { it.id }) { movie ->
                    val isFav = favorites.any { it.id == movie.id }
                    GenreMovieRow(
                        movie = movie,
                        isFavorite = isFav,
                        onMovieClick = { onMovieClick(movie.id) },
                        onToggleFavorite = { FavoritesManager.toggleFavorite(movie) }
                    )
                }
            }
        }
    }
}

@Composable
private fun GenreMovieRow(
    movie: Movie,
    isFavorite: Boolean,
    onMovieClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onMovieClick() },
        colors = CardDefaults.cardColors(containerColor = CinemaCardBg),
        border = BorderStroke(1.dp, Color(0xFF3B2661)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.width(84.dp)) {
                MovieCard(movie = movie)
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CinemaTextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "★ ${String.format("%.1f", movie.voteAverage)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CinemaGoldBright,
                    fontWeight = FontWeight.SemiBold
                )

                movie.releaseDate?.takeIf { it.isNotBlank() }?.let { date ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = date,
                        style = MaterialTheme.typography.bodySmall,
                        color = CinemaTextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (isFavorite) {
                    Button(
                        onClick = onToggleFavorite,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CinemaGold,
                            contentColor = CinemaPurpleBg
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("En favoritos", fontWeight = FontWeight.Bold, maxLines = 1)
                    }
                } else {
                    OutlinedButton(
                        onClick = onToggleFavorite,
                        border = BorderStroke(1.dp, CinemaGold),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CinemaGoldBright),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Guardar", fontWeight = FontWeight.SemiBold, maxLines = 1)
                    }
                }
            }
        }
    }
}