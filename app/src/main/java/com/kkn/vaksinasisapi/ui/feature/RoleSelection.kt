package com.kkn.vaksinasisapi.ui.feature

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RoleSelectionScreen(onRoleSelected: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Pilih Peran Anda", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onRoleSelected("peternak") },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Saya Peternak")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onRoleSelected("dokter") },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Saya Dokter Dinas")
        }
    }
}