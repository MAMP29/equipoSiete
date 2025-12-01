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

    // INPUTS
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    // PASSWORD VISIBILITY
    private val _passwordVisible = MutableStateFlow(false)
    val passwordVisible: StateFlow<Boolean> = _passwordVisible

    // FORM VALIDATION
    private val _isFormValid = MutableStateFlow(false)
    val isFormValid: StateFlow<Boolean> = _isFormValid

    // AUTH STATE
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState


    // ---------------------------
    //     INPUT UPDATES
    // ---------------------------
    fun onEmailChange(value: String) {
        _email.value = value
        validateForm()
    }

    fun onPasswordChange(value: String) {
        _password.value = value
        validateForm()
    }

    private fun validateForm() {
        _isFormValid.value =
            _email.value.isNotBlank() &&
                    _password.value.length >= 6
    }


    // ---------------------------
    //   PASSWORD VISIBILITY
    // ---------------------------
    fun togglePasswordVisibility() {
        _passwordVisible.value = !_passwordVisible.value
    }


    // ---------------------------
    //        REGISTER
    // ---------------------------
    fun register(email: String, password: String) = viewModelScope.launch {

        _authState.value = AuthState.Loading

        val result = try {
            authRepository.register(email, password)
        } catch (e: Exception) {
            // En caso de excepción inesperada al llamar al repo
            _authState.value = AuthState.Error("Error en el registro", isRegisterError = true)
            return@launch
        }

        if (!result.isSuccess) {
            // Marca explícitamente que es un error de registro
            _authState.value = AuthState.Error(
                message = result.message ?: "Error en el registro",
                isRegisterError = true
            )
            return@launch
        }

        // Guardar perfil en Firestore
        try {
            val newUser = User(id = result.userId ?: "", email = email)
            userRepository.createUser(newUser)
        } catch (e: Exception) {
            _authState.value = AuthState.Error(
                message = "Usuario creado, pero falló guardar el perfil: ${e.message}",
                isRegisterError = true
            )
            return@launch
        }

        // Login automático después del registro
        login(email, password)
    }


    // ---------------------------
    //          LOGIN
    // ---------------------------
    fun login(email: String, password: String) = viewModelScope.launch {

        _authState.value = AuthState.Loading

        val result = try {
            authRepository.login(email, password)
        } catch (e: Exception) {
            // Error inesperado al llamar al repo
            _authState.value = AuthState.Error("Login incorrecto", isRegisterError = false)
            return@launch
        }

        if (result.isSuccess) {
            _authState.value = AuthState.Success
        } else {
            _authState.value = AuthState.Error(
                message = result.message ?: "Login incorrecto",
                isRegisterError = false
            )
        }
    }


    // ---------------------------
    //          LOGOUT
    // ---------------------------
    fun logout() {
        authRepository.logout()
        _authState.value = AuthState.Idle
    }
}

