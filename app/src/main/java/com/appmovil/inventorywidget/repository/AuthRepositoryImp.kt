package com.appmovil.inventorywidget.repository

import android.util.Log
import com.appmovil.inventorywidget.model.AuthResult
import com.appmovil.inventorywidget.model.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CancellationException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class AuthRepositoryImp @Inject constructor(
    private val firebaseAuth: FirebaseAuth
): AuthRepository {
    private val tag = "AuthRepository: "

    override fun isLoggedIn(): Boolean {
        if (firebaseAuth.currentUser != null) {
            Log.d(tag,"El usuario ya ha iniciado sesión")
            return true
        }
        return false
    }

    override suspend fun register(email: String, password: String): AuthResult {
        try {
            val result = suspendCoroutine { continuation ->
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        Log.i(tag, "Registro exitoso")

                        continuation.resume(
                            AuthResult(
                                isSuccess = true
                            )
                        )
                    }

                    .addOnFailureListener {
                        Log.i(tag, "Fallo en el registro ${it.message}")
                        continuation.resume(
                            AuthResult(
                                isSuccess = false,
                                message = it.message
                            )
                        )
                    }
            }
            return result

        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            Log.e(tag, "Excepcion en el registro ${e.message}")
            return AuthResult(
                isSuccess = false,   // 👈 FIX IMPORTANTE
                message = e.toString()
            )
        }
    }


    override suspend fun login(email: String, password: String): AuthResult {
        try {
            val result = suspendCoroutine { continuation ->
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        Log.i(tag, "Inicio de sesión exitoso")
                        continuation.resume(AuthResult(
                            isSuccess = true
                        ))
                    }

                    .addOnFailureListener {
                        Log.i(tag, "Fallo en el inicio de sesión ${it.message}")
                        continuation.resume(
                            AuthResult(
                                isSuccess = false,
                                message = it.message
                            )
                        )
                    }

            }
            return result

        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            Log.e(tag, "Excepcion en el registro ${e.message}")
            return AuthResult(
                isSuccess = false,
                message = e.toString()
            )
        }
    }

    override fun currentUser(): User? {
        return firebaseAuth.currentUser?.let { User(id = it.uid) }
    }

    override fun logout() {
        firebaseAuth.signOut()
    }
}