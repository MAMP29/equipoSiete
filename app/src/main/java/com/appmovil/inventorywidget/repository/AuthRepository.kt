package com.appmovil.inventorywidget.repository
import com.appmovil.inventorywidget.model.AuthResult
import com.appmovil.inventorywidget.model.User

interface AuthRepository {
    fun isLoggedIn(): Boolean
    suspend fun register(email: String, password: String): AuthResult
    suspend fun login(email: String, password: String): AuthResult
    fun logout()
}