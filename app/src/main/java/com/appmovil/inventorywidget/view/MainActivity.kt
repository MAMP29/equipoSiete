package com.appmovil.inventorywidget.view

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.appmovil.inventorywidget.R
import com.appmovil.inventorywidget.utils.SessionManager
import com.appmovil.inventorywidget.viewmodel.AuthState
import com.appmovil.inventorywidget.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navigationContainer) as NavHostFragment
        val navController = navHostFragment.navController
        val navGraph = navController.navInflater.inflate(R.navigation.navigation)

        if (sessionManager.isLoggedIn()) {
            navGraph.setStartDestination(R.id.inventoryFragment)
        } else {
            navGraph.setStartDestination(R.id.loginFragment)
        }

        navController.graph = navGraph

        //testAuthFlow()
    }

    // FLUJO DE AUTENTICACION - SOLO PARA PRUEBAS
    private fun testAuthFlow() {
        val testEmail = "testuser@test.com"
        val testPassword = "12345678"

        authViewModel.logout()
        Log.d("AuthTest", "Sesión limpiada al inicio de la prueba.")

        lifecycleScope.launch {
            authViewModel.authState.collect { state ->
                when (state) {
                    is AuthState.Idle -> Log.d("AuthTest", "Estado: Idle")
                    is AuthState.Loading -> Log.d("AuthTest", "Estado: Loading...")
                    is AuthState.Success -> {
                        Log.d("AuthTest", "El flujo de autenticación funcionó.")
                    }
                    is AuthState.Error -> Log.e("AuthTest", "FALLO: ${state.message}")
                }
            }
        }

        // Prueba de Registro
        // Log.d("AuthTest", "Iniciando prueba de REGISTRO con $testEmail")
        // authViewModel.register(testEmail, testPassword)

        // Prueba de Login
        Log.d("AuthTest", "Iniciando prueba de LOGIN con $testEmail")
        authViewModel.login(testEmail, testPassword)
    }
}