package com.appmovil.inventorywidget.repository

import com.appmovil.inventorywidget.model.User

interface UserRepository {
    suspend fun createUser(uid: String, user: User)
    suspend fun updateUser(uid: String, user: User)
    suspend fun getUserById(uid: String): User?
}