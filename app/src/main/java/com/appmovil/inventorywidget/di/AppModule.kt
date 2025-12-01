package com.appmovil.inventorywidget.di

import com.appmovil.inventorywidget.repository.AuthRepository
import com.appmovil.inventorywidget.repository.AuthRepositoryImp
import com.appmovil.inventorywidget.repository.FirebaseAuthSessionManager
import com.appmovil.inventorywidget.repository.ProductRepository
import com.appmovil.inventorywidget.repository.ProductRepositoryImp
import com.appmovil.inventorywidget.repository.SessionManager
import com.appmovil.inventorywidget.repository.UserRepository
import com.appmovil.inventorywidget.repository.UserRepositoryImp
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

//    @Provides
//    @Singleton
//    fun provideInventoryDatabase(
//        @ApplicationContext app: Context
//    ) = Room.databaseBuilder(
//        app,
//        InventoryDatabase::class.java,
//        "inventario_database"
//    ).build()
//
//    @Provides
//    @Singleton
//    fun provideProductDao(db: InventoryDatabase): ProductDao {
//        return db.productDao()
//    }

    @Provides
    @Singleton
    fun provideAuthRepository(auth: FirebaseAuth): AuthRepository {
        return AuthRepositoryImp(auth)
    }

    @Provides
    @Singleton
    fun provideProductRepository(db: FirebaseFirestore, firebaseAuthSessionManager: SessionManager): ProductRepository {
        return ProductRepositoryImp(db, firebaseAuthSessionManager)
    }

    @Provides
    @Singleton
    fun provideUserRepository(db: FirebaseFirestore): UserRepository {
        return UserRepositoryImp(db)
    }

    @Provides
    @Singleton
    fun provideSessionManager(firebaseAuth: FirebaseAuth): SessionManager {
        return FirebaseAuthSessionManager(firebaseAuth)
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