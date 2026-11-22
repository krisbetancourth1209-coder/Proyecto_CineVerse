package com.cineverse.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.cineverse.app.ui.screens.CatalogScreen
import com.cineverse.app.ui.screens.DetailScreen
import com.cineverse.app.ui.screens.FavoritesScreen
import com.cineverse.app.ui.screens.GenresScreen
import com.cineverse.app.ui.screens.HomeScreen
import com.cineverse.app.ui.screens.SearchScreen

private val CinemaNavBg = Color(0xFF1A0F2E)
private val CinemaGold = Color(0xFFD4AF37)
private val CinemaTextSecondary = Color(0xFFD6CCE8)

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    val items = listOf(
        "home" to "Inicio",
        "catalog" to "Catálogo",
        "genres" to "Géneros",
        "favorites" to "Favoritos",
        "search" to "Buscar"
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = CinemaNavBg
            ) {

                val currentRoute =
                    navController.currentBackStackEntryAsState()
                        .value?.destination?.route

                items.forEach { (route, label) ->

                    NavigationBarItem(
                        selected = currentRoute == route,
                        onClick = {
                            navController.navigate(route) {
                                popUpTo(
                                    navController.graph.findStartDestination().id
                                ) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {},
                        label = {
                            Text(
                                text = label,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CinemaGold,
                            selectedTextColor = CinemaGold,
                            indicatorColor = Color(0xFF2C1A4D),
                            unselectedIconColor = CinemaTextSecondary,
                            unselectedTextColor = CinemaTextSecondary
                        )
                    )
                }
            }
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(padding)
        ) {

            composable("home") {
                HomeScreen(
                    onExploreClick = {
                        navController.navigate("catalog")
                    }
                )
            }

            composable("catalog") {
                CatalogScreen()
            }

            composable("genres") {
                GenresScreen(
                    onMovieClick = { movieId ->
                        navController.navigate("detail/$movieId")
                    }
                )
            }

            composable("favorites") {
                FavoritesScreen(
                    onMovieClick = { movieId ->
                        navController.navigate("detail/$movieId")
                    }
                )
            }

            composable("search") {
                SearchScreen(
                    onMovieClick = { movieId ->
                        navController.navigate("detail/$movieId")
                    }
                )
            }

            composable("detail/{movieId}") { backStackEntry ->
                val movieId = backStackEntry.arguments?.getString("movieId")?.toIntOrNull() ?: 0
                DetailScreen(movieId = movieId)
            }
        }
    }
}