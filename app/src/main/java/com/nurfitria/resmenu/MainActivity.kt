package com.nurfitria.resmenu


import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import com.nurfitria.resmenu.navigation.RestoApp
import com.nurfitria.resmenu.ui.theme.ResMenuTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("resto_prefs", Context.MODE_PRIVATE)

        enableEdgeToEdge()
        setContent {
            val systemTheme = isSystemInDarkTheme()
            var isDarkMode by remember {
                mutableStateOf(prefs.getBoolean("dark_mode", systemTheme))
            }

            ResMenuTheme(darkTheme = isDarkMode) {
                RestoApp(
                    prefs = prefs,
                    isDarkMode = isDarkMode,
                    onThemeToggle = { dark ->
                        isDarkMode = dark
                        prefs.edit().putBoolean("dark_mode", dark).apply()
                    }
                )
            }
        }
    }
}
