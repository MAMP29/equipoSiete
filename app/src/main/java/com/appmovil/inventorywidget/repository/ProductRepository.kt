package com.appmovil.inventorywidget.repository

import androidx.lifecycle.LiveData
import com.appmovil.inventorywidget.data.ProductDao
import com.appmovil.inventorywidget.model.Product
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val productDao: ProductDao
) {

    suspend fun getById(int: Int): Product? {
        return productDao.getProductById(int)
    }

    val allProducts: LiveData<List<Product>> = productDao.getProductList()

    suspend fun getProductListDirectly(): List<Product> {
        return productDao.getProductListDirectly()
    }

    suspend fun save(product: Product) {
        productDao.saveProduct(product)
    }

    suspend fun delete(product: Product) {
        productDao.deleteProduct(product)
    }

    suspend fun update(product: Product) {
        productDao.updateProduct(product)
    }
}