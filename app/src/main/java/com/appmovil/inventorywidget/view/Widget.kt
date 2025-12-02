package com.appmovil.inventorywidget.view

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.content.edit
import com.appmovil.inventorywidget.R
import com.appmovil.inventorywidget.repository.ProductRepository
import com.appmovil.inventorywidget.utils.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject

private const val ACTION_TOGGLE_VISIBILITY = "com.appmovil.inventorywidget.ACTION_TOGGLE_VISIBILITY"

@AndroidEntryPoint
class Widget : AppWidgetProvider() {

    @Inject
    lateinit var productRepository: ProductRepository

    @Inject
    lateinit var sessionManager: SessionManager

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_TOGGLE_VISIBILITY) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, Widget::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)

            toggleVisibilityState(context)

            onUpdate(context, appWidgetManager, appWidgetIds)
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        // Obtenemos el layout del widget como un objeto RemoteViews
        val views = RemoteViews(context.packageName, R.layout.widget)

        // --- Configuración de los Clicks (PendingIntents) ---

        // Abre la MainActivity que dirige al Home del inventario
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            putExtra("NAVIGATE_TO", "INVENTORY_FRAGMENT")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val openAppPendingIntent = PendingIntent.getActivity(context, 0, openAppIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        views.setOnClickPendingIntent(R.id.ibManageInventory, openAppPendingIntent)


        // Envía un broadcast a este mismo provider
        val toggleVisibilityIntent = Intent(context, Widget::class.java).apply {
            action = ACTION_TOGGLE_VISIBILITY
        }
        val toggleVisibilityPendingIntent = PendingIntent.getBroadcast(context, 1, toggleVisibilityIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        views.setOnClickPendingIntent(R.id.ibEye, toggleVisibilityPendingIntent)


        // --- Lógica de Datos y UI ---
        coroutineScope.launch {
            // Calculamos el saldo desde el repositorio
            var totalBalance = 0.0
            val formatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO"))

            if(sessionManager.isLoggedIn()) {
                try {
                    val productList = productRepository.getUserProductList()
                    productList.forEach { product ->
                        totalBalance += product.price * product.quantity
                    }
                } catch (e: Exception) {
                    views.setTextViewText(R.id.tvBalanceStatus, "Error")
                }
            } else {
                views.setTextViewText(R.id.tvBalanceStatus, "Inicia sesión")
            }

            val finalBalance = formatter.format(totalBalance)

            // Verificando el estado de visibilidad
            val isBalanceVisible = getVisibilityState(context)

            if (isBalanceVisible) {
                views.setTextViewText(R.id.tvBalanceStatus, finalBalance)
                views.setImageViewResource(R.id.ibEye, R.drawable.outline_visibility_off_24)
            } else {
                views.setTextViewText(R.id.tvBalanceStatus, "$ ****")
                views.setImageViewResource(R.id.ibEye, R.drawable.outline_visibility_24)
            }

            // Aplica los cambios con appWidgetManager
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    private fun saveVisibilityState(context: Context, isVisible: Boolean) {
        val prefs = context.getSharedPreferences("WidgetPrefs", Context.MODE_PRIVATE)
        prefs.edit { putBoolean("balance_visible", isVisible) }
    }

    private fun getVisibilityState(context: Context): Boolean {
        val prefs = context.getSharedPreferences("WidgetPrefs", Context.MODE_PRIVATE)
        return prefs.getBoolean("balance_visible", false) // Oculto por defecto
    }

    private fun toggleVisibilityState(context: Context) {
        val currentState = getVisibilityState(context)
        saveVisibilityState(context, !currentState)
    }

}