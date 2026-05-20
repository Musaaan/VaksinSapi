package com.kkn.vaksinasisapi.navigation

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.kkn.vaksinasisapi.ui.feature.AuthViewModel
import com.kkn.vaksinasisapi.ui.peternak.DashboardPeternakScreen
import com.kkn.vaksinasisapi.ui.feature.LoginScreen
import com.kkn.vaksinasisapi.ui.feature.RoleSelectionScreen
import com.kkn.vaksinasisapi.ui.features.peternak.TambahSapiScreen
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val webClientId = "213446750384-c7jd2ig13hti6556ejcpf4qpibpk1t7v.apps.googleusercontent.com"

    val userState by authViewModel.userState
    val showRoleSelection by authViewModel.navigateToRoleSelection
    val isLoading by authViewModel.loading

    // Logika Otomatisasi Navigasi setelah Login
    LaunchedEffect(userState, showRoleSelection) {
        if (showRoleSelection) {
            navController.navigate(NavRoutes.RoleSelection.route) {
                popUpTo(NavRoutes.Login.route) { inclusive = true }
            }
        } else if (userState != null) {
            val role = userState?.role
            if (role == "peternak") {
                navController.navigate(NavRoutes.DashboardPeternak.route) {
                    popUpTo(NavRoutes.Login.route) { inclusive = true }
                    popUpTo(NavRoutes.RoleSelection.route) { inclusive = true }
                }
            } else if (role == "dokter") {
                navController.navigate(NavRoutes.DashboardDokter.route) {
                    popUpTo(NavRoutes.Login.route) { inclusive = true }
                    popUpTo(NavRoutes.RoleSelection.route) { inclusive = true }
                }
            }
        }
    }

    // Indikator Loading Global
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }

    NavHost(
        navController = navController,
        startDestination = NavRoutes.Login.route,
        modifier = modifier
    ) {

        composable(route = NavRoutes.Login.route) {
            LoginScreen(
                onSignInClick = {
                    coroutineScope.launch {
                        try {
                            val credentialManager = CredentialManager.create(context)
                            val googleIdOption = GetGoogleIdOption.Builder()
                                .setFilterByAuthorizedAccounts(false)
                                .setServerClientId(webClientId)
                                .build()
                            val request = GetCredentialRequest.Builder()
                                .addCredentialOption(googleIdOption)
                                .build()

                            val result = credentialManager.getCredential(context, request)
                            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
                            val authCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)

                            authViewModel.signInWithGoogle(authCredential)
                        } catch (e: Exception) {
                            Log.e("Navigation", "Login failed", e)
                            Toast.makeText(context, "Login dibatalkan/gagal", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
        }

        composable(route = NavRoutes.RoleSelection.route) {
            RoleSelectionScreen(
                onRoleSelected = { selectedRole ->
                    authViewModel.updateUserRole(selectedRole)
                }
            )
        }

        composable(route = NavRoutes.DashboardPeternak.route) {
            DashboardPeternakScreen(
                userData = userState,
                onAddSapiClick = {
                    navController.navigate(NavRoutes.TambahSapi.route)
                }
            )
        }

        composable(route = NavRoutes.DashboardDokter.route) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Halaman Dashboard Dokter Dinas")
            }
        }

        composable(route = NavRoutes.TambahSapi.route) {
            TambahSapiScreen(
                onNavigateBack = { navController.popBackStack() },
                onSaveClick = { nama, jenis, umur, riwayat, tingkatStress, imageUri ->
                    // TODO: Kirim data ini ke ViewModel untuk disimpan ke Firestore & Firebase Storage
                    Toast.makeText(context, "Menyimpan data $nama...", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
            )
        }
    }
}