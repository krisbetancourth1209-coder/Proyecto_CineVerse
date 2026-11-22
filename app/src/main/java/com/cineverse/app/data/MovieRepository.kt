package com.cineverse.app.data

import com.cineverse.app.data.model.Genre
import com.cineverse.app.data.model.Movie
import com.cineverse.app.network.RetrofitClient

class MovieRepository {
    private val api = RetrofitClient.api
    private val apiKey = "9ccfcd95cd0f8e11c27fd1d38fc0a0cb"

    suspend fun getPopularMovies(): List<Movie> {
        return api.getPopularMovies(apiKey).results
    }

    suspend fun getMovies(genreId: Int? = null, page: Int = 1): List<Movie> {
        return api.getMovies(
            apiKey = apiKey,
            page = page,
            genreId = genreId
        ).results
    }

    suspend fun getGenres(): List<Genre> {
        return api.getGenres(apiKey).genres
    }

    suspend fun getMoviesByGenre(genreId: Int, page: Int = 1): List<Movie> {
        return api.getMovies(
            apiKey = apiKey,
            page = page,
            genreId = genreId
        ).results
    }

    suspend fun getMovieDetails(movieId: Int): Movie {
        return api.getMovieDetails(
            movieId = movieId,
            apiKey = apiKey
        )
    }

    suspend fun searchMovies(query: String): List<Movie> {
        return api.searchMovies(
            apiKey = apiKey,
            query = query
        ).results
    }
}