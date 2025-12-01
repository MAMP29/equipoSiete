package com.appmovil.inventorywidget.repository

import android.util.Log
import com.appmovil.inventorywidget.model.AuthResult
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
                    .addOnSuccessListener { authResult ->
                        val userId = authResult.user?.uid
                        Log.i(tag, "Registro exitoso")

                        if (userId != null) {
                            continuation.resume(AuthResult(isSuccess = true, userId = userId))
                        } else {
                            continuation.resume(AuthResult(isSuccess = false, message = "No se pudo obtener el UID del usuario."))
                        }
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
                isSuccess = true,
                message = e.toString()
            )
        }
    }

    override suspend fun login(email: String, password: String): AuthResult {
        try {
            val result = suspendCoroutine { continuation ->
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener { authResult ->
                        val userId = authResult.user?.uid
                        Log.i(tag, "Inicio de sesión exitoso")
                        if (userId != null) {
                            continuation.resume(AuthResult(isSuccess = true, userId = userId))
                        } else {
                            continuation.resume(AuthResult(isSuccess = false, message = "No se pudo obtener el UID del usuario."))
                        }
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


    override fun logout() {
        firebaseAuth.signOut()
    }
}