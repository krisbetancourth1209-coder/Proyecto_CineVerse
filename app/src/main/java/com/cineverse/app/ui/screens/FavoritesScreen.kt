package com.cineverse.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
fun FavoritesScreen(
    onMovieClick: (Int) -> Unit = {},
    repository: MovieRepository = remember { MovieRepository() }
) {
    val favorites by FavoritesManager.favorites.collectAsState()
    var suggestedMovies by remember { mutableStateOf<List<Movie>>(emptyList()) }
    var loadingSuggestions by remember { mutableStateOf(false) }

    LaunchedEffect(favorites.isEmpty()) {
        if (favorites.isEmpty() && suggestedMovies.isEmpty()) {
            loadingSuggestions = true
            try {
                suggestedMovies = withContext(Dispatchers.IO) {
                    repository.getPopularMovies().take(5)
                }
            } catch (_: Exception) {
            } finally {
                loadingSuggestions = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CinemaPurpleBg)
            .padding(16.dp)
    ) {
        Text(
            text = "Favoritos",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = CinemaGoldBright
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Películas guardadas: ${favorites.size}",
            style = MaterialTheme.typography.bodyMedium,
            color = CinemaTextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (favorites.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CinemaCardBg),
                border = BorderStroke(1.dp, Color(0xFF3B2661)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Aún no tienes películas en favoritos",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CinemaTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Guarda películas desde Géneros o añade alguna sugerencia aquí abajo.",
                        style = MaterialTheme.typography.bodySmall,
                        color = CinemaTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Sugerencias populares",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = CinemaTextPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (loadingSuggestions) {
                CircularProgressIndicator(
                    color = CinemaGold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(suggestedMovies, key = { it.id }) { movie ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onMovieClick(movie.id) },
                            colors = CardDefaults.cardColors(containerColor = CinemaCardBg),
                            border = BorderStroke(1.dp, Color(0xFF3B2661)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = movie.title,
                                    color = CinemaTextPrimary,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = { FavoritesManager.toggleFavorite(movie) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = CinemaGold,
                                        contentColor = CinemaPurpleBg
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("+ Favorito", fontWeight = FontWeight.Bold, maxLines = 1)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(favorites, key = { it.id }) { movie ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onMovieClick(movie.id) },
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
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedButton(
                                    onClick = { FavoritesManager.removeFavorite(movie.id) },
                                    border = BorderStroke(1.dp, Color(0xFFFF8A80)),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color(0xFFFF8A80)
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text("Quitar", fontWeight = FontWeight.SemiBold, maxLines = 1)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}