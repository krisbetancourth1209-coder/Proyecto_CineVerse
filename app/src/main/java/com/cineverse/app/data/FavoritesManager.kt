package com.cineverse.app.data

import com.cineverse.app.data.model.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object FavoritesManager {
    private val _favorites = MutableStateFlow<List<Movie>>(emptyList())
    val favorites: StateFlow<List<Movie>> = _favorites.asStateFlow()

    fun isFavorite(movieId: Int): Boolean {
        return _favorites.value.any { it.id == movieId }
    }

    fun toggleFavorite(movie: Movie) {
        _favorites.update { currentList ->
            val exists = currentList.any { it.id == movie.id }
            if (exists) {
                currentList.filterNot { it.id == movie.id }
            } else {
                currentList + movie
            }
        }
    }

    fun removeFavorite(movieId: Int) {
        _favorites.update { currentList ->
            currentList.filterNot { it.id == movieId }
        }
    }
}