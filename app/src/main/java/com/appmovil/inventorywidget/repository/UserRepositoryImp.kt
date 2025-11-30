package com.appmovil.inventorywidget.repository

import android.util.Log
import com.appmovil.inventorywidget.model.User
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class UserRepositoryImp @Inject constructor(
     private val db: FirebaseFirestore // Recordar inicializar en la main FirebaseApp.initializeApp(this)
) : UserRepository {


    override suspend fun createUser(uid: String, email: String) {
        val user = User(email = email)
        db.collection("users").document(uid)
            .set(user)
            .addOnSuccessListener { Log.d("UserRepo", "Documento guardado exitosamente") }
            .addOnFailureListener { e -> Log.w("UserRepo", "Error al guardar el documento", e) }
    }

    override suspend fun updateUser(uid: String, email: String) {
        val user = db.collection("users").document(uid)
        user.update("email", email)
            .addOnSuccessListener { Log.d("UserRepo", "Documento actualizado exitosamente") }
            .addOnFailureListener { e -> Log.w("UserRepo", "Error al actualizar el documento", e) }
    }

    override suspend fun getUserById(uid: String): User? = suspendCoroutine { cont ->
        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener { doc ->
                if(doc.exists()) {
                    cont.resume(
                        User(
                            id = doc.getString("id").toString(),
                            email = doc.getString("email").toString()
                        )
                    )
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