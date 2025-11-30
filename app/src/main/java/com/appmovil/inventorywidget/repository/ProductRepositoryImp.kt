package com.appmovil.inventorywidget.repository

import android.util.Log
import com.appmovil.inventorywidget.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Singleton
class ProductRepositoryImp @Inject constructor(
    private val db: FirebaseFirestore
) : ProductRepository {

    override suspend fun createProduct(uid: String, product: Product): Unit =
        suspendCoroutine { cont ->
            db.collection("users").document(uid)
                .collection("products").document(product.id)
                .set(product)
                .addOnSuccessListener {
                    Log.d("UserRepo", "Documento guardado exitosamente")
                    cont.resume(Unit)
                }
                .addOnFailureListener { e ->
                    Log.w("UserRepo", "Error al guardar el documento", e)
                    cont.resumeWithException(e)
                }
        }


    override suspend fun deleteProduct(uid: String, productId: String): Unit =
        suspendCoroutine { cont ->
            db.collection("users").document(uid)
                .collection("products").document(productId)
                .delete()
                .addOnSuccessListener {
                    Log.d("UserRepo", "Documento eliminado exitosamente")
                    cont.resume(Unit)
                }
                .addOnFailureListener { e ->
                    Log.w("UserRepo", "Error al eliminar el documento", e)
                    cont.resumeWithException(e)
                }
        }


    override suspend fun updateProduct(uid: String, product: Product): Unit =
        suspendCoroutine { cont ->
            db.collection("users")
                .document(uid)
                .collection("products")
                .document(product.id)
                .update(
                    mapOf(
                        "name" to product.name,
                        "price" to product.price,
                        "quantity" to product.quantity
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


    override suspend fun getProductById(uid: String, productId: String): Product? = suspendCoroutine { cont ->
        db.collection("users").document(uid)
            .collection("products").document(productId)
            .get()
            .addOnSuccessListener { doc ->
                if(doc.exists()) {
                    cont.resume(doc.toObject(Product::class.java))
                } else {
                    cont.resume(null)
                }
            }
            .addOnFailureListener { e ->
                Log.w("UserRepo", "Error al guardar el documento", e)
                cont.resume(null)
            }
    }

    override suspend fun getUserProductList(uid: String): List<Product>? = suspendCoroutine { cont ->
        db.collection("users").document(uid)
            .collection("products")
            .get()
            .addOnSuccessListener { doc ->
                cont.resume(doc.toObjects(Product::class.java))
            }
            .addOnFailureListener { e ->
                Log.w("UserRepo", "Error al guardar el documento", e)
                cont.resume(emptyList())
            }
    }
}