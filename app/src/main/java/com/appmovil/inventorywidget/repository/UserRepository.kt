package com.appmovil.inventorywidget.repository

import com.appmovil.inventorywidget.model.User

interface UserRepository {
    suspend  fun createUser(uid: String, email: String)
    suspend fun updateUser(uid: String, email: String)
    suspend fun getUserById(uid: String): User?
}