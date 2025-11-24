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
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
){
    private val tag = "AuthRepository: "

    fun isLoggedIn(): Boolean {
        if (firebaseAuth.currentUser != null) {
            Log.d(tag,"El usuario ya ha iniciado sesión")
            return true
        }
        return false
    }

    suspend fun register(email: String, password: String): AuthResult {
        try {
            val result = suspendCoroutine { continuation ->
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        Log.i(tag, "Registro exitoso")

                        // TODO: Guardar el usuario en el repositorio, viewmodel imp
                        // TODO: Encargar login al viewModel y el guardado
                        continuation.resume(AuthResult(
                            isSuccess = true
                        ))
                        /*CoroutineScope(Dispatchers.IO).launch {
                            continuation.resume(login(email, password))
                        }*/
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

    suspend fun login(email: String, password: String): AuthResult {
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

    fun logout() {
        firebaseAuth.signOut()
    }
}