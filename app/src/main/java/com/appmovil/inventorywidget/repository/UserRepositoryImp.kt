package com.appmovil.inventorywidget.repository

import android.util.Log
import com.appmovil.inventorywidget.model.User
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Singleton
class UserRepositoryImp @Inject constructor(
     private val db: FirebaseFirestore // Recordar inicializar en la main FirebaseApp.initializeApp(this)
) : UserRepository {


    override suspend fun createUser(uid: String, user: User): Unit =
        suspendCoroutine { cont ->
            db.collection("users").document(uid)
                .set(user)
                .addOnSuccessListener {
                    Log.d("UserRepo", "Documento guardado exitosamente")
                    cont.resume(Unit)
                }
                .addOnFailureListener { e ->
                    Log.w("UserRepo", "Error al guardar el documento", e)
                    cont.resumeWithException(e)
                }
        }


    override suspend fun updateUser(uid: String, user: User): Unit =
        suspendCoroutine { cont ->
            db.collection("users").document(uid)
                .update(
                    mapOf(
                        "email" to user.email,
                    )
                )
                .addOnSuccessListener {
                    Log.d("UserRepo", "Documento actualizado exitosamente")
                    cont.resume(Unit)
                }
                .addOnFailureListener { e ->
                    Log.w("UserRepo", "Error al actualizar el documento", e)
                    cont.resumeWithException(e)
                }
        }


    override suspend fun getUserById(uid: String): User? = suspendCoroutine { cont ->
        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener { doc ->
                if(doc.exists()) {
                    cont.resume(doc.toObject(User::class.java))
                } else {
                    cont.resume(null)
                }
            }
            .addOnFailureListener { e ->
                Log.w("UserRepo", "Error al guardar el documento", e)
                cont.resume(null)
            }
    }
}