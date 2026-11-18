package com.cineverse.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import com.cineverse.app.navigation.AppNavigation

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val darkColors = darkColorScheme(
                primary = Color(0xFFE9B44C),
                secondary = Color(0xFFB7A5D5),
                background = Color(0xFF101116),
                surface = Color(0xFF1B1C24)
            )

            val lightColors = lightColorScheme(
                primary = Color(0xFF805B16)
            )

            MaterialTheme(
                colorScheme = if (isSystemInDarkTheme()) {
                    darkColors
                } else {
                    lightColors
                }
            ) {
                AppNavigation()
            }
        }
    }
}
