package com.appmovil.inventorywidget.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.appmovil.inventorywidget.model.Product
import com.appmovil.inventorywidget.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    // Fuente del estado de la UI
    private val _productsUiState = MutableLiveData<ProductUiState>(ProductUiState.Loading)
    val productsUiState: LiveData<ProductUiState> = _productsUiState

    init {
        loadProducts()
    }

    fun loadProducts() = viewModelScope.launch {
        _productsUiState.value = ProductUiState.Loading
        try {
            val products = productRepository.getUserProductList()
            _productsUiState.value = ProductUiState.Success(products)
        } catch (e: Exception) {
            _productsUiState.value = ProductUiState.Error("No se pudieron cargar los productos: ${e.message}")
        }
    }

    fun getProductById(id: Int): LiveData<Product?> = liveData {
        val product = productRepository.getProductById(id.toString())
        emit(product)
    }

    fun save(product: Product) = viewModelScope.launch {
        try {
            productRepository.saveProduct(product)
            loadProducts()
        } catch (e: Exception) {
            _productsUiState.value = ProductUiState.Error("Error al guardar el producto: ${e.message}")
            loadProducts()
        }
    }

    fun delete(product: Product) = viewModelScope.launch {
        // Emitimos estado de carga al eliminar un producto
        _productsUiState.value = ProductUiState.Loading
        try {
            productRepository.deleteProduct(product.id)
            loadProducts()
        } catch (e: Exception) {
            _productsUiState.value = ProductUiState.Error("Error al eliminar el producto: ${e.message}")
            loadProducts()
        }
    }

    fun update(product: Product) = viewModelScope.launch {
        try {
            productRepository.updateProduct(product)
            loadProducts()
        } catch (e: Exception) {
            _productsUiState.value = ProductUiState.Error("Error al actualizar el producto: ${e.message}")
            loadProducts()
        }
    }
}