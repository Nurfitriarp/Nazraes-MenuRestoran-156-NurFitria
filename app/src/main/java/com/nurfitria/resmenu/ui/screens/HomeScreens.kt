package com.nurfitria.resmenu.ui.screens

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    prefs: SharedPreferences,
    isDarkMode: Boolean,
    onThemeToggle: (Boolean) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val restoName = remember(navBackStackEntry) { prefs.getString("name", "Nazraes") ?: "Nazraes" }
    val restoAddress = remember(navBackStackEntry) { prefs.getString("address", "Jl. Mawar, Malang") ?: "Jl. Mawar, Malang" }
    val restoDesc = remember(navBackStackEntry) { prefs.getString("description", "Cocok untuk berkumpul keluarga, tempat nongkrong, atau restoran dengan menu nusantara yang menghadirkan suasana hangat seperti rumah sendiri") ?: "Cocok untuk berkumpul keluarga, tempat nongkrong, atau restoran dengan menu nusantara yang menghadirkan suasana hangat seperti rumah sendiri" }
    val restoHours = remember(navBackStackEntry) { prefs.getString("hours", "08:00 - 21:00") ?: "08:00 - 21:00" }

    val context = LocalContext.current
    var isFabExpanded by remember { mutableStateOf(false) }
    val fabRotation by animateFloatAsState(if (isFabExpanded) 45f else 0f)

    // PALETTE WARNA BARU (Hitam, Cokelat, Beige)
    val deepBlack = Color(0xFF1A1A1A)
    val warmBrown = Color(0xFF7A5C43)
    val softBeige = Color(0xFFF7F4EB)

    val bannerImages = listOf(
        "https://images.unsplash.com/photo-1552566626-52f8b828add9?w=800&q=80",
        "https://images.unsplash.com/photo-1514933651103-005eec06c04b?w=800&q=80",
        "https://images.unsplash.com/photo-1414235077428-338989a2e8c0?w=800&q=80"
    )

    val pagerState = rememberPagerState(pageCount = { bannerImages.size + 1 })

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Selamat Datang Di",
                            style = MaterialTheme.typography.labelMedium,
                            // Saat Light Mode = Hitam transparan, Saat Dark Mode = Putih transparan
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                        Text(
                            text = restoName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            // Saat Light Mode = Hitam pekat, Saat Dark Mode = Putih pekat
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onThemeToggle(!isDarkMode) }) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme",
                            tint = MaterialTheme.colorScheme.onBackground // <-- Ganti ini
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AnimatedVisibility(
                    visible = isFabExpanded,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    SmallFloatingActionButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:08123456789")
                            }
                            context.startActivity(intent)
                        },
                        shape = CircleShape,
                        containerColor = softBeige,
                        contentColor = warmBrown
                    ) {
                        Icon(Icons.Default.Call, contentDescription = "Call")
                    }
                }
                AnimatedVisibility(
                    visible = isFabExpanded,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    SmallFloatingActionButton(
                        onClick = {
                            val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(restoAddress)}")
                            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                            mapIntent.setPackage("com.google.android.apps.maps")
                            context.startActivity(mapIntent)
                        },
                        shape = CircleShape,
                        containerColor = softBeige,
                        contentColor = warmBrown
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Location")
                    }
                }
                FloatingActionButton(
                    onClick = { isFabExpanded = !isFabExpanded },
                    containerColor = warmBrown, // FAB menggunakan Cokelat
                    contentColor = Color.White
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "More",
                        modifier = Modifier.rotate(fabRotation)
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                pageSpacing = 16.dp
            ) { page ->
                if (page < bannerImages.size) {
                    AsyncImage(
                        model = bannerImages[page],
                        contentDescription = "Banner Restoran $page",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Card(
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = softBeige // Menggunakan Beige hangat untuk kartu info
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = warmBrown, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(restoAddress, style = MaterialTheme.typography.bodySmall, color = deepBlack, maxLines = 1)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Schedule, contentDescription = null, tint = warmBrown, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(restoHours, style = MaterialTheme.typography.bodySmall, color = deepBlack)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = warmBrown.copy(alpha = 0.2f))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                restoDesc,
                                style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                                color = deepBlack.copy(alpha = 0.8f),
                                maxLines = 3
                            )
                        }
                    }
                }
            }

            // Pager Dots warna Cokelat & Beige
            Row(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pagerState.pageCount) { iteration ->
                    val color = if (pagerState.currentPage == iteration) warmBrown else warmBrown.copy(alpha = 0.3f)
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Promo Spesial Hari Ini",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground // <-- Ganti ini dari deepBlack
            )

            Spacer(modifier = Modifier.height(12.dp))

            PromoCarousel(navController)

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

data class PromoItem(val title: String, val discount: String, val icon: ImageVector, val menuId: Long)

@Composable
fun PromoCarousel(navController: NavHostController) {
    val promos = listOf(
        PromoItem("Makan Siang Hemat", "Diskon 20%", Icons.Default.LunchDining, 1L),
        PromoItem("Happy Hour Kopi", "Beli 1 Gratis 1", Icons.Default.Coffee, 5L),
        PromoItem("Paket Keluarga", "Hemat Rp 50rb", Icons.Default.Groups, 3L)
    )

    // KONFIGURASI WARNA DI CAROUSEL PROMO
    val softBeigeColor = Color(0xFFF7F4EB) // Warna latar kartu (Beige)
    val deepBlackText = Color(0xFF1A1A1A)  // Warna judul utama (Hitam)
    val warmBrownIcon = Color(0xFF7A5C43)  // Warna ikon background (Cokelat)

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(promos) { promo ->
            Box(
                modifier = Modifier
                    .width(280.dp)
                    .height(160.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(softBeigeColor) // Set background ke Beige
                    .clickable { navController.navigate("detail/${promo.menuId}") }
                    .padding(20.dp)
            ) {
                Column(modifier = Modifier.align(Alignment.TopStart)) {
                    Text(promo.title, color = deepBlackText, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text(promo.discount, color = deepBlackText.copy(alpha = 0.6f), fontSize = 16.sp)
                }
                Icon(
                    imageVector = promo.icon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .align(Alignment.BottomEnd)
                        .offset(x = 10.dp, y = 10.dp),
                    tint = warmBrownIcon.copy(alpha = 0.12f) // Ikon bayangan menggunakan warna Cokelat transparan agar sangat senada
                )
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    val context = LocalContext.current
    val dummyPrefs = context.getSharedPreferences("dummy_prefs", android.content.Context.MODE_PRIVATE)

    HomeScreen(
        navController = androidx.navigation.compose.rememberNavController(),
        prefs = dummyPrefs,
        isDarkMode = false,
        onThemeToggle = {}
    )
}