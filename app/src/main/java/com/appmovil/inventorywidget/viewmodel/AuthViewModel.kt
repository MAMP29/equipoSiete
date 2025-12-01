package com.appmovil.inventorywidget.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appmovil.inventorywidget.model.User
import com.appmovil.inventorywidget.repository.AuthRepository
import com.appmovil.inventorywidget.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState


    fun register(email: String, password: String) = viewModelScope.launch {
        _authState.value = AuthState.Loading

        val authResult = authRepository.register(email, password)

        if (authResult.isSuccess) {
            try {
                val newUser = User(id = authResult.userId.toString(), email = email)
                userRepository.createUser(newUser)
                _authState.value = AuthState.Success
                login(email, password)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(authResult.message?.toString() ?: "El usuario se creó, pero no se pudo guardar el perfil: ${e.message}")
            }
        } else {
            _authState.value = AuthState.Error(authResult.message?.toString() ?: "Error desconocido en el registro")
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


    fun logout() = viewModelScope.launch {
        _authState.value = AuthState.Idle
        authRepository.logout()
    }
}