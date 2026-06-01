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
import androidx.compose.ui.text.style.TextAlign
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
    val restoName = remember(navBackStackEntry) { prefs.getString("name", "NAZRAES") ?: "NAZRAES" }
    val restoAddress = remember(navBackStackEntry) { prefs.getString("address", "Jl. Mawar, Malang") ?: "Jl. Mawar, Malang" }

    val context = LocalContext.current
    var isFabExpanded by remember { mutableStateOf(false) }
    val fabRotation by animateFloatAsState(if (isFabExpanded) 45f else 0f)

    // State untuk memfilter kategori aktif (Makanan atau Minuman)
    var selectedCategory by remember { mutableStateOf("Makanan") }

    // PALETTE WARNA ULTRA DARK PREMIUM (Hitam Dominan, Aksentuasi Cokelat Oranye & Soft Grey)
    val bgBlack = Color(0xFF0D0D0D)
    val cardDark = Color(0xFF1A1A1A)
    val accentOrangeBrown = Color(0xFFE65100) // Warna oranye-cokelat tegas seperti tombol "All Type"
    val textGrey = Color(0xFF9E9E9E)

    val bannerImages = listOf(
        "https://images.unsplash.com/photo-1552566626-52f8b828add9?w=800&q=80",
        "https://images.unsplash.com/photo-1514933651103-005eec06c04b?w=800&q=80",
        "https://images.unsplash.com/photo-1414235077428-338989a2e8c0?w=800&q=80"
    )
    val pagerState = rememberPagerState(pageCount = { bannerImages.size })

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = bgBlack, // Mengubah latar belakang Scaffold menjadi hitam pekat
        topBar = {
            TopAppBar(
                title = {
                    // IMPLEMENTASI KEDUA: Judul Alamat Tengah atas diubah menjadi Nama Restoran "NAZRAES"
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(end = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = textGrey, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = restoName.uppercase(),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 1.5.sp
                        )
                    }
                },
                navigationIcon = {
                    // Profil Avatar melingkar kecil di sudut kiri atas seperti mockup target
                    Surface(
                        shape = CircleShape,
                        color = accentOrangeBrown.copy(alpha = 0.2f),
                        modifier = Modifier.padding(start = 16.dp).size(36.dp)
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.padding(6.dp))
                    }
                },
                actions = {
                    IconButton(onClick = { onThemeToggle(!isDarkMode) }) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bgBlack)
            )
        },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                AnimatedVisibility(visible = isFabExpanded, enter = fadeIn() + slideInVertically(), exit = fadeOut() + slideOutVertically()) {
                    SmallFloatingActionButton(
                        onClick = { context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:08123456789"))) },
                        shape = CircleShape, containerColor = cardDark, contentColor = Color.White
                    ) { Icon(Icons.Default.Call, contentDescription = "Call") }
                }
                AnimatedVisibility(visible = isFabExpanded, enter = fadeIn() + slideInVertically(), exit = fadeOut() + slideOutVertically()) {
                    SmallFloatingActionButton(
                        onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=${Uri.encode(restoAddress)}"))) },
                        shape = CircleShape, containerColor = cardDark, contentColor = Color.White
                    ) { Icon(Icons.Default.LocationOn, contentDescription = "Location") }
                }
                FloatingActionButton(
                    onClick = { isFabExpanded = !isFabExpanded },
                    containerColor = accentOrangeBrown,
                    contentColor = Color.White,
                    shape = CircleShape
                ) { Icon(Icons.Default.Add, contentDescription = "More", modifier = Modifier.rotate(fabRotation)) }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(bgBlack)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Ucapan Selamat Datang / Headline utama bergaya modern
            Text(text = "Hello, Food Lover", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
            Text(text = "Let's explore Nazraes world", fontSize = 14.sp, color = textGrey, fontWeight = Modifier.Companion.let { FontWeight.Normal })

            Spacer(modifier = Modifier.height(20.dp))

            // IMPLEMENTASI PERTAMA: Mengubah barisan kategori menjadi MAKANAN & MINUMAN saja
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf("Makanan", "Minuman").forEach { category ->
                    val isSelected = selectedCategory == category
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) accentOrangeBrown else cardDark)
                            .clickable { selectedCategory = category }
                            .padding(horizontal = 24.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = category,
                            color = if (isSelected) Color.White else textGrey,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "$selectedCategory Populer", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(text = "See all", fontSize = 12.sp, color = accentOrangeBrown, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // IMPLEMENTASI KETIGA: Transformasi List Promo Menjadi Struktur Card Sushi Premium Vertikal melayang
            val filteredPromos = if (selectedCategory == "Makanan") {
                listOf(
                    PromoItem("Ribeye Steak", "Rp 125.000", "https://images.unsplash.com/photo-1544025162-d76694265947?w=500&q=80", 2L)
                )
            } else {
                listOf(
                    PromoItem("Cappuccino (Hot/Ice)", "Rp 28.000", "https://images.unsplash.com/photo-1534778101976-62847782c213?w=500&q=80", 6L),
                    PromoItem("Fresh Orange Juice", "Rp 22.000", "https://images.unsplash.com/photo-1613478223719-2ab802602423?w=500&q=80", 7L)
                )
            }

            filteredPromos.forEach { promo ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .padding(bottom = 16.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .clickable { navController.navigate("detail/${promo.menuId}") }
                ) {
                    // Gambar Latar Belakang Menu Full
                    AsyncImage(
                        model = promo.imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Overlay Gradasi gelap agar info harga dan judul terbaca sempurna
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Black.copy(alpha = 0.4f), Color.Transparent, Color.Black.copy(alpha = 0.8f))
                                )
                            )
                    )

                    // Label Harga Kiri Atas
                    Text(
                        text = promo.discount, // Berisi data harga nominal string
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.align(Alignment.TopStart).padding(20.dp)
                    )

                    // Badge Ikon Rating Bintang Mengambang Kanan Atas
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.align(Alignment.TopEnd).padding(16.dp).size(36.dp)
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.padding(8.dp))
                    }

                    // Informasi Judul Kiri Bawah melayang di atas glassmorphism tipis
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black.copy(alpha = 0.4f))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Column {
                            Text(text = promo.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(text = "Rekomendasi Chef Koki", color = textGrey, fontSize = 11.sp)
                        }
                    }

                    // Lingkaran Putih Tombol Aksi Panah Kanan Bawah (Memicu perpindahan halaman)
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp).size(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowOutward, // Ikon representasi panah serong kanan atas ($\\nearrow$)
                            contentDescription = null,
                            tint = accentOrangeBrown,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// Perubahan tipe parameter struktur data penampung agar memuat tautan foto dinamis
data class PromoItem(val title: String, val discount: String, val imageUrl: String, val menuId: Long)

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    val context = LocalContext.current
    val dummyPrefs = context.getSharedPreferences("dummy_prefs", android.content.Context.MODE_PRIVATE)

    HomeScreen(
        navController = androidx.navigation.compose.rememberNavController(),
        prefs = dummyPrefs,
        isDarkMode = true,
        onThemeToggle = {}
    )
}