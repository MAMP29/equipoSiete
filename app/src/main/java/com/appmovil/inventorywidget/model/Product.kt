package com.appmovil.inventorywidget.model

import com.google.firebase.firestore.DocumentId

data class Product(
    @DocumentId
    val id: String = "",

    val code: Int = 0,

    val name: String = "",

    val price: Int = 0,

    val quantity: Int = 0
)
