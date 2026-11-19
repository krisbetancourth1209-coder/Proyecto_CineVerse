package com.cineverse.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cineverse.app.data.MovieRepository
import com.cineverse.app.data.model.Movie
import com.cineverse.app.ui.components.MovieCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val CinemaPurpleBg = Color(0xFF140B24)
private val CinemaGold = Color(0xFFD4AF37)
private val CinemaGoldBright = Color(0xFFF3D06B)
private val CinemaTextPrimary = Color(0xFFF8F5FF)
private val CinemaTextSecondary = Color(0xFFD6CCE8)

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
            .background(CinemaPurpleBg)
            .padding(16.dp)
    ) {
        Text(
            text = "CineVerse",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = CinemaGoldBright
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Tu universo cinematográfico",
            style = MaterialTheme.typography.bodyMedium,
            color = CinemaTextSecondary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Películas populares",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = CinemaTextPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        when {
            loading -> {
                CircularProgressIndicator(color = CinemaGold)
            }

            error != null -> {
                Text(
                    text = "Error: $error",
                    color = Color(0xFFFF8A80)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onExploreClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CinemaGold,
                        contentColor = CinemaPurpleBg
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Ir al catálogo", fontWeight = FontWeight.Bold)
                }
            }

            movies.isEmpty() -> {
                Text(
                    text = "No hay películas disponibles.",
                    color = CinemaTextSecondary
                )
            }

            else -> {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
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
            colors = ButtonDefaults.buttonColors(
                containerColor = CinemaGold,
                contentColor = CinemaPurpleBg
            ),
            border = BorderStroke(1.dp, CinemaGoldBright),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(
                text = "Explorar catálogo",
                fontWeight = FontWeight.Bold
            )
        }
    }
}
