package com.appmovil.inventorywidget.repository

import com.appmovil.inventorywidget.model.Product

interface ProductRepository {
    suspend fun saveProduct(product: Product)
    suspend fun deleteProduct(productId: String)
    suspend fun updateProduct(product: Product)
    suspend fun getProductById(productId: String): Product?
    suspend fun getUserProductList(): List<Product>
}