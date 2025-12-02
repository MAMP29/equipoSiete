package com.appmovil.inventorywidget.utils

import com.appmovil.inventorywidget.model.User

interface SessionManager {
    fun currentUser(): User?
    fun isLoggedIn(): Boolean
}