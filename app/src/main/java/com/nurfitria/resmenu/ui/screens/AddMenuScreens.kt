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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.nurfitria.resmenu.model.MenuItem
import com.nurfitria.resmenu.model.MenuRepository
import com.nurfitria.resmenu.utils.CurrencyVisualTransformation
import com.nurfitria.resmenu.utils.ImageHelper
import com.nurfitria.resmenu.utils.formatToRupiah

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMenuScreen(navController: NavHostController, prefs: SharedPreferences) {
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Makanan") }
    var imagePath by remember { mutableStateOf("") }

    val context = LocalContext.current
    val categories = listOf("Makanan", "Minuman")

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
                imagePath = path
            }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = bgBlack, // Latar belakang utama hitam pekat
        topBar = {
            TopAppBar(
                title = { Text("Tambah Menu Baru", fontWeight = FontWeight.Bold, color = Color.White) },
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
                .padding(padding)
                .fillMaxSize()
                .background(bgBlack)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. IMAGE PICKER CARD: Melengkung halus dengan overlay gelap minimalis
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(cardDark)
                    .clickable {
                        launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                contentAlignment = Alignment.Center
            ) {
                if (imagePath.isNotEmpty()) {
                    AsyncImage(
                        model = imagePath,
                        contentDescription = "Menu Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
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
                        Text("Pilih Foto Menu", color = textGrey, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            // Kustomisasi Gaya Warna Kolom Teks Tanpa Garis Tepi
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
                label = { Text("Nama Menu") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                shape = fieldShape,
                singleLine = true,
                isError = name.isBlank() && price.isNotBlank()
            )

            TextField(
                value = price,
                onValueChange = { if (it.all { char -> char.isDigit() }) price = it },
                label = { Text("Harga Menu") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                shape = fieldShape,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = CurrencyVisualTransformation(),
                isError = price.isBlank() && name.isNotBlank()
            )

            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Deskripsi Menu") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                shape = fieldShape,
                minLines = 4,
                maxLines = 6
            )

            // 3. SELEKSI KATEGORI: Model Kapsul Toggle Minimalis
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Kategori",
                    color = textGrey,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 10.dp, start = 4.dp)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    categories.forEach { cat ->
                        val isSelected = category.equals(cat, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) accentOrangeBrown else cardDark)
                                .clickable { category = cat }
                                .padding(horizontal = 24.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = cat,
                                color = if (isSelected) Color.White else textGrey,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. TOMBOL SIMPAN KAPSUL PREMIUM
            Button(
                onClick = {
                    if (name.isNotBlank() && price.isNotBlank()) {
                        val newItem = MenuItem(
                            id = System.currentTimeMillis(),
                            name = name,
                            price = formatToRupiah(price),
                            description = description,
                            // Fallback link resolusi tinggi estetik jika user mengosongkan gambar picker
                            imageUrl = if (imagePath.isBlank()) "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=500&q=80" else imagePath,
                            category = category
                        )
                        MenuRepository.addMenuItem(prefs, newItem)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = name.isNotBlank() && price.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1A1A1A), // Kapsul Hitam Elegan konsisten dengan Add to Cart
                    disabledContainerColor = cardDark.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier.align(Alignment.Center),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Simpan Menu Baru", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

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

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, showSystemUi = true)
@Composable
fun AddMenuScreenPreview() {
    val context = LocalContext.current
    val dummyPrefs = context.getSharedPreferences("dummy_prefs", android.content.Context.MODE_PRIVATE)

    AddMenuScreen(
        navController = androidx.navigation.compose.rememberNavController(),
        prefs = dummyPrefs
    )
}