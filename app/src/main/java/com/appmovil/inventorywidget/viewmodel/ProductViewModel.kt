package com.appmovil.inventorywidget.viewmodel

import androidx.lifecycle.LiveData
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
    private val repository: ProductRepository
) : ViewModel() {

    fun getProductById(id: Int): LiveData<Product?> = liveData {
        val product = repository.getById(id)
        emit(product)
    }

    val allProducts: LiveData<List<Product>> = repository.allProducts

    fun save(product: Product) = viewModelScope.launch {
        repository.save(product)
    }

    fun delete(product: Product) = viewModelScope.launch {
        repository.delete(product)
    }

    fun update(product: Product) = viewModelScope.launch {
        repository.update(product)
    }
}