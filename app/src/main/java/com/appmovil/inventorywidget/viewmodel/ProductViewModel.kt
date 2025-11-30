package com.appmovil.inventorywidget.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.appmovil.inventorywidget.model.Product
import com.appmovil.inventorywidget.repository.ProductRepositoryOld
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: ProductRepositoryOld
) : ViewModel() {

    // Fuente del estado de la UI
    private val _productsUiState = MediatorLiveData<ProductUiState>()

    val productsUiState: LiveData<ProductUiState> = _productsUiState

    val allProducts: LiveData<List<Product>> = repository.allProducts

    init {
        _productsUiState.value = ProductUiState.Loading // Emitir estado de carga

        // Cuando tengamos todos los productos emitimos el estado de exito
        _productsUiState.addSource(allProducts) { products ->
            _productsUiState.value = ProductUiState.Success(products)
        }
    }

    fun getProductById(id: Int): LiveData<Product?> = liveData {
        val product = repository.getById(id)
        emit(product)
    }

    fun save(product: Product) = viewModelScope.launch {
        repository.save(product)
    }

    fun delete(product: Product) {

        // Emitimos estado de carga al eliminar un producto
        _productsUiState.value = ProductUiState.Loading

        viewModelScope.launch {
            repository.delete(product)
        }
    }

    fun update(product: Product) = viewModelScope.launch {
        repository.update(product)
    }
}