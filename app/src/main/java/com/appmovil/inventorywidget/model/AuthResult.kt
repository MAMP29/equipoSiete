package com.appmovil.inventorywidget.model

data class AuthResult(
    val isSuccess: Boolean,
    val userId: String? = null,
    val message: String? = null
)