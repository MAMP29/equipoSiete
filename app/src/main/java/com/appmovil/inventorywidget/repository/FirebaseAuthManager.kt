package com.appmovil.inventorywidget.repository

import com.appmovil.inventorywidget.model.User
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class FirebaseAuthManager @Inject constructor(
    private val firebaseAuth: FirebaseAuth
): SessionManager {
    override fun currentUser(): User? {
        return firebaseAuth.currentUser?.let { User(id = it.uid) }
    }
}