package com.appmovil.inventorywidget

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class InventoryApp : Application() {
    // Clase para Hilt, sirve como centro para repartir dependencias
    override fun onCreate() {
        super.onCreate()
    }
}