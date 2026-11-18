
package com.cineverse.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*

import com.cineverse.app.ui.screens.HomeScreen
import com.cineverse.app.ui.screens.CatalogScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    val items = listOf(
        "home" to "Inicio",
        "catalog" to "Catálogo"
    )

    Scaffold(
        bottomBar = {
            NavigationBar {

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
                            Text(label)
                        }
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
        }
    }
}
