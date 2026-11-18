package com.cineverse.app.data

import com.cineverse.app.BuildConfig
import com.cineverse.app.data.model.Genre
import com.cineverse.app.data.model.Movie
import com.cineverse.app.network.RetrofitClient

class MovieRepository {

    private val api = RetrofitClient.api

    private val apiKey = BuildConfig.TMDB_API_KEY

    suspend fun getPopularMovies(): List<Movie> {
        return api.getPopularMovies(apiKey).results
    }

    suspend fun getMovies(genreId: Int? = null): List<Movie> {
        return api.getMovies(
            apiKey = apiKey,
            genreId = genreId
        ).results
    }

    suspend fun getGenres(): List<Genre> {
        return api.getGenres(apiKey).genres
    }
}