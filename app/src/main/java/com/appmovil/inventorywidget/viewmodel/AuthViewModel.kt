package com.appmovil.inventorywidget.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    // CAMPOS DE LA UI
    val email = MutableStateFlow("")
    val password = MutableStateFlow("")
    val passwordError = MutableStateFlow(false)

    // VISIBILIDAD DEL PASSWORD
    val passwordVisible = MutableStateFlow(false)

    // BOTONES
    val isLoginEnabled = MutableStateFlow(false)
    val isRegisterEnabled = MutableStateFlow(false)

    // ESTADO DE AUTENTICACIÓN
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState


    // -----------------------------------------------------
    // MANEJO DE CAMPOS
    // -----------------------------------------------------
    fun onEmailChange(text: String) {
        if (text.length <= 40) {
            email.value = text
            validateForm()
        }
    }

    fun onPasswordChange(text: String) {
        if (text.all { it.isDigit() } && text.length <= 10) {

            password.value = text

            // Error cuando entre 1..5 dígitos
            passwordError.value = text.length in 1..5

            validateForm()
        }
    }

    fun togglePasswordVisibility() {
        passwordVisible.value = !passwordVisible.value
    }


    // -----------------------------------------------------
    // VALIDACIONES HU 2.0
    // -----------------------------------------------------
    fun isPasswordValid(): Boolean {
        return !passwordError.value
    }

    fun canLogin(): Boolean {
        return email.value.isNotBlank() &&
                password.value.isNotBlank() &&
                !passwordError.value
    }

    private fun validateForm() {
        val valid = canLogin()
        isLoginEnabled.value = valid
        isRegisterEnabled.value = valid
    }


    // -----------------------------------------------------
    // LOGIN Y REGISTRO
    // -----------------------------------------------------
    fun login(email: String, pass: String) = viewModelScope.launch {
        _authState.value = AuthState.Loading

        val result = authRepository.login(email, pass)

        if (result.isSuccess) {
            _authState.value = AuthState.Success
        } else {
            _authState.value = AuthState.Error(result.message ?: "Error en login")
        }
    }

    fun register(email: String, pass: String) = viewModelScope.launch {
        _authState.value = AuthState.Loading

        val result = authRepository.register(email, pass)

        if (result.isSuccess) {
            _authState.value = AuthState.Success
            login(email, pass)
        } else {
            _authState.value = AuthState.Error(result.message ?: "Error en registro")
        }
    }


    // -----------------------------------------------------
    // SESIÓN
    // -----------------------------------------------------
    fun isLoggedIn(): Boolean = authRepository.isLoggedIn()

    fun logout() {
        authRepository.logout()
        _authState.value = AuthState.Idle
    }
}
