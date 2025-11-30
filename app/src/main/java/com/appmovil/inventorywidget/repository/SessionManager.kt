package com.appmovil.inventorywidget.repository

import com.appmovil.inventorywidget.model.User

interface SessionManager {
    fun currentUser(): User?
}