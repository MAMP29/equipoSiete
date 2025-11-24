package com.appmovil.inventorywidget.model

data class User(
    val id: String = "",
    val email: String = "",
    val products: List<Product> = emptyList()
)
