package com.appmovil.inventorywidget.repository

import com.appmovil.inventorywidget.model.User

interface UserRepository {
    suspend fun createUser(user: User)
    suspend fun updateUser(user: User)
    suspend fun getUserById(uid: String): User?
}