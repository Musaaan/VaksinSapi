package com.kkn.vaksinasisapi.ui.feature

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.kkn.vaksinasisapi.data.model.User
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {
    private val auth by lazy { Firebase.auth }
    private val db by lazy { Firebase.firestore }
    var loading = mutableStateOf(false)
    var userState = mutableStateOf<User?>(null)
    var navigateToRoleSelection = mutableStateOf(false)

    fun signInWithGoogle(credential: AuthCredential) {
        viewModelScope.launch {
            loading.value = true
            try {
                val result = auth.signInWithCredential(credential).await()
                val firebaseUser = result.user

                if (firebaseUser != null) {
                    checkUserRole(firebaseUser.uid, firebaseUser.displayName ?: "", firebaseUser.email ?: "", firebaseUser.photoUrl.toString())
                }
            } catch (e: Exception) {
                loading.value = false
                Log.e("AuthViewModel", "Gagal otentikasi Firebase", e)
            }
        }
    }

    private suspend fun checkUserRole(uid: String, name: String, email: String, photo: String) {
        val doc = db.collection("users").document(uid).get().await()

        if (doc.exists()) {
            userState.value = doc.toObject(User::class.java)
            loading.value = false
        } else {
            // User baru belum punya role
            userState.value = User(uid, name, email, "", photo)
            navigateToRoleSelection.value = true
            loading.value = false
        }
    }

    fun updateUserRole(role: String) {
        val currentUser = userState.value ?: return
        val updatedUser = currentUser.copy(role = role)

        viewModelScope.launch {
            db.collection("users").document(updatedUser.uid).set(updatedUser).await()
            userState.value = updatedUser
            navigateToRoleSelection.value = false
        }
    }
}