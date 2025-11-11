package com.appmovil.inventorywidget

import android.app.Application
import com.airbnb.lottie.Lottie
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieConfig
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@HiltAndroidApp
class InventoryApp : Application() {
    // Clase para Hilt, sirve como centro para repartir dependencias

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        warmUpLottieCache()
    }

    private fun warmUpLottieCache() {
        val config = LottieConfig.Builder().build()
        Lottie.initialize(config)

        applicationScope.launch {
            LottieCompositionFactory.fromRawRes(this@InventoryApp, R.raw.inventory)
            LottieCompositionFactory.fromRawRes(this@InventoryApp, R.raw.fingerprint_animation)
        }
    }
}