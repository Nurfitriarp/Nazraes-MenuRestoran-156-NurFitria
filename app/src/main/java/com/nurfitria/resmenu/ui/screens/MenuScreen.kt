package com.nurfitria.resmenu.ui.screens

import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.nurfitria.resmenu.model.MenuRepository
import com.nurfitria.resmenu.model.MenuItem
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(navController: NavHostController, prefs: SharedPreferences) {
    var isLoading by remember { mutableStateOf(true) }
    var selectedCategory by remember { mutableStateOf("Semua") }
    var searchQuery by remember { mutableStateOf("") }
    val categories = listOf("Semua", "Makanan", "Minuman")
    val context = LocalContext.current

    val menuList = remember { mutableStateListOf<MenuItem>() }

    // THEME DESIGN SYSTEM (Sama seperti Home & Detail)
    val bgBlack = Color(0xFF0D0D0D)
    val cardDark = Color(0xFF1A1A1A)
    val accentOrangeBrown = Color(0xFFE65100)
    val textGrey = Color(0xFF9E9E9E)

    LaunchedEffect(Unit) {
        menuList.clear()
        menuList.addAll(MenuRepository.getMenu(prefs))
        delay(1000)
        isLoading = false
    }

    val filteredMenu = menuList.filter { item ->
        val matchesCategory = if (selectedCategory == "Semua") true else item.category.equals(selectedCategory, ignoreCase = true)
        val matchesSearch = item.name.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = bgBlack, // Latar belakang utama hitam pekat
        topBar = {
            TopAppBar(
                title = { Text("Menu Restoran", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Opsi Pencarian tambahan / Filter */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bgBlack)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_menu") },
                containerColor = accentOrangeBrown, // Diubah ke Oranye-Cokelat agar selaras
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Menu")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(bgBlack)
        ) {

            // KATEGORI HORIZONTAL (Gaya Kapsul Minimalis Sesuai Gambar 2)
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(categories) { category ->
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

            if (isLoading) {
                // Shimmer Loading Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(4) { ShimmerGridItem() }
                }
            } else if (filteredMenu.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.RestaurantMenu, contentDescription = null, modifier = Modifier.size(80.dp), tint = cardDark)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Menu tidak ditemukan", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
                }
            } else {
                // IMPLEMENTASI UTAMA: LAYOUT GRID BERPASANGAN 2 KOLOM (GABUNGAN GAMBAR 1 & 2)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredMenu, key = { it.id }) { item ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(28.dp))
                                .background(cardDark)
                                .clickable { navController.navigate("detail/${item.id}") }
                                .padding(14.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Foto Hidangan Berbentuk Lingkaran Besar Menawan di Tengah Atas
                                AsyncImage(
                                    model = item.imageUrl,
                                    contentDescription = item.name,
                                    modifier = Modifier
                                        .size(110.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                // Nama Hidangan Rata Kiri
                                Text(
                                    text = item.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Start
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                // Deskripsi Singkat di bawah judul
                                Text(
                                    text = item.description,
                                    fontSize = 11.sp,
                                    color = textGrey,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Start
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Rating Bintang Kecil Statis & Label Harga (Kiri Bawah)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("4.8", color = textGrey, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                    }

                                    Text(
                                        text = item.price.replace("Rp ", "Rp"), // Menghapus spasi harga agar pas
                                        color = accentOrangeBrown,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            // Badge Bulat kecil Transparan untuk Tombol Bookmark / Suka di Sudut Atas Card
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.07f),
                                modifier = Modifier.align(Alignment.TopEnd).size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FavoriteBorder,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.6f),
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShimmerGridItem() {
    val shimmerColors = listOf(
        Color.White.copy(alpha = 0.1f),
        Color.White.copy(alpha = 0.03f),
        Color.White.copy(alpha = 0.1f),
    )
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f, targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ), label = "shimmer"
    )
    val brush = Brush.linearGradient(
        colors = shimmerColors, start = Offset.Zero, end = Offset(translateAnim, translateAnim)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xFF1A1A1A))
            .padding(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.size(100.dp).clip(CircleShape).background(brush))
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.fillMaxWidth(0.8f).height(16.dp).background(brush))
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.fillMaxWidth(0.5f).height(12.dp).background(brush))
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, showSystemUi = true)
@Composable
fun MenuScreenPreview() {
    val context = LocalContext.current
    val dummyPrefs = context.getSharedPreferences("dummy_prefs", android.content.Context.MODE_PRIVATE)

    MenuScreen(
        navController = androidx.navigation.compose.rememberNavController(),
        prefs = dummyPrefs
    )
}