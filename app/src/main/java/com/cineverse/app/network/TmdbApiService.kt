package com.cineverse.app.network

import com.cineverse.app.data.model.GenreResponse
import com.cineverse.app.data.model.Movie
import com.cineverse.app.data.model.MovieResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApiService {

    @GET(value = "movie/popular")
    suspend fun getPopularMovies(
        @Query(value = "api_key") apiKey: String,
        @Query(value = "language") language: String = "es-ES",
        @Query(value = "page") page: Int = 1
    ): MovieResponse

    @GET(value = "discover/movie")
    suspend fun getMovies(
        @Query(value = "api_key") apiKey: String,
        @Query(value = "language") language: String = "es-ES",
        @Query(value = "page") page: Int = 1,
        @Query(value = "with_genres") genreId: Int? = null
    ): MovieResponse

    @GET(value = "genre/movie/list")
    suspend fun getGenres(
        @Query(value = "api_key") apiKey: String,
        @Query(value = "language") language: String = "es-ES"
    ): GenreResponse

    // Tus nuevos endpoints añadidos
    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("language") language: String = "es-ES"
    ): MovieResponse

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "es-ES"
    ): Movie
}
