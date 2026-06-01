package com.nurfitria.resmenu.ui.screens

import android.content.Intent
import android.content.SharedPreferences
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.nurfitria.resmenu.model.MenuRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailMenuScreen(
    navController: NavHostController,
    menuId: Long,
    prefs: SharedPreferences
) {
    val context = LocalContext.current
    val menuList = remember { MenuRepository.getMenu(prefs) }
    val item = menuList.find { it.id == menuId } ?: return

    var quantity by remember { mutableIntStateOf(1) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val favorites = remember {
        mutableStateOf(
            prefs.getStringSet("favorite_menus", emptySet()) ?: emptySet()
        )
    }

    val isFavorite = favorites.value.contains(menuId.toString())
    var isHeartClicked by remember { mutableStateOf(false) }

    val heartScale by animateFloatAsState(
        targetValue = if (isHeartClicked) 1.5f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        finishedListener = { isHeartClicked = false },
        label = "heartScale"
    )

    val scrollState = rememberLazyListState()

    // PERBAIKAN 1: Menaikkan tinggi area header dari 300dp ke 340dp untuk memberi ruang kosong di atas gambar
    val headerHeight = 340.dp

    val firstItemOffset = remember { derivedStateOf { scrollState.firstVisibleItemScrollOffset } }
    val firstItemIndex = remember { derivedStateOf { scrollState.firstVisibleItemIndex } }

    val headerAlpha = remember {
        derivedStateOf {
            if (firstItemIndex.value > 0) 0f
            else (1f - (firstItemOffset.value.toFloat() / 500f)).coerceIn(0f, 1f)
        }
    }

    val headerTranslation = remember {
        derivedStateOf {
            if (firstItemIndex.value > 0) 0f
            else firstItemOffset.value.toFloat() * 0.5f
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Menu") },
            text = { Text("Apakah Anda yakin ingin menghapus ${item.name}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        MenuRepository.deleteMenuItem(prefs, menuId)
                        showDeleteDialog = false
                        navController.popBackStack()
                    }
                ) {
                    Text("Ya, Hapus", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = scrollState,
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Spacer(modifier = Modifier.height(headerHeight))
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer { translationY = -32.dp.toPx() }
                        .background(
                            MaterialTheme.colorScheme.surface,
                            RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                        )
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 44.dp)
                                .align(Alignment.Center)
                        )

                        IconButton(
                            onClick = {
                                isHeartClicked = true
                                val current = prefs.getStringSet("favorite_menus", emptySet())?.toMutableSet() ?: mutableSetOf()
                                if (isFavorite) current.remove(menuId.toString()) else current.add(menuId.toString())
                                prefs.edit().putStringSet("favorite_menus", current).apply()
                                favorites.value = current
                            },
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                modifier = Modifier.scale(heartScale),
                                tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = item.price,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.outline,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // PERBAIKAN 2: Menambahkan indikator rating minimalis (bintang + angka) satu baris di bawah harga, seperti di gambar referensi es cream
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "4.8",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // QUANTITY SELECTOR
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        IconButton(onClick = { if (quantity > 1) quantity-- }) {
                            Text("-", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                        Text(
                            text = quantity.toString(),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(horizontal = 12.dp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        IconButton(onClick = { quantity++ }) {
                            Text("+", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }

                    // CATATAN: BLOK 5 BINTANG INTERAKTIF DAN TEKS "BERIKAN RATING" SUDAH DIHAPUS TOTAL DI SINI UNTUK ESTETIKA MINIMALIS

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(24.dp))

                    // DESKRIPSI MENU YANG SUDAH RINGKAS & RATA TENGAH
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    // TOMBOL ADD TO CART KAPSUL
                    Button(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1A1A1A)
                        ),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Text(
                                text = "Add to Cart",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                modifier = Modifier.align(Alignment.Center)
                            )
                            Surface(
                                shape = CircleShape,
                                color = Color.White,
                                modifier = Modifier
                                    .size(36.dp)
                                    .align(Alignment.CenterEnd)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.padding(6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }

        // HEADER IMAGE (DIBERI PADDING TOP AGAR TURUN DAN TIDAK MEPET DENGAN ICON TOPBAR)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(headerHeight)
                .statusBarsPadding()
                .graphicsLayer {
                    translationY = -headerTranslation.value
                    alpha = headerAlpha.value
                },
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 40.dp) // PERBAIKAN 3: Memberi padding atas sebesar 40dp agar gambar bulat turun ke bawah ikon navigasi
                    .size(220.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        TopAppBar(
            modifier = Modifier.zIndex(1f),
            title = {
                if (headerAlpha.value < 0.5f) {
                    Text(item.name, fontWeight = FontWeight.Bold)
                }
            },
            navigationIcon = {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (headerAlpha.value > 0.5f) Color.Black.copy(alpha = 0.3f) else Color.Transparent,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = if (headerAlpha.value > 0.5f) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            },
            actions = {
                val actionIconTint = if (headerAlpha.value > 0.5f) Color.White else MaterialTheme.colorScheme.onSurface
                val actionBgColor = if (headerAlpha.value > 0.5f) Color.Black.copy(alpha = 0.3f) else Color.Transparent

                Surface(shape = RoundedCornerShape(12.dp), color = actionBgColor) {
                    IconButton(onClick = { navController.navigate("edit_menu/${item.id}") }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Menu", tint = actionIconTint)
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                Surface(shape = RoundedCornerShape(12.dp), color = actionBgColor) {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Hapus Menu", tint = if (headerAlpha.value > 0.5f) Color.White else Color.Red)
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                Surface(shape = RoundedCornerShape(12.dp), color = actionBgColor) {
                    IconButton(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "Cobain deh ${item.name} di Restoran kami, harganya cuma ${item.price}!")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Bagikan Menu"))
                        }
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Bagikan", tint = actionIconTint)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = if (headerAlpha.value < 0.5f) {
                    MaterialTheme.colorScheme.surface.copy(alpha = (1f - headerAlpha.value * 2).coerceIn(0f, 1f))
                } else Color.Transparent
            )
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, showSystemUi = true)
@Composable
fun DetailMenuScreenPreview() {
    val context = LocalContext.current
    val dummyPrefs = context.getSharedPreferences("dummy_prefs", android.content.Context.MODE_PRIVATE)

    DetailMenuScreen(
        navController = androidx.navigation.compose.rememberNavController(),
        menuId = 1L,
        prefs = dummyPrefs
    )
}