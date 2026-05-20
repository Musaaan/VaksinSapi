package com.kkn.vaksinasisapi.ui.features.peternak

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TambahSapiScreen(
    onNavigateBack: () -> Unit,
    onSaveClick: (nama: String, jenis: String, umur: Int, riwayat: String, tingkatStress: Int, imageUri: Uri?) -> Unit
) {
    // State untuk form input
    var namaSapi by remember { mutableStateOf("") }
    var jenisSapi by remember { mutableStateOf("") }
    var umurBulan by remember { mutableStateOf("") }
    var riwayatPenyakit by remember { mutableStateOf("") }

    // State untuk Tingkat Stres (Skala 1-10)
    var tingkatStress by remember { mutableFloatStateOf(1f) }

    // State untuk Foto Sapi
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Data Sapi Baru") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // --- BAGIAN UPLOAD FOTO ---
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    // Jika ada gambar, tampilkan preview menggunakan Coil
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Preview Foto Sapi",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Jika kosong, tampilkan ikon Add Photo
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.AddAPhoto,
                            contentDescription = "Upload Foto",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Pilih Foto Sapi", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- BAGIAN FORM ISIAN ---
            OutlinedTextField(
                value = namaSapi,
                onValueChange = { namaSapi = it },
                label = { Text("Nama/Kode Sapi") },
                placeholder = { Text("Contoh: PO-001 atau Sapi Budi") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = jenisSapi,
                onValueChange = { jenisSapi = it },
                label = { Text("Jenis Sapi") },
                placeholder = { Text("Contoh: Limousin, Brahman, dll") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = umurBulan,
                onValueChange = {
                    // Hanya izinkan angka
                    if (it.isEmpty() || it.all { char -> char.isDigit() }) umurBulan = it
                },
                label = { Text("Umur (Bulan)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = riwayatPenyakit,
                onValueChange = { riwayatPenyakit = it },
                label = { Text("Riwayat Penyakit (Bila Ada)") },
                modifier = Modifier.fillMaxWidth().height(100.dp), // Dibuat lebih tinggi agar bisa banyak teks
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- BAGIAN TINGKAT STRES (SLIDER) ---
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Tingkat Stres Awal: ${tingkatStress.toInt()}",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "1 = Sangat Santai, 10 = Sangat Stres",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Slider(
                        value = tingkatStress,
                        onValueChange = { tingkatStress = it },
                        valueRange = 1f..10f,
                        steps = 8 // 10 range - 2 ujung = 8 titik perhentian
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- TOMBOL SIMPAN ---
            Button(
                onClick = {
                    val umur = umurBulan.toIntOrNull() ?: 0
                    onSaveClick(namaSapi, jenisSapi, umur, riwayatPenyakit, tingkatStress.toInt(), imageUri)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = namaSapi.isNotBlank() && jenisSapi.isNotBlank() && umurBulan.isNotBlank()
            ) {
                Text("Simpan Data Sapi", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}