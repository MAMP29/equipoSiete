package com.appmovil.inventorywidget

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.appmovil.inventorywidget.model.Product
import com.appmovil.inventorywidget.repository.ProductRepository
import com.appmovil.inventorywidget.viewmodel.ProductUiState
import com.appmovil.inventorywidget.viewmodel.ProductViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify

@OptIn(ExperimentalCoroutinesApi::class)
class ProductViewModelTest {

    // Regla para ejecutar tareas de LiveData de forma síncrona
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var productRepository: ProductRepository
    private lateinit var productViewModel: ProductViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        productRepository = mock(ProductRepository::class.java)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    //init carga productos y actualiza el estado para que sea exitoso
    @Test
    fun init_loads_products_and_updates_state_to_success() = runTest {
        // Arrange
        val products = listOf(Product("1", 1 , "Test Product", 20000, 1))
        `when`(productRepository.getUserProductList()).thenReturn(products)

        // Act
        productViewModel = ProductViewModel(productRepository)
        advanceUntilIdle() // Ejecuta la corutina en init
        val uiState = productViewModel.productsUiState.value

        // Assert
        assertTrue(uiState is ProductUiState.Success)
        assertEquals(products, (uiState as ProductUiState.Success).products)
        verify(productRepository).getUserProductList()
    }

    //init no puede cargar productos y actualiza el estado a error
    @Test
    fun init_fails_to_load_products_and_updates_state_to_error() = runTest {
        // Arrange
        val errorMessage = "Database error"
        `when`(productRepository.getUserProductList()).thenThrow(RuntimeException(errorMessage))

        // Act
        productViewModel = ProductViewModel(productRepository)
        advanceUntilIdle() // Ejecuta la corutina en init
        val uiState = productViewModel.productsUiState.value

        // Assert
        assertTrue(uiState is ProductUiState.Error)
        assertTrue((uiState as ProductUiState.Error).error.contains(errorMessage))
    }

    //Guarda el repositorio de llamadas de productos y recarga los productos
    @Test
    fun save_product_calls_repository_and_reloads_products() = runTest {
        // Arrange
        val productToSave = Product("4", 2, "New Product", 200, 1)
        `when`(productRepository.getUserProductList()).thenReturn(emptyList()).thenReturn(listOf(productToSave))

        productViewModel = ProductViewModel(productRepository) // Initial load
        advanceUntilIdle() // Espera a que termine la carga inicial

        // Act
        productViewModel.save(productToSave)
        advanceUntilIdle() // Espera a que termine el guardado y la recarga

        // Assert
        verify(productRepository).saveProduct(productToSave)
        val uiState = productViewModel.productsUiState.value
        assertTrue(uiState is ProductUiState.Success)
        assertEquals(listOf(productToSave), (uiState as ProductUiState.Success).products)
    }

    //Eliminar el repositorio de llamadas de productos y recargar productos
    @Test
    fun delete_product_calls_repository_and_reloads_products() = runTest {
        // Arrange
        val productToDelete = Product("4", 2, "New Product", 200, 1)
        `when`(productRepository.getUserProductList()).thenReturn(listOf(productToDelete)).thenReturn(emptyList())

        productViewModel = ProductViewModel(productRepository) // Initial load
        advanceUntilIdle() // Espera a que termine la carga inicial

        // Act
        productViewModel.delete(productToDelete)
        advanceUntilIdle() // Espera a que termine el borrado y la recarga

        // Assert
        verify(productRepository).deleteProduct(productToDelete.id)
        val uiState = productViewModel.productsUiState.value
        assertTrue(uiState is ProductUiState.Success)
        assertTrue((uiState as ProductUiState.Success).products.isEmpty())
    }

    //Actualizar el repositorio de llamadas de productos y recargar productos
    @Test
    fun update_product_calls_repository_and_reloads_products() = runTest {
        // Arrange
        val originalProduct = Product("4", 2, "New Product", 200, 1)
        val updatedProduct = Product("4", 2, "New Product edit", 2000, 2)
        `when`(productRepository.getUserProductList()).thenReturn(listOf(originalProduct)).thenReturn(listOf(updatedProduct))

        productViewModel = ProductViewModel(productRepository) // Initial load
        advanceUntilIdle() // Espera a que termine la carga inicial

        // Act
        productViewModel.update(updatedProduct)
        advanceUntilIdle() // Espera a que termine la actualización y la recarga

        // Assert
        verify(productRepository).updateProduct(updatedProduct)
        val uiState = productViewModel.productsUiState.value
        assertTrue(uiState is ProductUiState.Success)
        assertEquals(listOf(updatedProduct), (uiState as ProductUiState.Success).products)
    }
}