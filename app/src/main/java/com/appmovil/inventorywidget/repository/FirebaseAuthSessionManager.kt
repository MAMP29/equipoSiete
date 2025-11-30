package com.appmovil.inventorywidget.repository

import android.util.Log
import com.appmovil.inventorywidget.model.User
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class FirebaseAuthSessionManager @Inject constructor(
    private val firebaseAuth: FirebaseAuth
): SessionManager {
    override fun currentUser(): User? {
        return firebaseAuth.currentUser?.let { User(id = it.uid) }
    }

    override fun isLoggedIn(): Boolean {
        if (firebaseAuth.currentUser != null) {
            Log.d("sessionManager: ","El usuario ya ha iniciado sesión")
            return true
        }
        return false
    }
}