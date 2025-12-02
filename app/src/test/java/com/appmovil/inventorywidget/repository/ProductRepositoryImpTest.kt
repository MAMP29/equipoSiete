
package com.appmovil.inventorywidget.repository

import com.appmovil.inventorywidget.utils.SessionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@ExperimentalCoroutinesApi
class ProductRepositoryImpTest {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var sessionManager: SessionManager
    private lateinit var productRepository: ProductRepositoryImp

    @Before
    fun setup() {
        firestore = mock(FirebaseFirestore::class.java)
        sessionManager = mock(SessionManager::class.java)
        productRepository = ProductRepositoryImp(firestore, sessionManager)
    }

    @Test
    fun `getUserProductList when user is not authenticated throws exception`() = runTest {
        `when`(sessionManager.currentUser()).thenReturn(null)

        // Act & Assert
        try {
            productRepository.getUserProductList()
            assert(false) // Should not reach here
        } catch (e: Exception) {
            assert(e.message == "No hay un usuario autenticado para realizar esta operación.")
        }
    }
}
