package com.appmovil.inventorywidget.viewmodel

import com.appmovil.inventorywidget.model.Product

sealed class ProductUiState {
    object Loading : ProductUiState()
    data class Success(val products: List<Product>) : ProductUiState()
    data class Error(val error: String) : ProductUiState()
}