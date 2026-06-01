package com.nurfitria.resmenu.ui.screens

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavHostController, prefs: SharedPreferences, updated: Boolean = false) {
    val context = LocalContext.current
    val name = prefs.getString("name", "Nazraes") ?: "Nazraes"
    val email = prefs.getString("email", "nazraes@gmail.com") ?: "nazraes@gmail.com"
    val address = prefs.getString("address", "Jl. Mawar, Malang") ?: "Jl. Mawar, Malang"
    val description = prefs.getString("description", "Cocok untuk berkumpul keluarga, tempat nongkrong, atau restoran dengan menu nusantara yang menghadirkan suasana hangat seperti rumah sendiri") ?: "Cocok untuk berkumpul keluarga, tempat nongkrong, atau restoran dengan menu nusantara yang menghadirkan suasana hangat seperti rumah sendiri"
    val hours = prefs.getString("hours", "08:00 - 21:00") ?: "08:00 - 21:00"

    val snackbarHostState = remember { SnackbarHostState() }

    // COLOR PALETTE ULTRA DARK (Sesuai dengan Home & Menu)
    val bgBlack = Color(0xFF0D0D0D)
    val cardDark = Color(0xFF1A1A1A)
    val accentOrangeBrown = Color(0xFFE65100)
    val textGrey = Color(0xFF9E9E9E)

    LaunchedEffect(updated) {
        if (updated) {
            snackbarHostState.showSnackbar("Profil berhasil diperbarui!")
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = bgBlack, // Mengubah dasar warna latar belakang menjadi hitam pekat
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Profil", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bgBlack)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(bgBlack)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. SECTION AVATAR HEADER TERPUSAT (Sesuai Target Gambar)
            Surface(
                shape = CircleShape,
                color = accentOrangeBrown.copy(alpha = 0.2f),
                modifier = Modifier.size(96.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Storefront,
                    contentDescription = null,
                    modifier = Modifier.padding(24.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = email,
                fontSize = 14.sp,
                color = textGrey,
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tombol Edit Profile berbentuk kapsul tipis di bawah nama-email
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(cardDark)
                    .clickable { navController.navigate("edit_profile") }
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Edit Profile", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 2. KELOMPOK PERTAMA: GENERAL SECTION
            Text(
                text = "General",
                color = textGrey,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                textAlign = TextAlign.Start
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(cardDark)
            ) {
                // Item Alamat / Lokasi (Klik buka Google Maps)
                ProfileItemRow(
                    icon = Icons.Default.LocationOn,
                    title = "Location",
                    subtitle = address,
                    onClick = {
                        val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(address)}")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                            setPackage("com.google.android.apps.maps")
                        }
                        context.startActivity(mapIntent)
                    }
                )

                HorizontalDivider(color = bgBlack, thickness = 2.dp)

                // Item Jam Operasional
                ProfileItemRow(
                    icon = Icons.Default.Schedule,
                    title = "Operational Hours",
                    subtitle = hours,
                    onClick = {}
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. KELOMPOK KEDUA: OTHER SECTION
            Text(
                text = "Other",
                color = textGrey,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                textAlign = TextAlign.Start
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(cardDark)
            ) {
                // Item Deskripsi Aplikasi Restoran
                ProfileItemRow(
                    icon = Icons.Default.Info,
                    title = "Restaurant Description",
                    subtitle = description,
                    onClick = {}
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// Komponen Baris Item Menu Profil Adaptif Baru
@Composable
fun ProfileItemRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon Sisi Kiri
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White.copy(alpha = 0.05f),
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.padding(10.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Label Keterangan Tengah
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Text(
                text = subtitle,
                color = Color(0xFF9E9E9E),
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        // Panah Indikator Kanan (Sesuai Target Gambar)
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color(0xFF616161),
            modifier = Modifier.size(20.dp)
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    val context = LocalContext.current
    val dummyPrefs = context.getSharedPreferences("dummy_prefs", android.content.Context.MODE_PRIVATE)

    ProfileScreen(
        navController = androidx.navigation.compose.rememberNavController(),
        prefs = dummyPrefs
    )
}