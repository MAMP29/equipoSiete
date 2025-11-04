package com.appmovil.inventorywidget.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.appmovil.inventorywidget.model.Product

@Database(entities = [Product::class], version = 1)
abstract class InventoryDatabase : RoomDatabase() {
    abstract fun productDao() : ProductDao

    // Objeto singlenton
    companion object {
        @Volatile
        private var INSTANCE: InventoryDatabase? = null

        fun getDatabase(context: Context): InventoryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    InventoryDatabase::class.java,
                    "inventario_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}