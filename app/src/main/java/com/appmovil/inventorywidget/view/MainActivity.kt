package com.appmovil.inventorywidget.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.appmovil.inventorywidget.R
import com.appmovil.inventorywidget.utils.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

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
    }
}