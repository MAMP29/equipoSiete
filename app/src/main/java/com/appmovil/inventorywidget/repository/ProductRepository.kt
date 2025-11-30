package com.appmovil.inventorywidget.repository

import com.appmovil.inventorywidget.model.Product

interface ProductRepository {
    suspend fun createProduct(uid: String, product: Product)
    suspend fun deleteProduct(uid: String, productId: String)
    suspend fun updateProduct(uid: String, product: Product)
    suspend fun getProductById(uid: String, productId: String): Product?
    suspend fun getUserProductList(uid: String): List<Product>?
}