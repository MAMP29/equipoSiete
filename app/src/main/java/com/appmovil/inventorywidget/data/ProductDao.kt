package com.appmovil.inventorywidget.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.appmovil.inventorywidget.model.Product

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProduct(product: Product)

    @Query("SELECT * FROM tabla_productos WHERE id=:id")
    suspend fun getProductById(id: Int): Product?

    @Query("SELECT * FROM tabla_productos")
    fun getProductList(): LiveData<List<Product>>

    @Query("SELECT * FROM tabla_productos")
    suspend fun getProductListDirectly(): List<Product>

    @Delete
    suspend fun deleteProduct(product: Product)

    @Update
    suspend fun updateProduct(product: Product)
}