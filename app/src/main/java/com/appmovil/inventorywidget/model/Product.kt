package com.appmovil.inventorywidget.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tabla_productos")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "codigo_producto")
    val code: Int,

    @ColumnInfo(name = "nombre_producto")
    val name: String,

    @ColumnInfo(name = "precio_producto")
    val price: Int,

    @ColumnInfo(name = "cantidad_inventario")
    val quantity: Int
)
