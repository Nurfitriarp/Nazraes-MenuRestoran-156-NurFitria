package com.nurfitria.resmenu.navigation

import android.content.SharedPreferences
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.nurfitria.resmenu.ui.screens.*

@Composable
fun RestoApp(prefs: SharedPreferences, isDarkMode: Boolean, onThemeToggle: (Boolean) -> Unit) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Slide + Fade Transitions
    val enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) = {
        slideIntoContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Left,
            animationSpec = tween(500)
        ) + fadeIn(animationSpec = tween(500))
    }

    val exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) = {
        slideOutOfContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Left,
            animationSpec = tween(500)
        ) + fadeOut(animationSpec = tween(500))
    }

    val popEnterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) = {
        slideIntoContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Right,
            animationSpec = tween(500)
        ) + fadeIn(animationSpec = tween(500))
    }

    val popExitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) = {
        slideOutOfContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Right,
            animationSpec = tween(500)
        ) + fadeOut(animationSpec = tween(500))
    }

    Scaffold(
        bottomBar = {
            val showBottomBar = currentDestination?.route in listOf("home", "menu", "profile", "profile?updated={updated}")
            if (showBottomBar) {
                // MODIFIKASI UTAMA: Membungkus NavigationBar dalam Box melayang dengan padding luar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Transparent) // Membiarkan background luar tembus pandang
                        .navigationBarsPadding() // Mengamankan area navigasi sistem Android
                        .padding(start = 24.dp, end = 24.dp, bottom = 16.dp) // Jarak margin agar melayang seperti gambar 2
                ) {
                    NavigationBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp)
                            .clip(RoundedCornerShape(24.dp)), // Membuat sudut melengkung penuh (Kapsul)
                        containerColor = Color(0xFF121212), // Mengubah background bar menjadi Hitam Pekat premium
                        tonalElevation = 0.dp
                    ) {
                        val items = listOf(
                            Triple("Home", "home", Icons.Default.Home),
                            Triple("Menu", "menu", Icons.Default.MenuBook),
                            Triple("Profile", "profile", Icons.Default.Person)
                        )
                        items.forEach { (label, route, icon) ->
                            val isSelected = currentDestination?.hierarchy?.any { it.route?.startsWith(route) == true } == true

                            NavigationBarItem(
                                icon = {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                        modifier = Modifier.fillMaxHeight()
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = label,
                                            modifier = Modifier.size(24.dp)
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        // IMPLEMENTASI: Indikator garis horizontal kuning/oranye aktif di bawah ikon
                                        Box(
                                            modifier = Modifier
                                                .width(18.dp)
                                                .height(3.dp)
                                                .clip(RoundedCornerShape(2.dp))
                                                .background(if (isSelected) Color(0xFFE65100) else Color.Transparent) // Oranye aksen jika aktif
                                        )
                                    }
                                },
                                label = null, // Menghapus teks bawaan agar bersih & fokus ke ikon + garis seperti di gambar 2
                                selected = isSelected,
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    unselectedIconColor = Color(0xFF9E9E9E),
                                    indicatorColor = Color.Transparent // Menghapus balon oval bawaan Material 3 yang kaku
                                ),
                                onClick = {
                                    navController.navigate(route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "splash",
            // Mengatur padding dinamis agar konten halaman tidak terpotong oleh letak melayang bottom bar
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding() + 20.dp),
            enterTransition = enterTransition,
            exitTransition = exitTransition,
            popEnterTransition = popEnterTransition,
            popExitTransition = popExitTransition
        ) {
            composable("splash") { SplashScreen(navController) }
            composable("home") { HomeScreen(navController, prefs, isDarkMode, onThemeToggle) }
            composable("menu") { MenuScreen(navController, prefs) }
            composable("add_menu") { AddMenuScreen(navController, prefs) }

            composable(
                route = "edit_menu/{menuId}",
                arguments = listOf(navArgument("menuId") { type = NavType.LongType })
            ) { backStackEntry ->
                val menuId = backStackEntry.arguments?.getLong("menuId") ?: 0L
                EditMenuScreen(navController, prefs, menuId)
            }

            composable(
                route = "detail/{menuId}",
                arguments = listOf(navArgument("menuId") { type = NavType.LongType })
            ) { backStackEntry ->
                val menuId = backStackEntry.arguments?.getLong("menuId") ?: 0L
                DetailMenuScreen(navController, menuId, prefs)
            }

            composable(
                route = "profile?updated={updated}",
                arguments = listOf(navArgument("updated") { defaultValue = false; type = NavType.BoolType })
            ) { backStackEntry ->
                val updated = backStackEntry.arguments?.getBoolean("updated") ?: false
                ProfileScreen(navController, prefs, updated)
            }

            composable("edit_profile") { EditProfileScreen(navController, prefs) }
        }
    }
}