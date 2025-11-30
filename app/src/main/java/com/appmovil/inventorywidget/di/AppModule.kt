package com.appmovil.inventorywidget.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.appmovil.inventorywidget.data.InventoryDatabase
import com.appmovil.inventorywidget.data.ProductDao
import com.appmovil.inventorywidget.repository.AuthRepository
import com.appmovil.inventorywidget.repository.AuthRepositoryImp
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideInventoryDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        InventoryDatabase::class.java,
        "inventario_database"
    ).build()

    @Provides
    @Singleton
    fun provideProductDao(db: InventoryDatabase): ProductDao {
        return db.productDao()
    }

    @Provides
    @Singleton
    fun provideAuthRepository(auth: FirebaseAuth): AuthRepository {
        return AuthRepositoryImp(auth)
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseDb() : FirebaseFirestore {
        return Firebase.firestore
    }
}