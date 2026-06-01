package com.nurfitria.resmenu.ui.screens

import android.content.SharedPreferences
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.nurfitria.resmenu.utils.ImageHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavHostController, prefs: SharedPreferences) {
    var name by remember { mutableStateOf(prefs.getString("name", "Nazraes") ?: "") }
    var email by remember { mutableStateOf(prefs.getString("email", "Nazraes@gmail.com") ?: "") }
    var address by remember { mutableStateOf(prefs.getString("address", "Jl. Malang, Malang") ?: "") }
    var description by remember { mutableStateOf(prefs.getString("description", "Coba Edit") ?: "") }
    var hours by remember { mutableStateOf(prefs.getString("hours", "08:00 - 21:00") ?: "") }
    var bannerUrl by remember { mutableStateOf(prefs.getString("banner_url", "") ?: "") }

    val context = LocalContext.current

    // THEME DESIGN SYSTEM (Konsisten Ultra Dark Premium)
    val bgBlack = Color(0xFF0D0D0D)
    val cardDark = Color(0xFF1A1A1A)
    val accentOrangeBrown = Color(0xFFE65100)
    val textGrey = Color(0xFF9E9E9E)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            val path = ImageHelper.copyUriToInternalStorage(context, it)
            if (path != null) {
                bannerUrl = path
            }
        }
    }

    fun saveData() {
        prefs.edit().apply {
            putString("name", name)
            putString("email", email)
            putString("address", address)
            putString("description", description)
            putString("hours", hours)
            putString("banner_url", bannerUrl)
            apply()
        }
        navController.navigate("profile?updated=true") {
            popUpTo("profile") { inclusive = true }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = bgBlack, // Mengubah latar belakang Scaffold menjadi hitam pekat
        topBar = {
            TopAppBar(
                title = { Text("Edit Profil", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { if (name.isNotBlank()) saveData() }) {
                        Icon(Icons.Default.Save, contentDescription = "Save", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bgBlack)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(bgBlack)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. BANNER PICKER CARD: Melengkung halus dengan overlay gelap minimalis
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(cardDark)
                    .clickable {
                        launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                contentAlignment = Alignment.Center
            ) {
                if (bannerUrl.isNotEmpty()) {
                    AsyncImage(
                        model = bannerUrl,
                        contentDescription = "Banner Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Lapisan transparan tipis penanda aksi edit foto banner
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f))
                    )
                    Surface(
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.5f),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = Color.White, modifier = Modifier.padding(12.dp))
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.AddAPhoto, contentDescription = null, modifier = Modifier.size(40.dp), tint = textGrey)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Pilih Banner Restoran", color = textGrey, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            // Kustomisasi Gaya Warna Kolom Teks Minimalis Tanpa Border Luar
            val textFieldColors = TextFieldDefaults.colors(
                focusedContainerColor = cardDark,
                unfocusedContainerColor = cardDark,
                disabledContainerColor = cardDark,
                errorContainerColor = cardDark,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Red.copy(alpha = 0.5f),
                focusedLabelColor = accentOrangeBrown,
                unfocusedLabelColor = textGrey,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
            val fieldShape = RoundedCornerShape(16.dp)

            // 2. INPUT TEXT FIELDS PREMIUM CARD STYLE
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nama Restoran") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                shape = fieldShape,
                singleLine = true,
                isError = name.isBlank()
            )

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                shape = fieldShape,
                singleLine = true
            )

            TextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Alamat") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                shape = fieldShape,
                minLines = 2,
                maxLines = 3
            )

            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Deskripsi") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                shape = fieldShape,
                minLines = 4,
                maxLines = 6
            )

            TextField(
                value = hours,
                onValueChange = { hours = it },
                label = { Text("Jam Buka") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                shape = fieldShape,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 3. TOMBOL SIMPAN KAPSUL PREMIUM (Sama seperti Halaman Edit Menu)
            Button(
                onClick = { if (name.isNotBlank()) saveData() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1A1A1A), // Warna dasar hitam pekat kontras
                    disabledContainerColor = cardDark.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(28.dp) // Bentuk kapsul melengkung sempurna
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier.align(Alignment.Center),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Simpan Perubahan", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    // Bulatan putih tempat ikon aksi simpan di sudut kanan kapsul
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        modifier = Modifier
                            .size(36.dp)
                            .align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}