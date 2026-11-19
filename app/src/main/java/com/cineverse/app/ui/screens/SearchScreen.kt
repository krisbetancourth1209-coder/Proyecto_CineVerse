package com.cineverse.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
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
fun SearchScreen(onMovieClick: (Int) -> Unit) {
    var query by remember { mutableStateOf("") }
    var movies by remember { mutableStateOf<List<Movie>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val repository = remember { MovieRepository() }
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    fun searchNow() {
        val cleanQuery = query.trim()
        if (cleanQuery.isEmpty()) {
            errorMessage = "Escribe el nombre de una película primero."
            return
        }

        keyboardController?.hide()
        isLoading = true
        errorMessage = null

        scope.launch {
            try {
                movies = repository.searchMovies(cleanQuery)
                if (movies.isEmpty()) {
                    errorMessage = "No se encontraron resultados para \"$cleanQuery\"."
                }
            } catch (e: Exception) {
                errorMessage = "Error al buscar: ${e.localizedMessage ?: "revisa tu conexión"}"
            } finally {
                isLoading = false
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
            text = "Buscar películas",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = CinemaGoldBright
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Buscar película...", color = CinemaTextSecondary) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { searchNow() }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CinemaPurpleCard,
                unfocusedContainerColor = CinemaPurpleCard,
                focusedTextColor = CinemaTextPrimary,
                unfocusedTextColor = CinemaTextPrimary,
                cursorColor = CinemaGold,
                focusedBorderColor = CinemaGold,
                unfocusedBorderColor = CinemaGold.copy(alpha = 0.5f)
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { searchNow() },
            colors = ButtonDefaults.buttonColors(
                containerColor = CinemaGold,
                contentColor = CinemaPurpleBg
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Listo", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = CinemaGold)
            }
        } else if (errorMessage != null) {
            Text(
                text = errorMessage ?: "",
                color = Color(0xFFFF8A80),
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(movies) { movie ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, CinemaGold.copy(alpha = 0.55f)),
                        colors = CardDefaults.cardColors(
                            containerColor = CinemaPurpleCard
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = movie.title ?: "Sin título",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = CinemaTextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "★ ${String.format("%.1f", movie.voteAverage)}",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = CinemaGoldBright
                            )
                            if (!movie.releaseDate.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Estreno: ${movie.releaseDate}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = CinemaTextSecondary
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { onMovieClick(movie.id) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CinemaGold,
                                    contentColor = CinemaPurpleBg
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Detalles", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}