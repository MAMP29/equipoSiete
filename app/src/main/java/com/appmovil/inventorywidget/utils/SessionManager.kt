package com.appmovil.inventorywidget.utils

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(@ApplicationContext context: Context) {

    private val prefs: SharedPreferences
    private val editor: SharedPreferences.Editor

    companion object {
        private const val PREFS_NAME = "app_session_prefs"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
    }

    init {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        editor = prefs.edit()
    }

    /**
     * Guarda el estado de la sesión.
     */
    fun saveSession(isLoggedIn: Boolean) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
        editor.apply()
    }

    /**
     * Comprueba si hay una sesión activa
     */
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    /**
     * Cierra la sesión del usuario
     */
    fun logout() {
        saveSession(false)
    }


}