package com.cineverse.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.cineverse.app.data.MovieRepository
import com.cineverse.app.data.model.Movie
import kotlinx.coroutines.launch

private val CinemaPurpleBg = Color(0xFF140B24)
private val CinemaPurpleCard = Color(0xFF22133B)
private val CinemaGold = Color(0xFFD4AF37)
private val CinemaGoldBright = Color(0xFFF3D06B)
private val CinemaTextPrimary = Color(0xFFF8F5FF)
private val CinemaTextSecondary = Color(0xFFD6CCE8)

@Composable
fun DetailScreen(movieId: Int) {
    var movie by remember { mutableStateOf<Movie?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val repository = remember { MovieRepository() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(movieId) {
        if (movieId <= 0) {
            errorMessage = "ID de película no válido."
            isLoading = false
            return@LaunchedEffect
        }

        isLoading = true
        errorMessage = null
        scope.launch {
            try {
                movie = repository.getMovieDetails(movieId)
            } catch (e: Exception) {
                errorMessage = "Error al cargar detalles: ${e.localizedMessage ?: "revisa tu conexión"}"
            } finally {
                isLoading = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CinemaPurpleBg)
            .padding(16.dp)
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    color = CinemaGold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            errorMessage != null -> {
                Text(
                    text = errorMessage ?: "",
                    color = Color(0xFFFF8A80),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            movie != null -> {
                val currentMovie = movie!!
                val posterUrl = currentMovie.posterPath?.let { path ->
                    if (path.isNotBlank()) "https://image.tmdb.org/t/p/w500$path" else null
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (posterUrl != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(CinemaPurpleCard)
                                .border(
                                    width = 2.5.dp,
                                    color = CinemaGold,
                                    shape = RoundedCornerShape(18.dp)
                                )
                                .padding(6.dp)
                        ) {
                            AsyncImage(
                                model = posterUrl,
                                contentDescription = currentMovie.title ?: "Póster de película",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(360.dp)
                                    .clip(RoundedCornerShape(14.dp))
                            )
                        }
                    }

                    // Título destacado con acento dorado
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = CinemaPurpleCard,
                        border = BorderStroke(1.dp, CinemaGold.copy(alpha = 0.65f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = currentMovie.title ?: "Sin título",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = CinemaGoldBright
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "★ ${String.format("%.1f", currentMovie.voteAverage)} / 10",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = CinemaGold
                                )

                                if (!currentMovie.releaseDate.isNullOrBlank()) {
                                    Text(
                                        text = "• Estreno: ${currentMovie.releaseDate}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = CinemaTextSecondary
                                    )
                                }
                            }
                        }
                    }

                    Text(
                        text = "Sinopsis",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CinemaGoldBright
                    )

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = CinemaPurpleCard,
                        border = BorderStroke(1.dp, CinemaGold.copy(alpha = 0.45f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = currentMovie.overview?.ifBlank { "Sin sinopsis disponible." }
                                ?: "Sin sinopsis disponible.",
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight,
                            color = CinemaTextPrimary,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }
            }
        }
    }
}