package com.appmovil.inventorywidget.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appmovil.inventorywidget.model.AuthResult
import com.appmovil.inventorywidget.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState


    fun register(email: String, password: String) = viewModelScope.launch {
        _authState.value = AuthState.Loading

        val authResult = authRepository.register(email, password)

        if (authResult.isSuccess) {
            // TODO: Guardar usuario en la DB
            _authState.value = AuthState.Success
            login(email, password)
        } else {
            _authState.value = AuthState.Error(authResult.message?.toString() ?: "Error")
        }
    }

    fun login(email: String, password: String) = viewModelScope.launch {
        _authState.value = AuthState.Loading

        val authResult = authRepository.login(email, password)

        if (authResult.isSuccess) {
            _authState.value = AuthState.Success
        } else {
            _authState.value = AuthState.Error(authResult.message?.toString() ?: "Error")
        }
    }

    fun isLoggedIn(): Boolean {
        return authRepository.isLoggedIn()
    }

    fun logout() = viewModelScope.launch {
        _authState.value = AuthState.Idle
        authRepository.logout()
    }
}